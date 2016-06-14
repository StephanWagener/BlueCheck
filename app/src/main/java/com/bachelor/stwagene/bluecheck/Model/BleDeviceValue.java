package com.bachelor.stwagene.bluecheck.Model;

/**
 * Created by stwagene on 14.06.2016.
 */
public class BleDeviceValue
{
    private String name;
    private String unit;
    private double value;

    public BleDeviceValue(String name, String unit, double value)
    {
        this.name = name;
        this.unit = unit;
        this.value = value;
    }


    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public double getValue()
    {
        return value;
    }

    public void setValue(double value)
    {
        this.value = value;
    }

    public String getUnit()
    {
        return unit;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    public String getCompleteText()
    {
        return String.format("%.2f %s", this.value, this.unit);
    }
}
