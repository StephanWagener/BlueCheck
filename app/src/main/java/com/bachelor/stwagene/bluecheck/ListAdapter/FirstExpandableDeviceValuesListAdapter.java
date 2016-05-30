package com.bachelor.stwagene.bluecheck.ListAdapter;

import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.bachelor.stwagene.bluecheck.Main.MainActivity;
import com.bachelor.stwagene.bluecheck.Model.TISensorTagData;
import com.bachelor.stwagene.bluecheck.R;

import java.util.ArrayList;

/**
 * Created by stwagene on 30.05.2016.
 */
public class FirstExpandableDeviceValuesListAdapter extends BaseExpandableListAdapter
{
    private final ArrayList<BluetoothGattService> services;
    private final MainActivity activity;

    public FirstExpandableDeviceValuesListAdapter(ArrayList<BluetoothGattService> services, MainActivity activity)
    {
        this.services = services;
        this.activity = activity;
    }

    @Override
    public int getGroupCount()
    {
        return this.services.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return this.services.get(groupPosition).getCharacteristics().size();
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return this.services.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return this.services.get(groupPosition).getCharacteristics().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        BluetoothGattService currentService = this.services.get(groupPosition);
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.device_value_list_item, parent, false);
        }
        TextView firstLevelName = (TextView) convertView.findViewById(R.id.device_value_name);
        firstLevelName.setText("Service");

        TextView firstLevelUUID = (TextView) convertView.findViewById(R.id.device_value_UUID);
        firstLevelUUID.setText(currentService.getUuid().toString());

        TextView firstLevelPermission = (TextView) convertView.findViewById(R.id.device_value_permission);
        firstLevelPermission.setVisibility(View.GONE);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.device_values_list, parent, false);
        }

        SecondExpandableDeviceValuesListAdapter adapter = new SecondExpandableDeviceValuesListAdapter(TISensorTagData.services.get(groupPosition).getCharacteristics(), activity);
        ((ExpandableListView)convertView).setAdapter(adapter);

        return convertView;
    }
}
