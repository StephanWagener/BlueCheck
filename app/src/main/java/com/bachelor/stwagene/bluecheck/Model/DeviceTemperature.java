package com.bachelor.stwagene.bluecheck.Model;

/**
 * Created by stwagene on 08.08.2016.
 */
public class DeviceTemperature
{
    private TempType tempType;

    public DeviceTemperature() {}

    public DeviceTemperature(TempType tempType)
    {
        this.tempType = tempType;
    }

    public TempType getTempType()
    {
        return tempType;
    }

    public void setTempType(TempType tempType)
    {
        this.tempType = tempType;
    }
}
