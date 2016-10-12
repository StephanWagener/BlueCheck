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
import com.bachelor.stwagene.bluecheck.Model.ChooserListOption;
import com.bachelor.stwagene.bluecheck.R;

/**
 * Created by stwagene on 02.06.2016.
 */
public class SettingsFragment extends Fragment
{
    public static final String IS_DEVELOPER_MODE = "developer_mode_key";
    public static final String VALUE_CHANGED_INTERVAL = "value_changed_interval";
    public static final String IS_SHOW_UUID = "shown_log_text";
    public static final String CURRENT_DELIVERY = "current_delivery";

    private TextView valueChangedInterval;
    private LinearLayout developerLayout;
    private CheckBox developerMode;
    private TextView deliveryText;

    private ChooserListItem valueChangedIntervalItem = new ChooserListItem(ChooserListOption.EVERYONE);
    private boolean isDeveloperMode = true;
    private boolean isShowUUID = true;
    private String currentDelivery = "";

    public SettingsFragment () {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);

        initSettings();

        final CheckBox showUUID = (CheckBox) view.findViewById(R.id.show_UUID);
        final CheckBox showText = (CheckBox) view.findViewById(R.id.show_text);

        showUUID.setChecked(isShowUUID);
        showText.setChecked(!isShowUUID);

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
        valueChangedInterval.setText(valueChangedIntervalItem.getText());

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
        developerMode.setChecked(isDeveloperMode);
        developerMode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideDeveloperSettings();
            }
        });
        hideDeveloperSettings();

        deliveryText = (EditText) view.findViewById(R.id.delivery_id_text);
        deliveryText.setHint(currentDelivery);

        Button save = (Button) view.findViewById(R.id.save_settings);
        save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean changed = false;

                if (showUUID.isChecked() != isShowUUID)
                {
                    setShowUuid(showUUID.isChecked());
                    changed = true;
                }

                if (developerMode.isChecked() != isDeveloperMode)
                {
                    setDeveloperMode(developerMode.isChecked());
                    changed = true;
                }

                if (valueChangedIntervalItem.getValue() != ((MainActivity) getActivity()).getSharedPreferences().getInt(VALUE_CHANGED_INTERVAL, 1))
                {
                    ((MainActivity) getActivity()).getSharedPreferences().edit().putInt(VALUE_CHANGED_INTERVAL, valueChangedIntervalItem.getValue()).apply();
                    changed = true;
                }

                String text = deliveryText.getText().toString();
                if (!text.equals(currentDelivery))
                {
                    if (((MainActivity) getActivity()).getCurrentDeliveries().contains(text))
                    {
                        setCurrentDelivery(text);
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
                isShowUUID = true;
                showText.setChecked(false);
                developerMode.setChecked(true);
                isDeveloperMode = true;
                valueChangedIntervalItem = new ChooserListItem(ChooserListOption.EVERYONE);
                valueChangedInterval.setText(valueChangedIntervalItem.getText());
                resetSettings();
            }
        });

        return view;
    }

    public void setValueChangedInterval(ChooserListItem item)
    {
        valueChangedInterval.setText(item.getText());
        valueChangedIntervalItem = item;
    }

    public void hideDeveloperSettings()
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

    private void initSettings()
    {
        isDeveloperMode = ((MainActivity) getActivity()).getSharedPreferences().getBoolean(IS_DEVELOPER_MODE, true);
        isShowUUID = ((MainActivity) getActivity()).getSharedPreferences().getBoolean(IS_SHOW_UUID, true);
        setValueChangedInterval(((MainActivity) getActivity()).getSharedPreferences().getInt(VALUE_CHANGED_INTERVAL, 1));
        currentDelivery = ((MainActivity) getActivity()).getSharedPreferences().getString(CURRENT_DELIVERY, "");
    }

    private void resetSettings()
    {
        setDeveloperMode(true);
        setValueChangedInterval(1);
        setShowUuid(true);
    }

    private void setDeveloperMode(boolean enable)
    {
        isDeveloperMode = enable;
        ((MainActivity) getActivity()).getSharedPreferences().edit().putBoolean(IS_DEVELOPER_MODE, isDeveloperMode).apply();
        OptionsFragment optionsFragment = (OptionsFragment) getActivity().getSupportFragmentManager().findFragmentByTag(OptionsFragment.class.getSimpleName());
        if (optionsFragment != null)
        {
            optionsFragment.setDeveloperMode();
        }
        hideDeveloperSettings();
    }

    private void setShowUuid(boolean enable)
    {
        isShowUUID = enable;
        ((MainActivity) getActivity()).getSharedPreferences().edit().putBoolean(IS_SHOW_UUID, isShowUUID).apply();
    }

    private void setValueChangedInterval(int value)
    {
        switch (value)
        {
            case 0:
                valueChangedIntervalItem = new ChooserListItem(ChooserListOption.ONLY_ONCE);
                break;
            case 1:
                valueChangedIntervalItem = new ChooserListItem(ChooserListOption.EVERYONE);
                break;
            case 5:
                valueChangedIntervalItem = new ChooserListItem(ChooserListOption.EVERY_FIFTH);
                break;
            case 10:
                valueChangedIntervalItem = new ChooserListItem(ChooserListOption.EVERY_TENTH);
                break;
            case 30:
                valueChangedIntervalItem = new ChooserListItem(ChooserListOption.EVERY_THIRTIETH);
                break;
            case 60:
                valueChangedIntervalItem = new ChooserListItem(ChooserListOption.EVERY_SIXTIETH);
                break;
        }
    }

    private void setCurrentDelivery(String delivery)
    {
        currentDelivery = delivery;
        deliveryText.setHint(currentDelivery);
        deliveryText.setText("");
        ((MainActivity) getActivity()).getSharedPreferences().edit().putString(CURRENT_DELIVERY, currentDelivery).apply();
    }
}
