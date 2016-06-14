package com.bachelor.stwagene.bluecheck.ListManagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bachelor.stwagene.bluecheck.Main.MainActivity;
import com.bachelor.stwagene.bluecheck.Model.Option;
import com.bachelor.stwagene.bluecheck.R;

import java.util.ArrayList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by stwagene on 12.05.2016.
 */
public class OptionsListAdapter extends ArrayAdapter<Option>
{
    private final LayoutInflater inflater;
    private final ArrayList<Option> items;
    private final MainActivity activity;

    public OptionsListAdapter (MainActivity activity, int resource, ArrayList<Option> items)
    {
        super(activity.getApplicationContext(), resource, items);
        this.inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        this.items = items;
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = inflater.inflate(R.layout.option_list_item, parent, false);
        Option option = items.get(position);

        ImageView image = (ImageView) view.findViewById(R.id.option_image);
        TextView name = (TextView) view.findViewById(R.id.option_name);

        image.setBackground(activity.getResources().getDrawable(option.getIconID()));
        name.setText(option.getName());

        return view;
    }
}
