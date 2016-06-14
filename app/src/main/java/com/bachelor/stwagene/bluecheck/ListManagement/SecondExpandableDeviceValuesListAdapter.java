package com.bachelor.stwagene.bluecheck.ListManagement;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bachelor.stwagene.bluecheck.Main.MainActivity;
import com.bachelor.stwagene.bluecheck.Model.TISensorTagData;
import com.bachelor.stwagene.bluecheck.Model.TexasInstrumentsUtils;
import com.bachelor.stwagene.bluecheck.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by stwagene on 30.05.2016.
 */
public class SecondExpandableDeviceValuesListAdapter extends BaseExpandableListAdapter
{
    private final List<BluetoothGattCharacteristic> characteristics;
    private final MainActivity activity;
    private ArrayList<SecondLevelViewHolder> secondLevelHolder = new ArrayList<>();

    public SecondExpandableDeviceValuesListAdapter(List<BluetoothGattCharacteristic> characteristics, MainActivity activity)
    {
        this.characteristics = characteristics;
        this.activity = activity;
    }

    @Override
    public void onGroupCollapsed(int groupPosition)
    {
        if (this.characteristics.get(groupPosition).getDescriptors().size() != 0)
        {
            secondLevelHolder.get(groupPosition).arrow.setBackground(activity.getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_36dp));
        }

        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition)
    {
        if (this.characteristics.get(groupPosition).getDescriptors().size() != 0)
        {
            secondLevelHolder.get(groupPosition).arrow.setBackground(activity.getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_36dp));
        }

        super.onGroupExpanded(groupPosition);
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
        SecondLevelViewHolder holder;
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.second_device_value_list_item, parent, false);
            holder = new SecondLevelViewHolder();
            holder.nameTextView = (TextView) convertView.findViewById(R.id.device_value_name);
            holder.UUIDTextView = (TextView) convertView.findViewById(R.id.device_value_UUID);
            holder.permissionTextView = (TextView) convertView.findViewById(R.id.device_value_permission);
            holder.valueTextView = (TextView) convertView.findViewById(R.id.device_value_value);
            holder.arrow = (ImageView) convertView.findViewById(R.id.arrow);
            convertView.setTag(holder);
            secondLevelHolder.add(holder);
        }
        else
        {
            holder = (SecondLevelViewHolder) convertView.getTag();
        }

        holder.nameTextView.setText("Characteristik");

        holder.UUIDTextView.setText(currentCharacteristic.getUuid().toString());


        ArrayList<String> permissions = new ArrayList<>();
        int property = currentCharacteristic.getProperties();
        if(property == BluetoothGattCharacteristic.PROPERTY_WRITE
                || property == BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE
                || property == BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE)
        {
            permissions.add("Schreiben");
        }
        if(property == BluetoothGattCharacteristic.PROPERTY_READ)
        {
            permissions.add("Lesen");
        }
        if(property == BluetoothGattCharacteristic.PROPERTY_BROADCAST)
        {
            permissions.add("Broadcast");
        }
        if(property == BluetoothGattCharacteristic.PROPERTY_NOTIFY)
        {
            permissions.add("Notifications");
        }
        if(property == BluetoothGattCharacteristic.PROPERTY_INDICATE)
        {
            permissions.add("Kennzeichen");
        }
        if(property == BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS)
        {
            permissions.add("Erweiterte Berechtigung");
        }
        if (permissions.size() == 0)
        {
            permissions.add("Keine");
        }
        String permission = "";
        for (int i = 0; i < permissions.size(); i++)
        {
            permission += permissions.get(i);
            if (i != permissions.size()-1)
            {
                permission += ", ";
            }
        }
        holder.permissionTextView.setText(permission);


        if (currentCharacteristic.getUuid().toString().equals(TexasInstrumentsUtils.UUID_STRING_CHARACTERISTIC_TEMPERATURE_DATA))
        {
            holder.valueTextView.setText(TISensorTagData.getAmbientTemperature()+" °C");
        }
        else if (currentCharacteristic.getUuid().toString().equals(TexasInstrumentsUtils.UUID_STRING_CHARACTERISTIC_HUMIDITY_DATA))
        {
            holder.valueTextView.setText(TISensorTagData.getHumidity()+" %");
        }
        else if (currentCharacteristic.getUuid().toString().equals(TexasInstrumentsUtils.UUID_STRING_CHARACTERISTIC_PRESSURE_DATA))
        {
            holder.valueTextView.setText(TISensorTagData.getPressure()+" hPa");
        }
        else if (currentCharacteristic.getUuid().toString().equals(TexasInstrumentsUtils.UUID_STRING_CHARACTERISTIC_LIGHT_INTENSITY_DATA))
        {
            holder.valueTextView.setText(TISensorTagData.getLightIntensity()+" Lux");
        }
        else if (currentCharacteristic.getValue() != null)
        {
            holder.valueTextView.setText(Arrays.toString(currentCharacteristic.getValue()));
        }
        else
        {
            holder.valueTextView.setText("null");
        }

        if (currentCharacteristic.getDescriptors().size() == 0)
        {
            holder.arrow.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        BluetoothGattDescriptor currentDescriptor = this.characteristics.get(groupPosition).getDescriptors().get(childPosition);
        ThirdLevelViewHolder holder;
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.third_device_value_list_item, parent, false);
            holder = new ThirdLevelViewHolder();
            holder.nameTextView = (TextView) convertView.findViewById(R.id.device_value_name);
            holder.UUIDTextView = (TextView) convertView.findViewById(R.id.device_value_UUID);
            holder.valueTextView = (TextView) convertView.findViewById(R.id.device_value_value);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ThirdLevelViewHolder) convertView.getTag();
        }

        holder.nameTextView.setText("Descriptor");

        holder.UUIDTextView.setText(currentDescriptor.getUuid().toString());

        if (currentDescriptor.getValue() != null)
        {
            holder.valueTextView.setText(Arrays.toString(currentDescriptor.getValue()));
        }
        else
        {
            holder.valueTextView.setText("null");
        }

        return convertView;
    }

    static class SecondLevelViewHolder
    {
        private TextView nameTextView;
        private TextView UUIDTextView;
        private TextView permissionTextView;
        private TextView valueTextView;
        private ImageView arrow;
    }

    static class ThirdLevelViewHolder
    {
        private TextView nameTextView;
        private TextView UUIDTextView;
        private TextView valueTextView;
    }
}
