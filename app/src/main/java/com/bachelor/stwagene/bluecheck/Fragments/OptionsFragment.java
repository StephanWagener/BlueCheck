package com.bachelor.stwagene.bluecheck.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.bachelor.stwagene.bluecheck.ListManagement.OptionsListAdapter;
import com.bachelor.stwagene.bluecheck.Main.MainActivity;
import com.bachelor.stwagene.bluecheck.Model.Option;
import com.bachelor.stwagene.bluecheck.Model.OptionType;
import com.bachelor.stwagene.bluecheck.R;

import java.util.ArrayList;

/**
 * Created by stwagene on 12.05.2016.
 */
public class OptionsFragment extends Fragment
{
    private OptionsListAdapter adapter;

    public OptionsFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.options_fragment, container, false);

        ListView list = (ListView) view.findViewById(R.id.options);

        adapter = new OptionsListAdapter((MainActivity) getActivity(), R.layout.option_list_item, getOptionsList());
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                OptionType currentType = adapter.getItem(position).getType();
                if (currentType.equals(OptionType.EXIT))
                {
                    getActivity().finish();
                }
                else if (currentType.equals(OptionType.LOG))
                {
                    Bundle args = new Bundle();
                    args.putStringArrayList(LogFragment.LOG_FRAGMENT_ARGUMENT_TEXT, ((MainActivity) getActivity()).getLogTexts());
                    LogFragment logFragment = new LogFragment();
                    logFragment.setArguments(args);
                    ((MainActivity) getActivity()).openFragment(logFragment);
                }
                else if (currentType.equals(OptionType.SETTINGS))
                {
                    ((MainActivity) getActivity()).openFragment(new SettingsFragment());
                }
                else
                {
                    getDialog().show();
                }
            }
        });

        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.options_layout);
        layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    private ArrayList<Option> getOptionsList()
    {
        ArrayList<Option> optionsList = new ArrayList<>();
        optionsList.add(new Option("Einstellungen", R.drawable.ic_settings_black_36dp, OptionType.SETTINGS));
        if (((MainActivity) getActivity()).getSharedPreferences().getBoolean(SettingsFragment.IS_DEVELOPER_MODE, true))
        {
            optionsList.add(new Option("Log-Ansicht", R.drawable.ic_description_black_36dp, OptionType.LOG));
        }
        optionsList.add(new Option("Zurücksetzen", R.drawable.ic_restore_black_36dp, OptionType.RESET));
        optionsList.add(new Option("Beenden", R.drawable.ic_close_black_36dp, OptionType.EXIT));
        return optionsList;
    }

    public void setDeveloperMode()
    {
        adapter.clear();
        adapter.addAll(getOptionsList());
        adapter.notifyDataSetChanged();
    }

    private AlertDialog.Builder getDialog ()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Soll die Applikation wirklich in den Anfangszustand zurückgesetzt werden?");
        builder.setPositiveButton("Zurücksetzen",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        ((MainActivity) getActivity()).reset();
                    }
                });
        builder.setNegativeButton("Abbrechen",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, final int i)
                    {
                        getActivity().onBackPressed();
                    }
                });
        builder.create();
        return builder;
    }
}
