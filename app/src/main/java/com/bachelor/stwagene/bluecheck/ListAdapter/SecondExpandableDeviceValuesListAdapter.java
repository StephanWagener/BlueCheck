package com.bachelor.stwagene.bluecheck.ListAdapter;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.bachelor.stwagene.bluecheck.Main.MainActivity;
import com.bachelor.stwagene.bluecheck.R;

import java.util.List;

/**
 * Created by stwagene on 30.05.2016.
 */
public class SecondExpandableDeviceValuesListAdapter extends BaseExpandableListAdapter
{
    private final List<BluetoothGattCharacteristic> characteristics;
    private final MainActivity activity;

    public SecondExpandableDeviceValuesListAdapter(List<BluetoothGattCharacteristic> characteristics, MainActivity activity)
    {
        this.characteristics = characteristics;
        this.activity = activity;
    }

    @Override
    public int getGroupCount()
    {
        return this.characteristics.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return this.characteristics.get(groupPosition).getDescriptors().size();
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return this.characteristics.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return this.characteristics.get(groupPosition).getDescriptors().get(childPosition);
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
        BluetoothGattCharacteristic currentCharacteristic = this.characteristics.get(groupPosition);
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.device_value_list_item, parent, false);
        }
        TextView firstLevelName = (TextView) convertView.findViewById(R.id.device_value_name);
        firstLevelName.setText("Characteristik");

        TextView firstLevelUUID = (TextView) convertView.findViewById(R.id.device_value_UUID);
        firstLevelUUID.setText(currentCharacteristic.getUuid().toString());

        TextView firstLevelPermission = (TextView) convertView.findViewById(R.id.device_value_permission);
        if(currentCharacteristic.getPermissions() == BluetoothGattCharacteristic.PERMISSION_READ
                || currentCharacteristic.getPermissions() == BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED
                || currentCharacteristic.getPermissions() == BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED_MITM)
        {
            firstLevelPermission.setText("Lesen");
        }
        else if(currentCharacteristic.getPermissions() == BluetoothGattCharacteristic.PERMISSION_WRITE
                || currentCharacteristic.getPermissions() == BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED
                || currentCharacteristic.getPermissions() == BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED_MITM
                || currentCharacteristic.getPermissions() == BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED
                || currentCharacteristic.getPermissions() == BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED_MITM)
        {
            firstLevelPermission.setText("Schreiben");
        }
        else
        {
            firstLevelPermission.setText("Keine");
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        BluetoothGattDescriptor currentDescriptor = this.characteristics.get(groupPosition).getDescriptors().get(childPosition);
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.device_value_list_item, parent, false);
        }
        TextView firstLevelName = (TextView) convertView.findViewById(R.id.device_value_name);
        firstLevelName.setText("Descriptor");

        TextView firstLevelUUID = (TextView) convertView.findViewById(R.id.device_value_UUID);
        firstLevelUUID.setText(currentDescriptor.getUuid().toString());

        TextView firstLevelPermission = (TextView) convertView.findViewById(R.id.device_value_permission);
        if(currentDescriptor.getPermissions() == BluetoothGattCharacteristic.PERMISSION_READ
                || currentDescriptor.getPermissions() == BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED
                || currentDescriptor.getPermissions() == BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED_MITM)
        {
            firstLevelPermission.setText("Lesen");
        }
        else if(currentDescriptor.getPermissions() == BluetoothGattCharacteristic.PERMISSION_WRITE
                || currentDescriptor.getPermissions() == BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED
                || currentDescriptor.getPermissions() == BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED_MITM
                || currentDescriptor.getPermissions() == BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED
                || currentDescriptor.getPermissions() == BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED_MITM)
        {
            firstLevelPermission.setText("Schreiben");
        }
        else
        {
            firstLevelPermission.setText("Keine");
        }
        return convertView;
    }
}
