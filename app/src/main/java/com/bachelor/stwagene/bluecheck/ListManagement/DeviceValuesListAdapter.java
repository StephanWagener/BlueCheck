package com.bachelor.stwagene.bluecheck.ListManagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bachelor.stwagene.bluecheck.Main.MainActivity;
import com.bachelor.stwagene.bluecheck.Model.BleDeviceValue;
import com.bachelor.stwagene.bluecheck.Model.TISensorTagData;
import com.bachelor.stwagene.bluecheck.R;

import java.util.ArrayList;

/**
 * Created by stwagene on 14.06.2016.
 */
public class DeviceValuesListAdapter extends ArrayAdapter<BleDeviceValue>
{
    private final ArrayList<BleDeviceValue> items;
    private ArrayList<ViewHolder> holders = new ArrayList<>();
    private final MainActivity activity;

    public DeviceValuesListAdapter (MainActivity activity, int resource, ArrayList<BleDeviceValue> items)
    {
        super(activity.getApplicationContext(), resource, items);
        this.activity = activity;
        this.items = items;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        BleDeviceValue listItem = items.get(position);
        ViewHolder holder;
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.device_value_list_item, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.device_value_name);
            holder.value = (TextView) convertView.findViewById(R.id.device_value);
            convertView.setTag(holder);
            holders.add(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(listItem.getName());
        holder.value.setText(listItem.getCompleteText());

        return convertView;
    }

    public void refresh()
    {
        if (!holders.isEmpty())
        {
            for (int i = 0; i < items.size(); i++)
            {
                if (i == 0)
                {
                    items.get(i).setValue(TISensorTagData.getAmbientTemperature());
                }
                if (i == 1)
                {
                    items.get(i).setValue(TISensorTagData.getLightIntensity());
                }
                if (i == 2)
                {
                    items.get(i).setValue(TISensorTagData.getPressure());
                }
                if (i == 3)
                {
                    items.get(i).setValue(TISensorTagData.getHumidity());
                }
            }
            for (int i = 0; i < holders.size(); i++)
            {
                holders.get(i).value.setText(items.get(i).getCompleteText());
            }
            notifyDataSetChanged();
        }
    }

    private static class ViewHolder
    {
        TextView name;
        TextView value;
    }
}
