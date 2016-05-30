package com.bachelor.stwagene.bluecheck.Bluetooth;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.bachelor.stwagene.bluecheck.Fragments.ShowProgressFragment;
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

    @Override
    public void handleMessage(final Message msg)
    {
        this.post(new Runnable()
        {
            @Override
            public void run()
            {
                String value = (String) msg.getData().get("VALUE");
                try
                {
                    double doubleValue = Double.parseDouble(value);
                    ShowProgressFragment progressFragment = (ShowProgressFragment) activity.getSupportFragmentManager().findFragmentByTag("ShowProgressFragment");
                    if (progressFragment != null)
                    {
                        progressFragment.close();
                    }
                    activity.setValue(doubleValue);
                }
                catch (Exception ex)
                {
                    activity.writeToLog("Error beim Setzen des Wertes des DeviceValueFragments.");
                    ex.printStackTrace();
                }
            }
        });
        super.handleMessage(msg);
    }
}
