package com.bachelor.stwagene.bluecheck.Model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Created by stwagene on 08.08.2016.
 */
@JsonPropertyOrder({ "value", "unit" })
public class ValueType
{
    private double value;
    private String unit;

    public ValueType() {}

    public ValueType(double value, String unit)
    {
        this.value = value;
        this.unit = unit;
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
}
