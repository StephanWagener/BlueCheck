package com.bachelor.stwagene.bluecheck.Model;

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

    // unsigned byte in int konvertieren
    public static Integer getTemperature(byte[] c, int offset) {
        Integer lowerByte = (int) c[offset] & 0xFF;
        Integer upperByte = (int) c[offset+1] & 0xFF;
        return (upperByte << 8) + lowerByte;
    }
}
