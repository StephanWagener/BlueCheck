package com.bachelor.stwagene.bluecheck.Model;

import static java.lang.StrictMath.pow;

/**
 * Consists of many static methods to convert the byte Data of the Bluetooth devices to real values.
 *
 * Created by stwagene on 03.05.2016.
 */
public class TexasInstrumentsUtils
{
    public static final String UUID_STRING_SERVICE_TEMPERATURE = "f000aa00-0451-4000-b000-000000000000";
    public static final String UUID_STRING_CHARACTERISTIC_TEMPERATURE_DATA = "f000aa01-0451-4000-b000-000000000000";
    public static final String UUID_STRING_CHARACTERISTIC_TEMPERATURE_CONFIGURATION = "f000aa02-0451-4000-b000-000000000000";
    public static final String UUID_STRING_CHARACTERISTIC_TEMPERATURE_DESCRIPTOR = "00002902-0000-1000-8000-00805f9b34fb";

    public static final String UUID_STRING_SERVICE_HUMIDITY = "f000aa20-0451-4000-b000-000000000000";
    public static final String UUID_STRING_CHARACTERISTIC_HUMIDITY_DATA = "f000aa21-0451-4000-b000-000000000000";
    public static final String UUID_STRING_CHARACTERISTIC_HUMIDITY_CONFIGURATION = "f000aa22-0451-4000-b000-000000000000";
    public static final String UUID_STRING_CHARACTERISTIC_HUMIDITY_DESCRIPTOR = "00002902-0000-1000-8000-00805f9b34fb";

    public static final String UUID_STRING_SERVICE_PRESSURE = "f000aa40-0451-4000-b000-000000000000";
    public static final String UUID_STRING_CHARACTERISTIC_PRESSURE_DATA = "f000aa41-0451-4000-b000-000000000000";
    public static final String UUID_STRING_CHARACTERISTIC_PRESSURE_CONFIGURATION = "f000aa42-0451-4000-b000-000000000000";
    public static final String UUID_STRING_CHARACTERISTIC_PRESSURE_DESCRIPTOR = "00002902-0000-1000-8000-00805f9b34fb";

    public static final String UUID_STRING_SERVICE_LIGHT_INTENSITY = "f000aa70-0451-4000-b000-000000000000";
    public static final String UUID_STRING_CHARACTERISTIC_LIGHT_INTENSITY_DATA = "f000aa71-0451-4000-b000-000000000000";
    public static final String UUID_STRING_CHARACTERISTIC_LIGHT_INTENSITY_CONFIGURATION = "f000aa72-0451-4000-b000-000000000000";
    public static final String UUID_STRING_CHARACTERISTIC_LIGHT_INTENSITY_DESCRIPTOR = "00002902-0000-1000-8000-00805f9b34fb";

    public static double getTemperature(byte[] value, int offset)
    {
        Integer lowerByte = (int) value[offset] & 0xFF;
        Integer upperByte = (int) value[offset+1] & 0xFF;
        return ((upperByte << 8) + lowerByte) / 128.0;
    }

    public static double getHumidity(byte[] value)
    {
        Integer lowerByte = (int) value[2] & 0xFF;
        Integer upperByte = (int) value[3] & 0xFF;
        double v = (upperByte << 8) + lowerByte;
        return (v / 65536)*100;
    }

    public static double getPressure(byte[] value)
    {
        Integer lowerByte = (int) value[3] & 0xFF;
        Integer middleByte = (int) value[4] & 0xFF;
        Integer upperByte = (int) value[5] & 0xFF;
        double v = (upperByte << 16) + (middleByte << 8) + lowerByte;
        return v / 100.0;
    }

    public static double getLightIntensity(byte[] value)
    {
        Integer lowerByte = (int) value[0] & 0x0FFF;
        Integer upperByte = (int) (value[1] & 0xF000) >> 12;
        return lowerByte * (0.01 * pow(2.0, upperByte));
    }
}
