package com.bachelor.stwagene.bluecheck.Model;

import android.bluetooth.BluetoothGattService;

import java.util.ArrayList;

/**
 * Created by stwagene on 10.05.2016.
 */
public class TISensorTagData
{
    public static ArrayList<BluetoothGattService> services = new ArrayList<>();
    private double lastObjectTemperature = 0;
    private double lastAmbientTemperature = 0;

    public TISensorTagData() {}

    public double getLastObjectTemperature()
    {
        return lastObjectTemperature;
    }

    public void setLastObjectTemperature(double lastObjectTemperature)
    {
        this.lastObjectTemperature = lastObjectTemperature;
    }

    public double getLastAmbientTemperature()
    {
        return lastAmbientTemperature;
    }

    public void setLastAmbientTemperature(double lastAmbientTemperature)
    {
        this.lastAmbientTemperature = lastAmbientTemperature;
    }
}
