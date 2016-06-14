package com.bachelor.stwagene.bluecheck.Bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;

import com.bachelor.stwagene.bluecheck.Main.MainActivity;
import com.bachelor.stwagene.bluecheck.Model.TISensorTagData;
import com.bachelor.stwagene.bluecheck.Model.TexasInstrumentsUtils;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Callback für GATT-Verbindung
 *
 * Created by stwagene on 10.05.2016.
 */
public class BluetoothCallback extends BluetoothGattCallback
{
    private MainActivity activity;
    private int state = 0;
    private int temperatureCounter;
    private int pressureCounter;
    private int humidityCounter;
    private int lightIntensityCounter;
    private boolean isTemperatureShown = false;
    private boolean isPressureShown = false;
    private boolean isHumidityShown = false;
    private boolean isLightIntensityShown = false;

    public BluetoothCallback(MainActivity mainActivity)
    {
        this.activity = mainActivity;
        resetCounter();
    }

    private void resetCounter()
    {
        lightIntensityCounter = 0;
        humidityCounter = 0;
        pressureCounter = 0;
        temperatureCounter = 0;
    }

    private void initNextService(BluetoothGatt gatt)
    {
        BluetoothGattCharacteristic characteristic = null;
        switch(state)
        {
            case 0:
                characteristic = gatt.getService(UUID.fromString(TexasInstrumentsUtils.UUID_STRING_SERVICE_TEMPERATURE))
                        .getCharacteristic(UUID.fromString(TexasInstrumentsUtils.UUID_STRING_CHARACTERISTIC_TEMPERATURE_CONFIGURATION));
                characteristic.setValue(new byte[]{0x01});
                gatt.writeCharacteristic(characteristic);
                break;
            case 1:
                characteristic = gatt.getService(UUID.fromString(TexasInstrumentsUtils.UUID_STRING_SERVICE_HUMIDITY))
                        .getCharacteristic(UUID.fromString(TexasInstrumentsUtils.UUID_STRING_CHARACTERISTIC_HUMIDITY_CONFIGURATION));
                characteristic.setValue(new byte[]{0x01});
                gatt.writeCharacteristic(characteristic);
                break;
            case 2:
                characteristic = gatt.getService(UUID.fromString(TexasInstrumentsUtils.UUID_STRING_SERVICE_PRESSURE))
                        .getCharacteristic(UUID.fromString(TexasInstrumentsUtils.UUID_STRING_CHARACTERISTIC_PRESSURE_CONFIGURATION));
                characteristic.setValue(new byte[]{0x01});
                gatt.writeCharacteristic(characteristic);
                break;
            case 3:
                characteristic = gatt.getService(UUID.fromString(TexasInstrumentsUtils.UUID_STRING_SERVICE_LIGHT_INTENSITY))
                        .getCharacteristic(UUID.fromString(TexasInstrumentsUtils.UUID_STRING_CHARACTERISTIC_LIGHT_INTENSITY_CONFIGURATION));
                characteristic.setValue(new byte[]{0x01});
                gatt.writeCharacteristic(characteristic);
                break;
            default:
                break;
        }

        if (characteristic != null)
        {
            activity.handler.writeToLog(getClass().getSimpleName() + ": Der Service " + characteristic.getService().getUuid() + " wird aktiviert.");
        }
    }

    private void enableNextNotifications(BluetoothGattCharacteristic characteristic, BluetoothGatt gatt)
    {
        BluetoothGattDescriptor descriptor;
        switch(state)
        {
            case 0:
                gatt.setCharacteristicNotification(characteristic, true);
                descriptor = characteristic.getDescriptor(UUID.fromString(TexasInstrumentsUtils.UUID_STRING_CHARACTERISTIC_TEMPERATURE_DESCRIPTOR));
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
                break;
            case 1:
                gatt.setCharacteristicNotification(characteristic, true);
                descriptor = characteristic.getDescriptor(UUID.fromString(TexasInstrumentsUtils.UUID_STRING_CHARACTERISTIC_HUMIDITY_DESCRIPTOR));
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
                break;
            case 2:
                gatt.setCharacteristicNotification(characteristic, true);
                descriptor = characteristic.getDescriptor(UUID.fromString(TexasInstrumentsUtils.UUID_STRING_CHARACTERISTIC_PRESSURE_DESCRIPTOR));
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
                break;
            case 3:
                gatt.setCharacteristicNotification(characteristic, true);
                descriptor = characteristic.getDescriptor(UUID.fromString(TexasInstrumentsUtils.UUID_STRING_CHARACTERISTIC_LIGHT_INTENSITY_DESCRIPTOR));
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
                break;
            default:
                break;
        }

        activity.handler.writeToLog(getClass().getSimpleName() + ": Der Service " + characteristic.getService().getUuid() + " wird ausgelesen.");
        saveNextValue(characteristic, gatt);
    }

