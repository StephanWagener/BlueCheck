package com.bachelor.stwagene.bluecheck.Bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;

import com.bachelor.stwagene.bluecheck.Main.MainActivity;
import com.bachelor.stwagene.bluecheck.Model.TISensorTagData;

import java.util.ArrayList;

/**
 * Created by stwagene on 30.08.2016.
 */
public class BluetoothMainCallback extends BluetoothGattCallback
{
    private final MainActivity activity;

    public BluetoothMainCallback (MainActivity activity)
    {
        this.activity = activity;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
    {
        activity.getHandler().writeToLog(getClass().getSimpleName() + ": Neuer Verbindungsstatus => " + newState);
        switch (newState)
        {
            case BluetoothProfile.STATE_CONNECTED:
                activity.getHandler().writeToLog(getClass().getSimpleName() + ": Das Device " + gatt.getDevice().getName() + "(" + gatt.getDevice().getAddress() + ") wurde verbunden.");
                gatt.discoverServices();
                break;
            case BluetoothProfile.STATE_DISCONNECTED:
                activity.getHandler().writeToLog(getClass().getSimpleName() + ": Die Verbindung zum Device " + gatt.getDevice().getName() + "(" + gatt.getDevice().getAddress() + ") wurde getrennt.");
                break;
            default:
                gatt.disconnect();
                activity.getHandler().writeToLog(getClass().getSimpleName() + ": Neuer Verbindungsstatus => " + newState);
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status)
    {
        TISensorTagData.services = (ArrayList<BluetoothGattService>) gatt.getServices();
        activity.getHandler().openDeviceValuesList();
    }
}
