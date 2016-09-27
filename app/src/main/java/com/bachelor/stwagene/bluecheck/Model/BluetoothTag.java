package com.bachelor.stwagene.bluecheck.Model;

import android.bluetooth.BluetoothDevice;

/**
 * Model to save and manage the data of found Bluetooth devices.
 *
 * Created by stwagene on 02.05.2016.
 */
public class BluetoothTag
{
    private String name;
    private BluetoothDevice device;
    private String address;
    private DeliveryProduct product;

    public BluetoothTag(String name, BluetoothDevice device, String address, DeliveryProduct product)
    {
        this.product = product;
        this.device = device;
        this.name = name;
        this.address = address;
    }

    public BluetoothTag(String name, BluetoothDevice device, String address)
    {
        this.device = device;
        this.name = name;
        this.address = address;
    }

    public BluetoothTag(String name, String address)
    {
        this.address = address;
        this.name = name;
    }

    public BluetoothTag () {}

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

    public DeliveryProduct getProduct()
    {
        return product;
    }

    public void setProduct(DeliveryProduct product)
    {
        this.product = product;
    }
}