    private void writeNextCharacteristic(BluetoothGatt gatt)
    {
        BluetoothGattCharacteristic characteristic = null;
        switch(state)
        {
            case 0:
                characteristic = gatt.getService(UUID.fromString(TexasInstrumentsUtils.UUID_STRING_SERVICE_TEMPERATURE))
                        .getCharacteristic(UUID.fromString(TexasInstrumentsUtils.UUID_STRING_CHARACTERISTIC_TEMPERATURE_DATA));
                gatt.readCharacteristic(characteristic);
                break;
            case 1:
                characteristic = gatt.getService(UUID.fromString(TexasInstrumentsUtils.UUID_STRING_SERVICE_HUMIDITY))
                        .getCharacteristic(UUID.fromString(TexasInstrumentsUtils.UUID_STRING_CHARACTERISTIC_HUMIDITY_DATA));
                gatt.readCharacteristic(characteristic);
                break;
            case 2:
                characteristic = gatt.getService(UUID.fromString(TexasInstrumentsUtils.UUID_STRING_SERVICE_PRESSURE))
                        .getCharacteristic(UUID.fromString(TexasInstrumentsUtils.UUID_STRING_CHARACTERISTIC_PRESSURE_DATA));
                gatt.readCharacteristic(characteristic);
                break;
            case 3:
                characteristic = gatt.getService(UUID.fromString(TexasInstrumentsUtils.UUID_STRING_SERVICE_LIGHT_INTENSITY))
                        .getCharacteristic(UUID.fromString(TexasInstrumentsUtils.UUID_STRING_CHARACTERISTIC_LIGHT_INTENSITY_DATA));
                gatt.readCharacteristic(characteristic);
                break;
            default:
                break;
        }

        if (characteristic != null)
        {
            activity.handler.writeToLog(getClass().getSimpleName() + ": Der Service " + characteristic.getService().getUuid() + " wurde aktiviert.");
        }
    }

