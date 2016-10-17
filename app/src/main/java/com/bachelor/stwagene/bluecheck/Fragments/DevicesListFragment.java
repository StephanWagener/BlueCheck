package com.bachelor.stwagene.bluecheck.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bachelor.stwagene.bluecheck.ListManagement.DevicesListAdapter;
import com.bachelor.stwagene.bluecheck.Main.MainActivity;
import com.bachelor.stwagene.bluecheck.Model.BluetoothTag;
import com.bachelor.stwagene.bluecheck.R;

import java.util.ArrayList;

/**
 * Created by stwagene on 24.08.2016.
 */
public class DevicesListFragment extends Fragment
{
    private DevicesListAdapter deviceListAdapter;


    public DevicesListFragment() {}

    @Override
    public void onResume()
    {
        ((MainActivity) getActivity()).setButtonBarVisibility(true);
        ((MainActivity) getActivity()).setBackButtonVisible(true);
        refreshDeviceList(((MainActivity)getActivity()).getDevices());
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.devices_list_fragment, container, false);

        initDeviceList(view);

        initData();

        return view;
    }

    private void initData()
    {
        deviceListAdapter.addAll(((MainActivity)getActivity()).getDevices());
        deviceListAdapter.notifyDataSetChanged();
    }

    private void initDeviceList(View view)
    {
        ListView devicesList = (ListView) view.findViewById(R.id.ble_device_list);
        deviceListAdapter = new DevicesListAdapter(((MainActivity) getActivity()), R.layout.device_list_item, new ArrayList<BluetoothTag>());

        if (devicesList != null)
        {
            devicesList.setAdapter(deviceListAdapter);
        }
    }

    public void clearDeviceList()
    {
        deviceListAdapter.clear();
        deviceListAdapter.notifyDataSetChanged();
    }

    public void refreshDeviceList(ArrayList<BluetoothTag> tags)
    {
        deviceListAdapter.clear();
        deviceListAdapter.addAll(tags);
        deviceListAdapter.notifyDataSetChanged();
    }
}
