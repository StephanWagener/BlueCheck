package com.bachelor.stwagene.bluecheck.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bachelor.stwagene.bluecheck.ListManagement.DeviceValuesListAdapter;
import com.bachelor.stwagene.bluecheck.Main.MainActivity;
import com.bachelor.stwagene.bluecheck.Model.BleDeviceValue;
import com.bachelor.stwagene.bluecheck.Model.TISensorTagData;
import com.bachelor.stwagene.bluecheck.R;

import java.util.ArrayList;

/**
 * Created by stwagene on 14.06.2016.
 */
public class DeviceValuesListFragment extends Fragment
{
    private DeviceValuesListAdapter adapter;

    public DeviceValuesListFragment () {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.device_values_list_fragment, container, false);
        ListView deviceValues = (ListView) view.findViewById(R.id.device_values_list);

        ArrayList<BleDeviceValue> items = new ArrayList<>();
        items.add(new BleDeviceValue("Temperatur", "°C", TISensorTagData.getAmbientTemperature()));
        items.add(new BleDeviceValue("Licht Intensität", "Lux", TISensorTagData.getLightIntensity()));
        items.add(new BleDeviceValue("Luftdruck", "hPa", TISensorTagData.getPressure()));
        items.add(new BleDeviceValue("Luftfeuchtigkeit", "%", TISensorTagData.getHumidity()));

        adapter = new DeviceValuesListAdapter((MainActivity) getActivity(), R.layout.device_value_list_item, items);
        deviceValues.setAdapter(adapter);

        refreshData();

        return view;
    }

    public void refreshData()
    {
        adapter.refresh();
    }
}