    private void raiseState()
    {
        state++;
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status)
    {
        activity.handler.setRssiPercentageValue(rssi);
        super.onReadRemoteRssi(gatt, rssi, status);
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
    {
        activity.handler.writeToLog(getClass().getSimpleName() + ": Neuer Verbindungsstatus => " + newState);
        switch (newState)
        {
            case BluetoothProfile.STATE_CONNECTED:
                activity.handler.writeToLog(getClass().getSimpleName() + ": Das Device " + gatt.getDevice().getName() + " wurde verbunden.");
                gatt.discoverServices();
                break;
            case BluetoothProfile.STATE_DISCONNECTED:
                activity.handler.writeToLog(getClass().getSimpleName() + ": Die Verbindung zum Device " + gatt.getDevice().getName() + " wurde getrennt.");
                break;
            default:
                gatt.disconnect();
                activity.handler.writeToLog(getClass().getSimpleName() + ": Neuer Verbindungsstatus => " + newState);
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status)
    {
        activity.handler.writeToLog(getClass().getSimpleName() + ": Services und Characteristics werden ausgelesen und gespeichert.");
        TISensorTagData.services = (ArrayList<BluetoothGattService>) gatt.getServices();
        activity.handler.refreshProgressFragment("Lese Daten...");
        initNextService(gatt);
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
    {
        activity.handler.writeToLog(getClass().getSimpleName() + ": Für den Service " + characteristic.getService().getUuid() + " werden die Notifications aktiviert.");
        enableNextNotifications(characteristic, gatt);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
    {
        gatt.readRemoteRssi();
        setNextCounter(characteristic);
        activity.handler.refreshDeviceValuesFragment();
        saveNextValue(characteristic, gatt);
    }

    private void setNextCounter(BluetoothGattCharacteristic characteristic)
    {
        int selectedInterval = activity.getValueChangedInterval().getValue();
        String uuid = characteristic.getService().getUuid().toString();
        switch (uuid)
        {
            case TexasInstrumentsUtils.UUID_STRING_SERVICE_TEMPERATURE:
                if (temperatureCounter == selectedInterval && !isTemperatureShown)
                {
                    activity.handler.writeToLog(getClass().getSimpleName() + ": Der Wert des Service " + characteristic.getService().getUuid() + " hat sich geändert.");
                    temperatureCounter = 0;
                }
                if (selectedInterval == 0)
                {
                    isTemperatureShown = true;
                }
                temperatureCounter++;
                break;
            case TexasInstrumentsUtils.UUID_STRING_SERVICE_PRESSURE:
                if (pressureCounter == selectedInterval && !isPressureShown)
                {
                    activity.handler.writeToLog(getClass().getSimpleName() + ": Der Wert des Service " + characteristic.getService().getUuid() + " hat sich geändert.");
                    pressureCounter = 0;
                }
                if (selectedInterval == 0)
                {
                    isPressureShown = true;
                }
                pressureCounter++;
                break;
            case TexasInstrumentsUtils.UUID_STRING_SERVICE_LIGHT_INTENSITY:
                if (lightIntensityCounter == selectedInterval && !isLightIntensityShown)
                {
                    activity.handler.writeToLog(getClass().getSimpleName() + ": Der Wert des Service " + characteristic.getService().getUuid() + " hat sich geändert.");
                    lightIntensityCounter = 0;
                }
                if (selectedInterval == 0)
                {
                    isLightIntensityShown = true;
                }
                lightIntensityCounter++;
                break;
            case TexasInstrumentsUtils.UUID_STRING_SERVICE_HUMIDITY:
                if (humidityCounter == selectedInterval && !isHumidityShown)
                {
                    activity.handler.writeToLog(getClass().getSimpleName() + ": Der Wert des Service " + characteristic.getService().getUuid() + " hat sich geändert.");
                    humidityCounter = 0;
                }
                if (selectedInterval == 0)
                {
                    isHumidityShown = true;
                }
                humidityCounter++;
                break;
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
    {
        writeNextCharacteristic(gatt);
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
    {
        raiseState();
        initNextService(gatt);
    }

    private void saveNextValue(BluetoothGattCharacteristic characteristic, BluetoothGatt gatt)
    {
        byte[] value = characteristic.getValue();
        String UUID = characteristic.getService().getUuid().toString();
        switch (UUID)
        {
            case TexasInstrumentsUtils.UUID_STRING_SERVICE_TEMPERATURE:
                TISensorTagData.setObjectTemperature(TexasInstrumentsUtils.getTemperature(value, 0));
                TISensorTagData.setAmbientTemperature(TexasInstrumentsUtils.getTemperature(value, 2));
                break;
            case TexasInstrumentsUtils.UUID_STRING_SERVICE_HUMIDITY:
                TISensorTagData.setHumidity(TexasInstrumentsUtils.getHumidity(value));
                break;
            case TexasInstrumentsUtils.UUID_STRING_SERVICE_PRESSURE:
                TISensorTagData.setPressure(TexasInstrumentsUtils.getPressure(value));
                break;
            case TexasInstrumentsUtils.UUID_STRING_SERVICE_LIGHT_INTENSITY:
                TISensorTagData.setLightIntensity(TexasInstrumentsUtils.getLightIntensity(value));
                break;
        }

        if (state == 3)
        {
            activity.handler.openDeviceValuesList();
        }
        TISensorTagData.services = (ArrayList<BluetoothGattService>) gatt.getServices();
    }
}

