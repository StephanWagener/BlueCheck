package com.bachelor.stwagene.bluecheck.Model;

import android.bluetooth.BluetoothGattService;

import java.util.ArrayList;

/**
 * Created by stwagene on 10.05.2016.
 */
public class TISensorTagData
{
    public static ArrayList<BluetoothGattService> services = new ArrayList<>();
    private static double objectTemperature = 0;
    private static double ambientTemperature = 0;
    private static double humidity = 0;
    private static double pressure = 0;
    private static double lightIntensity = 0;

    public TISensorTagData() {}

    public static double getObjectTemperature()
    {
        return objectTemperature;
    }

    public static void setObjectTemperature(double objectTemperature)
    {
        TISensorTagData.objectTemperature = objectTemperature;
    }

    public static double getAmbientTemperature()
    {
        return ambientTemperature;
    }

    public static void setAmbientTemperature(double ambientTemperature)
    {
        TISensorTagData.ambientTemperature = ambientTemperature;
    }

    public static double getHumidity()
    {
        return humidity;
    }

    public static void setHumidity(double humidity)
    {
        TISensorTagData.humidity = humidity;
    }

    public static void setPressure(double pressure)
    {
        TISensorTagData.pressure = pressure;
    }

    public static double getPressure()
    {
        return pressure;
    }

    public static double getLightIntensity()
    {
        return lightIntensity;
    }

    public static void setLightIntensity(double lightIntensity)
    {
        TISensorTagData.lightIntensity = lightIntensity;
    }
}
