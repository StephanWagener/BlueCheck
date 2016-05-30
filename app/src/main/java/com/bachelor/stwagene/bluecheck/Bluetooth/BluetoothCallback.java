package com.bachelor.stwagene.bluecheck.Bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.os.Bundle;
import android.os.Message;

import com.bachelor.stwagene.bluecheck.Main.MainActivity;
import com.bachelor.stwagene.bluecheck.Model.TISensorTagData;
import com.bachelor.stwagene.bluecheck.Model.TexasInstrumentsUtils;

import java.util.Arrays;
import java.util.UUID;

/**
 * Callback für GATT-Verbindung
 *
 * Created by stwagene on 10.05.2016.
 */
public class BluetoothCallback extends BluetoothGattCallback {

    private TISensorTagData sensorTag;
    private MainActivity activity;

    public BluetoothCallback(TISensorTagData sensorTag, MainActivity mainActivity) {
        this.sensorTag = sensorTag;
        this.activity = mainActivity;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        activity.writeToLog(getClass().getSimpleName() + ": Neuer Verbindungsstatus => " + newState);
        switch (newState)
        {
            case BluetoothProfile.STATE_CONNECTED:
                activity.writeToLog(getClass().getSimpleName() + ": Das Device " + gatt.getDevice().getName() + " wurde verbunden.");
                gatt.discoverServices();
                break;
            case BluetoothProfile.STATE_DISCONNECTED:
                activity.writeToLog(getClass().getSimpleName() + ": Die Verbindung zum Device " + gatt.getDevice().getName() + " wurde getrennt.");
                break;
            default:
                gatt.disconnect();
                activity.writeToLog(getClass().getSimpleName() + ": Neuer Verbindungsstatus => " + newState);
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status)
    {
        activity.writeToLog(getClass().getSimpleName() + ": Services und Characteristics werden ausgelesen und gespeichert.");

        TISensorTagData.services = gatt.getServices();

        BluetoothGattCharacteristic characteristic = gatt.getService(UUID.fromString(TexasInstrumentsUtils.UUID_STRING_SERVICE_TEMPERATURE))
                .getCharacteristic(UUID.fromString(TexasInstrumentsUtils.UUID_STRING_CHARACTERISTIC_TEMPERATURE_CONFIGURATION));
        characteristic.setValue(new byte[]{0x01});
        gatt.writeCharacteristic(characteristic);
        activity.writeToLog(getClass().getSimpleName() + ": Der Service " + characteristic.getService().getUuid() + " wird aktiviert.");
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
    {
        activity.writeToLog(getClass().getSimpleName() + ": Für den Service " + characteristic.getService().getUuid() + " werden die Notifications aktiviert.");
        gatt.setCharacteristicNotification(characteristic, true);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(TexasInstrumentsUtils.UUID_STRING_CHARACTERISTIC_TEMPERATURE_DESCRIPTOR));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(descriptor);

        activity.writeToLog(getClass().getSimpleName() + ": Der Service " + characteristic.getService().getUuid() + " wird ausgelesen.");
        saveTemperatureValue(characteristic);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
    {
        activity.writeToLog(getClass().getSimpleName() + ": Der Wert des Service " + characteristic.getService().getUuid() + " hat sich geändert.");
        saveTemperatureValue(characteristic);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
    {
        BluetoothGattCharacteristic characteristic2 = gatt.getService(UUID.fromString(TexasInstrumentsUtils.UUID_STRING_SERVICE_TEMPERATURE))
                .getCharacteristic(UUID.fromString(TexasInstrumentsUtils.UUID_STRING_CHARACTERISTIC_TEMPERATURE_DATA));
        gatt.readCharacteristic(characteristic2);
        activity.writeToLog(getClass().getSimpleName() + ": Der Service " + characteristic2.getService().getUuid() + " wurde aktiviert.");
    }

    private void saveTemperatureValue(BluetoothGattCharacteristic c)
    {
        byte[] value = c.getValue();
        sensorTag.setLastObjectTemperature(TexasInstrumentsUtils.getTemperature(value, 0) / 128.0);
        sensorTag.setLastAmbientTemperature(TexasInstrumentsUtils.getTemperature(value, 2) / 128.0);

        Message message = new Message();
        if (Arrays.asList(value).equals(Arrays.asList(new byte[]{0,0,0,0})))
        {
            Bundle bundle = new Bundle();
            bundle.putString("VALUE", "NULL");
            message.setData(bundle);
        }
        else
        {
            Bundle bundle = new Bundle();
            bundle.putString("VALUE", sensorTag.getLastAmbientTemperature()+"");
            message.setData(bundle);
        }

        activity.handler.handleMessage(message);
    }
}

