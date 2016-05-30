package com.bachelor.stwagene.bluecheck.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bachelor.stwagene.bluecheck.Main.MainActivity;
import com.bachelor.stwagene.bluecheck.R;

/**
 * Created by stwagene on 03.05.2016.
 */
public class DeviceValueFragment extends Fragment
{
    private TextView deviceValue;
    private double value;

    public DeviceValueFragment () {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.device_value, container, false);

        deviceValue = (TextView) view.findViewById(R.id.value);
        TextView deviceName = (TextView) view.findViewById(R.id.device_name);

        deviceValue.setText(String.format("%.2f °C", getArguments().getDouble("VALUE")));
        deviceName.setText(getArguments().getString("NAME"));

        Button send = (Button) view.findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((MainActivity) getActivity()).sendData(String.valueOf(value).replace(',','.'));
            }
        });

        return view;
    }

    public void setValue(double value)
    {
        this.value = value;
        deviceValue.setText(String.format("%.2f °C", value));
    }
}
