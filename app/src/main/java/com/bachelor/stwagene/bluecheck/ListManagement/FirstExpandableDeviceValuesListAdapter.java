package com.bachelor.stwagene.bluecheck.ListManagement;

import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bachelor.stwagene.bluecheck.Main.MainActivity;
import com.bachelor.stwagene.bluecheck.Model.TexasInstrumentsUtils;
import com.bachelor.stwagene.bluecheck.R;

import java.util.ArrayList;

/**
 * Created by stwagene on 30.05.2016.
 */
public class FirstExpandableDeviceValuesListAdapter extends BaseExpandableListAdapter
{
    private final ArrayList<BluetoothGattService> services;
    private final MainActivity activity;
    private ArrayList<FirstLevelViewHolder> holders = new ArrayList<>();

    public FirstExpandableDeviceValuesListAdapter(ArrayList<BluetoothGattService> services, MainActivity activity)
    {
        this.services = services;
        this.activity = activity;
    }

    @Override
    public void onGroupCollapsed(int groupPosition)
    {
        if (this.services.get(groupPosition).getCharacteristics().size() != 0)
        {
            holders.get(groupPosition).arrow.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_36dp));
        }

        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition)
    {
        if (this.services.get(groupPosition).getCharacteristics().size() != 0)
        {
            holders.get(groupPosition).arrow.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_36dp));
        }

        super.onGroupExpanded(groupPosition);
    }

    @Override
    public int getGroupCount()
    {
        return this.services.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return this.services.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return this.services.get(groupPosition).getCharacteristics();
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
        FirstLevelViewHolder holder;
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.first_device_value_list_item, parent, false);
            holder = new FirstLevelViewHolder();
            holder.nameTextView = (TextView) convertView.findViewById(R.id.device_value_name);
            holder.UUIDTextView = (TextView) convertView.findViewById(R.id.device_value_UUID);
            holder.arrow = (ImageView) convertView.findViewById(R.id.arrow);
            convertView.setTag(holder);
            holders.add(holder);
        }
        else
        {
            holder = (FirstLevelViewHolder) convertView.getTag();
        }

        if (currentService.getUuid().toString().equals(TexasInstrumentsUtils.UUID_STRING_SERVICE_TEMPERATURE))
        {
            holder.nameTextView.setText("IR Temperature Service");
        }
        else if (currentService.getUuid().toString().equals(TexasInstrumentsUtils.UUID_STRING_SERVICE_HUMIDITY))
        {
            holder.nameTextView.setText("Humidity Service");
        }
        else if (currentService.getUuid().toString().equals(TexasInstrumentsUtils.UUID_STRING_SERVICE_PRESSURE))
        {
            holder.nameTextView.setText("Barometric Pressure Service");
        }
        else if (currentService.getUuid().toString().equals(TexasInstrumentsUtils.UUID_STRING_SERVICE_LIGHT_INTENSITY))
        {
            holder.nameTextView.setText("Light Intensity Service");
        }
        else
        {
            holder.nameTextView.setText("Service");
        }

        holder.UUIDTextView.setText(currentService.getUuid().toString());

        if (currentService.getCharacteristics().size() == 0)
        {
            holder.arrow.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        CustomExpandableListView view = new CustomExpandableListView(activity.getApplicationContext());
        view.setGroupIndicator(null);
        view.setChildDivider(new ColorDrawable(activity.getResources().getColor(R.color.colorPrimary)));
        view.setPadding(50,0,0,0);
        SecondExpandableDeviceValuesListAdapter adapter = new SecondExpandableDeviceValuesListAdapter(this.services.get(groupPosition).getCharacteristics(), activity);
        view.setAdapter(adapter);
        return view;
    }

    private static class FirstLevelViewHolder
    {
        private TextView nameTextView;
        private TextView UUIDTextView;
        private ImageView arrow;
    }
}
