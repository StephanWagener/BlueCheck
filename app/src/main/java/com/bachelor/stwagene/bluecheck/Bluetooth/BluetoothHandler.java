package com.bachelor.stwagene.bluecheck.Bluetooth;

import android.os.Handler;
import android.os.Looper;

import com.bachelor.stwagene.bluecheck.Fragments.DeviceServicesListFragment;
import com.bachelor.stwagene.bluecheck.Fragments.DeviceValuesListFragment;
import com.bachelor.stwagene.bluecheck.Fragments.SettingsFragment;
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
                activity.closeProgressFragment();
                if (activity.getSharedPreferences().getBoolean(SettingsFragment.IS_DEVELOPER_MODE, true))
                {
                    DeviceServicesListFragment deviceServiceFragment = (DeviceServicesListFragment) activity.getSupportFragmentManager().findFragmentByTag(DeviceServicesListFragment.class.getSimpleName());
                    if (deviceServiceFragment == null)
                    {
                        activity.openFragment(new DeviceServicesListFragment());
                    }
                }
                else
                {
                    DeviceValuesListFragment deviceValueFragment = (DeviceValuesListFragment) activity.getSupportFragmentManager().findFragmentByTag(DeviceValuesListFragment.class.getSimpleName());
                    if (deviceValueFragment == null)
                    {
                        activity.openFragment(new DeviceValuesListFragment());
                    }
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
                activity.newProgress(message);
            }
        });
    }

    public void refreshDeviceValuesFragment()
    {
        this.post(new Runnable()
        {
            @Override
            public void run()
            {
                DeviceValuesListFragment deviceValueFragment = (DeviceValuesListFragment) activity.getSupportFragmentManager().findFragmentByTag(DeviceValuesListFragment.class.getSimpleName());
                if (deviceValueFragment != null)
                {
                    deviceValueFragment.refreshData();
                }
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
