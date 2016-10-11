package com.bachelor.stwagene.bluecheck.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.bachelor.stwagene.bluecheck.ListManagement.FirstExpandableDeviceValuesListAdapter;
import com.bachelor.stwagene.bluecheck.Main.MainActivity;
import com.bachelor.stwagene.bluecheck.Model.TISensorTagData;
import com.bachelor.stwagene.bluecheck.R;

/**
 * Created by stwagene on 30.05.2016.
 */
public class DeviceServicesListFragment extends Fragment
{

    public DeviceServicesListFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.device_services_list_fragment, container, false);

        ExpandableListView firstLevelList = (ExpandableListView) view.findViewById(R.id.ble_device_values_list);
        FirstExpandableDeviceValuesListAdapter adapter = new FirstExpandableDeviceValuesListAdapter(TISensorTagData.services, (MainActivity) getActivity());
        firstLevelList.setAdapter(adapter);

        return view;
    }

    @Override
    public void onDestroy()
    {
        ((MainActivity) getActivity()).setRssiPercentageVisible(false);
        super.onDestroy();
    }
}
