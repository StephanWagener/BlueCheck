package com.bachelor.stwagene.bluecheck.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bachelor.stwagene.bluecheck.Main.MainActivity;
import com.bachelor.stwagene.bluecheck.Model.ChooserListItem;
import com.bachelor.stwagene.bluecheck.R;

/**
 * Created by stwagene on 02.06.2016.
 */
public class SettingsFragment extends Fragment
{
    private TextView valueChangedInterval;
    private LinearLayout developerLayout;
    private CheckBox developerMode;

    public SettingsFragment () {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);

        final CheckBox showUUID = (CheckBox) view.findViewById(R.id.show_UUID);
        final CheckBox showText = (CheckBox) view.findViewById(R.id.show_text);

        showUUID.setChecked(((MainActivity)getActivity()).isShowUUIDInLog());
        showText.setChecked(!((MainActivity)getActivity()).isShowUUIDInLog());

        showUUID.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (showUUID.isChecked())
                {
                    showText.setChecked(false);
                }
                else
                {
                    showText.setChecked(true);
                }
            }
        });

        showText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (showText.isChecked())
                {
                    showUUID.setChecked(false);
                }
                else
                {
                    showUUID.setChecked(true);
                }
            }
        });

        valueChangedInterval = (TextView) view.findViewById(R.id.value_changed_interval_text);
        valueChangedInterval.setText(((MainActivity)getActivity()).getValueChangedInterval().getText());

        //TODO handle chooser auswahl mit speichern Button
        Button changeShowValueChanged = (Button) view.findViewById(R.id.change_show_value_changed_interval);
        changeShowValueChanged.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((MainActivity)getActivity()).openFragment(new ChooserFragment());
            }
        });

        developerLayout = (LinearLayout) view.findViewById(R.id.developer_settings);
        developerMode = (CheckBox) view.findViewById(R.id.change_developer_mode);
        developerMode.setChecked(((MainActivity)getActivity()).isDeveloperMode());
        developerMode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setDeveloperMode();
                OptionsFragment optionsFragment = (OptionsFragment) getActivity().getSupportFragmentManager().findFragmentByTag(OptionsFragment.class.getSimpleName());
                if (optionsFragment != null)
                {
                    optionsFragment.setDeveloperMode();
                }
            }
        });
        setDeveloperMode();

        final EditText deliveryText = (EditText) view.findViewById(R.id.delivery_id_text);
        deliveryText.setHint(((MainActivity) getActivity()).getDeliveryID());

        Button save = (Button) view.findViewById(R.id.save_settings);
        save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MainActivity act = ((MainActivity) getActivity());
                boolean changed = false;

                if (showUUID.isChecked() != act.isShowUUIDInLog())
                {
                    act.setShowUUIDInLog(showUUID.isChecked());
                    changed = true;
                }
                if (developerMode.isChecked() != act.isDeveloperMode())
                {
                    act.setDeveloperMode(developerMode.isChecked());
                    changed = true;
                }
                String text = deliveryText.getText().toString();
                if (!text.equals(act.getDeliveryID()))
                {
                    if (text.equals("ABCD1234") || text.equals("7890VBNM"))
                    {
                        act.setDeliveryID(text);
                        deliveryText.setHint(text);
                        deliveryText.setText("");
                        changed = true;
                    }
                    else if (!text.trim().isEmpty())
                    {
                        Toast.makeText(getActivity().getApplicationContext(), "Keine gültige Lieferungskennung.", Toast.LENGTH_SHORT).show();
                    }
                }

                if (changed)
                {
                    Toast.makeText(getActivity().getApplicationContext(), "Erfolgreich gespeichert.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button reset = (Button) view.findViewById(R.id.reset_settings);
        reset.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showUUID.setChecked(true);
                showText.setChecked(false);
                developerMode.setChecked(true);
                deliveryText.setHint("ABCD1234");
                valueChangedInterval.setText("Jeder");
                ((MainActivity) getActivity()).setShowUUIDInLog(showUUID.isChecked());
                ((MainActivity) getActivity()).setDeveloperMode(developerMode.isChecked());
                ((MainActivity) getActivity()).setDeliveryID(deliveryText.getText().toString());
                ((MainActivity) getActivity()).setValueChangedInterval(new ChooserListItem(1, "Jeder"));
            }
        });

        return view;
    }

    public void setValueChangedInterval(ChooserListItem item)
    {
        valueChangedInterval.setText(item.getText());
    }

    public void setDeveloperMode()
    {
        if (!developerMode.isChecked())
        {
            developerLayout.setVisibility(View.GONE);
        }
        else
        {
            developerLayout.setVisibility(View.VISIBLE);
        }
    }
}
