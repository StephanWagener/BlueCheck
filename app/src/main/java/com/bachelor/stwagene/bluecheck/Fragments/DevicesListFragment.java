package com.bachelor.stwagene.bluecheck.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bachelor.stwagene.bluecheck.ListManagement.DevicesListAdapter;
import com.bachelor.stwagene.bluecheck.Main.MainActivity;
import com.bachelor.stwagene.bluecheck.Model.BleDevice;
import com.bachelor.stwagene.bluecheck.R;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by stwagene on 24.08.2016.
 */
public class DevicesListFragment extends Fragment
{
    private Button sendIdList;
    private LinearLayout buttonBar;
    private Button scanTwo;
    private DevicesListAdapter deviceListAdapter;
    private boolean isScanOneFinished = false;
    private ImageView backButton;
    private ActionBar actionBar;
    private TextView rssiPercentageTextView;
    private static final String DEVICE_NAME_LIST_TEXT = "deviceNameList";
    private static final String DEVICE_ADDRESS_LIST_TEXT = "deviceAddressList";

    public DevicesListFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.devices_list_fragment, container, false);

        buttonBar = (LinearLayout) view.findViewById(R.id.button_bar_scans);
        scanTwo = (Button) view.findViewById(R.id.button_scan_two);

        initActionBar();

        initToolbar();

        initButtonScans(view);

        initDeviceList(view);

        initSendButton(view);

        initData(savedInstanceState);

        return view;
    }

    private void initData(Bundle savedBundle)
    {
        deviceListAdapter.addAll(((MainActivity)getActivity()).getDevices());
        deviceListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop()
    {
        ((MainActivity)getActivity()).setDevices(deviceListAdapter.getItems());
        super.onStop();
    }

    private void initActionBar()
    {
        actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setElevation(0);
            actionBar.setCustomView(R.layout.toolbar_layout);
        }
        setButtonBarElevation(true);
    }

    private void initToolbar()
    {
        backButton = (ImageView) actionBar.getCustomView().findViewById(R.id.back_icon);
        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getActivity().onBackPressed();
            }
        });

        ImageView menu = (ImageView) actionBar.getCustomView().findViewById(R.id.menu_icon);
        menu.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((MainActivity)getActivity()).writeToLog("Menü-Icon angeklickt.");
                OptionsFragment fragment = (OptionsFragment) getActivity().getSupportFragmentManager().findFragmentByTag("OptionsFragment");
                if (fragment != null)
                {
                    getActivity().onBackPressed();
                }
                else
                {
                    ((MainActivity) getActivity()).openFragment(new OptionsFragment(), "OptionsFragment");
                }

            }
        });

        rssiPercentageTextView = (TextView) actionBar.getCustomView().findViewById(R.id.connection_rssi_percentage);
    }

    private void initSendButton(View view)
    {
        sendIdList = (Button) view.findViewById(R.id.send_id_list);
        setVisibility(this.sendIdList, ((MainActivity) getActivity()).isDeveloperMode());
        sendIdList.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((MainActivity) getActivity()).initPut();
            }
        });
    }

    private void initButtonScans(View view)
    {
        Button scanOne = (Button) view.findViewById(R.id.button_scan_one);

        scanOne.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                scanTwo.setTextColor(getResources().getColor(R.color.grey));
                ((MainActivity) getActivity()).writeToLog("Scan 1 Button wurde gedrückt.");
                isScanOneFinished = false;
                ProgressFragment fragment = new ProgressFragment();
                Bundle bundle = new Bundle();
                bundle.putString("PROGRESS", "Scanne nach Geräten...");
                fragment.setArguments(bundle);
                ((MainActivity) getActivity()).openFragment(fragment, "ProgressFragment");
                deviceListAdapter.clear();
                deviceListAdapter.notifyDataSetChanged();
                ((MainActivity) getActivity()).startBleScan();
            }
        });

        scanTwo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isScanOneFinished)
                {
                    Toast.makeText(getActivity().getApplicationContext(), "Noch nicht verfügbar.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getActivity().getApplicationContext(), "Scan 1 wurde noch nicht ausgeführt.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initDeviceList(View view)
    {
        ListView devicesList = (ListView) view.findViewById(R.id.ble_device_list);
        deviceListAdapter = new DevicesListAdapter(((MainActivity) getActivity()), R.layout.device_list_item, new ArrayList<BleDevice>());

        if (devicesList != null)
        {
            devicesList.setAdapter(deviceListAdapter);
        }
    }

    public void setSendButtonVisibility(boolean visible)
    {
        setVisibility(this.sendIdList, visible);
    }

    public void setButtonBarVisibility(boolean visible)
    {
        setVisibility(this.buttonBar, visible);
    }

    public void setButtonBarElevation(boolean elevate)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if (elevate)
            {
                buttonBar.setElevation(30);
            }
            else
            {
                buttonBar.setElevation(0);
            }
        }
    }

    public void setScanOneFinished()
    {
        isScanOneFinished = true;
        scanTwo.setTextColor(getResources().getColor(android.R.color.white));
    }

    public ArrayList<String> getDeviceIdList()
    {
        ArrayList<String> deviceIdList = new ArrayList<>();
        for (int i = 0; i < deviceListAdapter.getItems().size(); i++)
        {
            deviceIdList.add(deviceListAdapter.getItems().get(i).getAddress());
        }
        //TODO entfernen der dummies
        if (deviceIdList.size() < 3)
        {
            deviceIdList.add("00:11:22:AA:BB:CC");
            deviceIdList.add("10:20:30:40:50:60");
            deviceIdList.add("CC:BB:AA:FF:EE:DD");
            deviceIdList.add("00:11:00:11:00:11");
        }
        return deviceIdList;
    }

    public void addDevice(BleDevice bleDevice)
    {
        deviceListAdapter.add(bleDevice);
        deviceListAdapter.notifyDataSetChanged();
    }

    public void setBackButtonVisibility(boolean visible)
    {
        setVisibility(this.backButton, visible);
    }

    public void setRssiValueVisibility(boolean visible)
    {
        setVisibility(this.rssiPercentageTextView, visible);
    }

    public void setVisibility(View view, boolean visible)
    {
        if (visible)
        {
            view.setVisibility(View.VISIBLE);
        }
        else
        {
            view.setVisibility(View.GONE);
        }
    }

    public void setRssiPercentageValue(int rssiValue)
    {
        if (this.rssiPercentageTextView.getVisibility() != View.VISIBLE)
        {
            setRssiValueVisibility(true);
        }

        if (rssiValue < -100)
        {
            this.rssiPercentageTextView.setText(1+"%");
        }
        else if (rssiValue > -25)
        {
            this.rssiPercentageTextView.setText(100+"%");
        }
        else
        {
            this.rssiPercentageTextView.setText(String.format(Locale.GERMAN, "%.1f", ((double)rssiValue+100.0)/75.0*100.0)+"%");
        }
    }
}
