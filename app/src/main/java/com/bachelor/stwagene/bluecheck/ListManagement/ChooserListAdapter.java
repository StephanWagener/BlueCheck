package com.bachelor.stwagene.bluecheck.ListManagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bachelor.stwagene.bluecheck.Main.MainActivity;
import com.bachelor.stwagene.bluecheck.Model.ChooserListItem;
import com.bachelor.stwagene.bluecheck.R;

import java.util.ArrayList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by stwagene on 02.06.2016.
 */
public class ChooserListAdapter extends ArrayAdapter<ChooserListItem>
{
    private final ArrayList<ChooserListItem> items;
    private final LayoutInflater inflater;

    public ChooserListAdapter(MainActivity activity, int resource, ArrayList<ChooserListItem> objects)
    {
        super(activity.getApplicationContext(), resource, objects);
        this.items = objects;
        this.inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        final View view = inflater.inflate(R.layout.chooser_list_item, parent, false);
        ChooserListItem listItem = items.get(position);

        final TextView name = (TextView) view.findViewById(R.id.chooser_list_item_text);
        name.setText(listItem.getText());

        return view;
    }
}
