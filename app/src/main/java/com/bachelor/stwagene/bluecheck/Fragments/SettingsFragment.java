package com.bachelor.stwagene.bluecheck.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

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
                ((MainActivity) getActivity()).setShowUUIDInLog(showUUID.isChecked());
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
                ((MainActivity) getActivity()).setShowUUIDInLog(!showText.isChecked());
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
        setDeveloperMode();

        final CheckBox developerMode = (CheckBox) view.findViewById(R.id.change_developer_mode);
        developerMode.setChecked(((MainActivity)getActivity()).isDeveloperMode());
        developerMode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((MainActivity)getActivity()).setDeveloperMode(developerMode.isChecked());
                SettingsFragment fragment = (SettingsFragment) getActivity().getSupportFragmentManager().findFragmentByTag(SettingsFragment.class.getSimpleName());
                if (fragment != null)
                {
                    fragment.setDeveloperMode();
                }
                OptionsFragment optionsFragment = (OptionsFragment) getActivity().getSupportFragmentManager().findFragmentByTag(OptionsFragment.class.getSimpleName());
                if (optionsFragment != null)
                {
                    optionsFragment.setDeveloperMode();
                }
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
        if (!((MainActivity) getActivity()).isDeveloperMode())
        {
            developerLayout.setVisibility(View.GONE);
        }
        else
        {
            developerLayout.setVisibility(View.VISIBLE);
        }
    }
}
