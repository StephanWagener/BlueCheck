package com.bachelor.stwagene.bluecheck.ListManagement;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bachelor.stwagene.bluecheck.Fragments.SettingsFragment;
import com.bachelor.stwagene.bluecheck.Main.MainActivity;
import com.bachelor.stwagene.bluecheck.Model.BluetoothTag;
import com.bachelor.stwagene.bluecheck.R;

import java.util.ArrayList;

/**
 * Insert the data of the found Bluetooth-Devices into the device list in the main layout.
 *
 * Created by stwagene on 02.05.2016.
 */
public class DevicesListAdapter extends ArrayAdapter<BluetoothTag>
{
    private final ArrayList<BluetoothTag> items;
    private final MainActivity activity;

    public DevicesListAdapter (MainActivity activity, int resource, ArrayList<BluetoothTag> items)
    {
        super(activity.getApplicationContext(), resource, items);
        this.items = items;
        this.activity = activity;
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        BluetoothTag currentDevice = this.items.get(position);
        final ListItemViewHolder holder;
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.device_list_item, parent, false);
            holder = new ListItemViewHolder();
            holder.productDimensions = (TextView) convertView.findViewById(R.id.device_product_dimen);
            holder.productID = (TextView) convertView.findViewById(R.id.device_category_product);
            holder.productReceiver = (TextView) convertView.findViewById(R.id.device_product_receiver);
            holder.productState = (TextView) convertView.findViewById(R.id.device_product_state);
            holder.bluetoothName = (TextView) convertView.findViewById(R.id.device_name);
            holder.bluetoothAddress = (TextView) convertView.findViewById(R.id.device_address);
            holder.deviceLayout = (LinearLayout) convertView.findViewById(R.id.device_info);
            holder.deviceBluetoothLayout = (LinearLayout) convertView.findViewById(R.id.device_bluetooth_info);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ListItemViewHolder) convertView.getTag();
        }

        if (currentDevice.getProduct() != null)
        {
            String stateText = "";
            Drawable drawable = null;
            switch (currentDevice.getProduct().getLoadStatus())
            {
                case MainActivity.FALSE_LOADED_PRODUCT:
                    stateText = "Falsch";
                    drawable = this.activity.getResources().getDrawable(R.drawable.rounded_stroked_box_red);
                    break;
                case MainActivity.RIGHT_LOADED_PRODUCT:
                    stateText = "Richtig";
                    drawable = this.activity.getResources().getDrawable(R.drawable.rounded_stroked_box_green);
                    break;
                case MainActivity.MISSING_PRODUCT:
                    stateText = "Fehlt";
                    drawable = this.activity.getResources().getDrawable(R.drawable.rounded_stroked_box_red);
                    break;
                case MainActivity.UNKNOWN_PRODUCT:
                    stateText = "Unbekannt";
                    drawable = this.activity.getResources().getDrawable(R.drawable.rounded_stroked_box_grey);
                    break;
                default:
                    drawable = this.activity.getResources().getDrawable(R.drawable.rounded_stroked_box);
                    break;
            }
            holder.deviceLayout.setBackground(drawable);
            holder.productID.setText("Ware - " + currentDevice.getProduct().getId());
            holder.productState.setText(stateText);
            holder.productReceiver.setText(currentDevice.getProduct().getRecipient());
            holder.productDimensions.setText(currentDevice.getProduct().getDimensions());
        }

        //TODO reagieren auf das Drücken eines Elementes
        /*holder.deviceLayout.setTag(position);
        holder.deviceLayout.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    holder.deviceLayout.setBackground(activity.getResources().getDrawable(R.drawable.rounded_stroked_box_grey));
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_SCROLL)
                {
                    holder.deviceLayout.setBackground(activity.getResources().getDrawable(R.drawable.rounded_stroked_box));
                    activity.performDeviceListItemClick(getItem((int) holder.deviceLayout.getTag()));
                    return true;
                }
                return false;
            }
        });*/

        holder.deviceLayout.setTag(position);
        holder.deviceLayout.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    activity.performDeviceListItemClick(getItem((int) holder.deviceLayout.getTag()));
                    return true;
                }
                return false;
            }
        });

        holder.bluetoothName.setText(currentDevice.getName());
        holder.bluetoothAddress.setText(currentDevice.getAddress());
        if (activity.getSharedPreferences().getBoolean(SettingsFragment.IS_DEVELOPER_MODE, true))
        {
            holder.deviceBluetoothLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.deviceBluetoothLayout.setVisibility(View.GONE);
        }

        return convertView;
    }

    public ArrayList<BluetoothTag> getItems()
    {
        return items;
    }

    static class ListItemViewHolder
    {
        private TextView productDimensions;
        private TextView productID;
        private TextView productReceiver;
        private TextView productState;
        private TextView bluetoothName;
        private TextView bluetoothAddress;
        private LinearLayout deviceLayout;
        private LinearLayout deviceBluetoothLayout;
    }
}
