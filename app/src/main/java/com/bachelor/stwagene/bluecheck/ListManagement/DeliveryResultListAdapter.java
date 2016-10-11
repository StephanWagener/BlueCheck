package com.bachelor.stwagene.bluecheck.ListManagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bachelor.stwagene.bluecheck.Main.MainActivity;
import com.bachelor.stwagene.bluecheck.Model.DeliveryResultListItem;
import com.bachelor.stwagene.bluecheck.R;

import java.util.ArrayList;

/**
 * Created by stwagene on 02.06.2016.
 */
public class DeliveryResultListAdapter extends ArrayAdapter<DeliveryResultListItem>
{
    private final ArrayList<DeliveryResultListItem> items;
    private final MainActivity activity;

    public DeliveryResultListAdapter(MainActivity activity, int resource, ArrayList<DeliveryResultListItem> objects)
    {
        super(activity.getApplicationContext(), resource, objects);
        this.items = objects;
        this.activity = activity;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        DeliveryResultListItem currentItem = this.items.get(position);
        final ListItemViewHolder holder;
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.delivery_result_packages_list_item, parent, false);
            holder = new ListItemViewHolder();
            holder.itemCount = (TextView) convertView.findViewById(R.id.delivery_result_list_count);
            holder.itemName = (TextView) convertView.findViewById(R.id.delivery_result_list_name);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ListItemViewHolder) convertView.getTag();
        }

        holder.itemName.setText(currentItem.getText());
        switch (currentItem.getType())
        {
            case MainActivity.FALSE_LOADED_PRODUCT:
                holder.itemName.setTextColor(activity.getResources().getColor(R.color.red));
                break;
            case MainActivity.RIGHT_LOADED_PRODUCT:
                holder.itemName.setTextColor(activity.getResources().getColor(R.color.green));
                break;
            case MainActivity.MISSING_PRODUCT:
                holder.itemName.setTextColor(activity.getResources().getColor(R.color.red));
                break;
            case MainActivity.UNKNOWN_PRODUCT:
                holder.itemName.setTextColor(activity.getResources().getColor(R.color.grey));
                break;
            default:
                break;
        }

        holder.itemCount.setText(currentItem.getNumber()+"");

        return convertView;
    }

    private static class ListItemViewHolder
    {
        TextView itemCount;
        TextView itemName;
    }
}
