package com.bachelor.stwagene.bluecheck.Model;

/**
 * Created by stwagene on 08.08.2016.
 */
public class DeviceValue
{
    private ValueType valueType;

    public DeviceValue() {}

    public DeviceValue(ValueType valueType)
    {
        this.valueType = valueType;
    }

    public ValueType getValueType()
    {
        return valueType;
    }

    public void setValueType(ValueType valueType)
    {
        this.valueType = valueType;
    }
}
