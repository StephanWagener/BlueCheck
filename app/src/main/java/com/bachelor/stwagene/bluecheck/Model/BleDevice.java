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
    private String address;

    public BleDevice(String name, BluetoothDevice device, String address)
    {
        this.device = device;
        this.name = name;
        this.address = address;
    }

    public BleDevice(String name, String address)
    {
        this.address = address;
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

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
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
