package com.bachelor.stwagene.bluecheck.Bluetooth;

import android.bluetooth.BluetoothGattCallback;

import com.bachelor.stwagene.bluecheck.Main.MainActivity;

/**
 * Created by stwagene on 17.10.2016.
 */

public class BluetoothCallbackFactory
{
    private BluetoothCallbackFactory() {}

    public static BluetoothGattCallback getBluetoothCallback(BluetoothCallbackType type, MainActivity activity)
    {
        switch (type)
        {
            case MAIN:
                return new BluetoothMainCallback(activity);
            case TEXAS_INSTRUMENTS:
                return new BluetoothTexasInstrumentsCallback(activity);
            default:
                return new BluetoothMainCallback(activity);
        }
    }

    public enum BluetoothCallbackType
    {
        MAIN,
        TEXAS_INSTRUMENTS;
    }
}
