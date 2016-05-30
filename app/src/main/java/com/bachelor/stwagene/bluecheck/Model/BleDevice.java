package com.bachelor.stwagene.bluecheck.Model;

import android.bluetooth.BluetoothDevice;

/**
 * Model to saved and manage the data of found Bluetooth devices.
 *
 * Created by stwagene on 02.05.2016.
 */
public class BleDevice
{
    private String name;
    private BluetoothDevice device;

    public BleDevice(String name, BluetoothDevice device)
    {
        this.device = device;
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public BluetoothDevice getDevice()
    {
        return device;
    }

    public void setDevice(BluetoothDevice device)
    {
        this.device = device;
    }
}
