package com.bachelor.stwagene.bluecheck.ListAdapter;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bachelor.stwagene.bluecheck.Main.MainActivity;
import com.bachelor.stwagene.bluecheck.Model.BleDevice;
import com.bachelor.stwagene.bluecheck.R;

import java.util.ArrayList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Insert the data of the found Bluetooth-Devices into the device list in the main layout.
 *
 * Created by stwagene on 02.05.2016.
 */
public class DevicesListAdapter extends ArrayAdapter<BleDevice>
{
    private final LayoutInflater inflater;
    private final ArrayList<BleDevice> items;
    private final MainActivity activity;

    public DevicesListAdapter (MainActivity activity, int resource, ArrayList<BleDevice> items)
    {
        super(activity.getApplicationContext(), resource, items);
        this.inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        this.items = items;
        this.activity = activity;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        final View view = inflater.inflate(R.layout.device_list_item, parent, false);
        BleDevice listItem = items.get(position);

        final TextView name = (TextView) view.findViewById(R.id.device_name);
        name.setText(listItem.getName());

        final TextView address = (TextView) view.findViewById(R.id.device_address);
        address.setText(listItem.getDevice().getAddress());

        final LinearLayout layout = (LinearLayout) view.findViewById(R.id.device_info);
        layout.setTag(position);
        layout.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    layout.setBackground(activity.getResources().getDrawable(R.drawable.clicked_rounded_box));
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    layout.setBackground(activity.getResources().getDrawable(R.drawable.rounded_box));
                    activity.performDeviceListItemClick((int) layout.getTag());
                    return true;
                }
                return false;
            }
        });

        return view;
    }

    public boolean contains(String name)
    {
        for (BleDevice device : items)
        {
            if (device.getName().equals(name))
            {
                return true;
            }
        }
        return false;
    }
}
