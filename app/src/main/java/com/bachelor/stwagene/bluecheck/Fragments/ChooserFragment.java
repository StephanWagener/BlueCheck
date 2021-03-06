package com.bachelor.stwagene.bluecheck.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bachelor.stwagene.bluecheck.ListManagement.ChooserListAdapter;
import com.bachelor.stwagene.bluecheck.Main.MainActivity;
import com.bachelor.stwagene.bluecheck.Model.ChooserListItem;
import com.bachelor.stwagene.bluecheck.Model.ChooserListOption;
import com.bachelor.stwagene.bluecheck.R;

import java.util.ArrayList;

/**
 * Created by stwagene on 02.06.2016.
 */
public class ChooserFragment extends Fragment
{
    public ChooserFragment () {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.chooser_fragment, container, false);

        ListView list = (ListView) view.findViewById(R.id.items_to_choose);
        ArrayList<ChooserListItem> items = new ArrayList<>();
        items.add(new ChooserListItem(ChooserListOption.EVERYONE));
        items.add(new ChooserListItem(ChooserListOption.EVERY_FIFTH));
        items.add(new ChooserListItem(ChooserListOption.EVERY_TENTH));
        items.add(new ChooserListItem(ChooserListOption.EVERY_THIRTIETH));
        items.add(new ChooserListItem(ChooserListOption.EVERY_SIXTIETH));
        items.add(new ChooserListItem(ChooserListOption.ONLY_ONCE));
        final ChooserListAdapter adapter = new ChooserListAdapter((MainActivity) getActivity(), R.layout.chooser_list_item, items);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                SettingsFragment fragment = (SettingsFragment) getActivity().getSupportFragmentManager().findFragmentByTag(SettingsFragment.class.getSimpleName());
                if (fragment != null)
                {
                    fragment.setValueChangedInterval(adapter.getItem(position));
                }
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.remove(ChooserFragment.this);
                ft.commit();
            }
        });

        return view;
    }
}
