package com.bachelor.stwagene.bluecheck.Bluetooth;

import android.os.Handler;
import android.os.Looper;

import com.bachelor.stwagene.bluecheck.Fragments.DeviceValuesListFragment;
import com.bachelor.stwagene.bluecheck.Fragments.ProgressFragment;
import com.bachelor.stwagene.bluecheck.Main.MainActivity;

/**
 * Handles the messages between background and main thread. Also manage the start and stop of the BluetoothCallback scan.
 *
 * Created by stwagene on 03.05.2016.
 */
public class BluetoothHandler extends Handler
{
    private final MainActivity activity;

    public BluetoothHandler(MainActivity mainActivity)
    {
        super(Looper.getMainLooper());
        this.activity = mainActivity;
    }

    public void openDeviceValuesList()
    {
        this.post(new Runnable()
        {
            @Override
            public void run()
            {
                ProgressFragment fragment = (ProgressFragment) activity.getSupportFragmentManager().findFragmentByTag("ProgressFragment");
                if (fragment != null)
                {
                    fragment.close();
                }

                DeviceValuesListFragment deviceValueFragment = (DeviceValuesListFragment) activity.getSupportFragmentManager().findFragmentByTag("DeviceValuesListFragment");
                if (deviceValueFragment == null)
                {
                    activity.openFragment(new DeviceValuesListFragment(), "DeviceValuesListFragment");
                }
            }
        });
    }

    public void refreshProgressFragment(final String message)
    {
        this.post(new Runnable()
        {
            @Override
            public void run()
            {
                activity.refreshLoadFragment(message);
            }
        });
    }

    public void setRssiPercentageValue(final int rssiValue)
    {
        this.post(new Runnable()
        {
            @Override
            public void run()
            {
                activity.setRssiPercentageValue(rssiValue);
            }
        });
    }

    public void writeToLog(final String s)
    {
        this.post(new Runnable()
        {
            @Override
            public void run()
            {
                activity.writeToLog(s);
            }
        });
    }
}
