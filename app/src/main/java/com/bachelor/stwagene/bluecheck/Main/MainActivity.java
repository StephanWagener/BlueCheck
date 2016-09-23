package com.bachelor.stwagene.bluecheck.Main;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.bachelor.stwagene.bluecheck.Bluetooth.BluetoothHandler;
import com.bachelor.stwagene.bluecheck.Bluetooth.BluetoothMainCallback;
import com.bachelor.stwagene.bluecheck.Bluetooth.BluetoothTexasInstrumentsCallback;
import com.bachelor.stwagene.bluecheck.Cloud.CloudConnectionInitiator;
import com.bachelor.stwagene.bluecheck.Fragments.DeviceServicesListFragment;
import com.bachelor.stwagene.bluecheck.Fragments.DeviceValuesListFragment;
import com.bachelor.stwagene.bluecheck.Fragments.DevicesListFragment;
import com.bachelor.stwagene.bluecheck.Fragments.LogFragment;
import com.bachelor.stwagene.bluecheck.Fragments.OptionsFragment;
import com.bachelor.stwagene.bluecheck.Fragments.ProgressFragment;
import com.bachelor.stwagene.bluecheck.Fragments.SettingsFragment;
import com.bachelor.stwagene.bluecheck.Model.BluetoothTag;
import com.bachelor.stwagene.bluecheck.Model.ChooserListItem;
import com.bachelor.stwagene.bluecheck.Model.Delivery;
import com.bachelor.stwagene.bluecheck.Model.DeviceListViewOption;
import com.bachelor.stwagene.bluecheck.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the communication between all Fragments and the events of the main layout.
 *
 * Created by stwagene on 02.05.2016.
 */
public class MainActivity extends AppCompatActivity
{
    public static final int FALSE_LOADED_PRODUCT = 0;
    public static final int RIGHT_LOADED_PRODUCT = 1;
    public static final int MISSING_PRODUCT = 2;
    public static final int UNKNOWN_PRODUCT = 3;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final int REQUEST_PERMISSION_ACCESS_COARSE_LOCATION = 2;
    private static final int REQUEST_ENABLE_BLUETOOTH_INIT = 3;
    private ArrayList<String> logTexts = new ArrayList<>();
    private boolean isClose = false;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner bleScanner;
    private ScanCallback bleCallback;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    private BluetoothHandler handler = new BluetoothHandler(this);
    private BluetoothGatt mGatt;
    private boolean isBleScanning = false;
    private boolean isShowUUIDInLog = true;
    private ChooserListItem valueChangedInterval = new ChooserListItem(1, "Jeder");
    private boolean isDeveloperMode = true;
    private boolean isSendingSuccessful = true;
    private CloudConnectionInitiator cloudConnectionInitiator;
    private ArrayList<BluetoothTag> devices = new ArrayList<>();
    private boolean isScanOneFinished = false;
    private String deliveryID = "ABCD1234";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //TODO info button an dem Warenitem, um alle Informationen zu sehen
        //TODO String File für Texte anlegen
        //TODO Informationen der Lieferung einarbeiten und anzeigen
        //TODO Status der Lieferung nach den Scans anzeigen (eine Art Zusammenfassung)
        //TODO sortieren der Liste
        //TODO Conroller für die Gateway Funktionalität
        //TODO Einstellungen öffnen verhindern wenn ein Progreess dialog offen ist
        //TODO Liste der VAlues wird beim wechseln zwischen Entwickler und Kunde nicht geändert
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);

        checkBluetoothOfDevice();

        openFragment(new DevicesListFragment());

        writeToLog("BlueCheck wurde gestartet.");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            requestLocationPermission();
        }

        cloudConnectionInitiator = new CloudConnectionInitiator(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState)
    {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void requestLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSION_ACCESS_COARSE_LOCATION);
        }
    }

    private void checkBluetoothOfDevice()
    {
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null)
        {
            writeToLog("Bluetooth wird nicht unterstützt.");
            Toast.makeText(getApplicationContext(), "Bluetooth wird nicht unterstützt.", Toast.LENGTH_SHORT).show();
            this.finish();
        }
        if (!mBluetoothAdapter.isEnabled())
        {
            writeToLog("Bluetooth ist nicht aktiviert.");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH_INIT);
        }
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
        {
            Toast.makeText(this, "Bluetooth Low Energy wird nicht unterstützt.", Toast.LENGTH_SHORT).show();
            finish();
        }
        else
        {
            writeToLog("Bluetooth Low Energy wird unterstützt und ist aktiviert.");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_ENABLE_BLUETOOTH || requestCode == REQUEST_ENABLE_BLUETOOTH_INIT)
        {
            if (resultCode == RESULT_OK)
            {
                writeToLog("Bluetooth wurde aktiviert.");
                Toast.makeText(getApplicationContext(), "Bluetooth wurde aktiviert.", Toast.LENGTH_SHORT).show();
                if (requestCode == REQUEST_ENABLE_BLUETOOTH)
                {
                    startBleScan();
                }
            }
            if (resultCode == RESULT_CANCELED)
            {
                writeToLog("Bluetooth wurde nicht aktiviert.");
                Toast.makeText(getApplicationContext(), "Bluetooth muss für die App aktiviert werden.", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        //left empty because of a bug in API 11 and higher
        //makes scanning ble devices after enabling bluetooth for Scan 1 possible
        //see http://stackoverflow.com/questions/7469082/getting-exception-illegalstateexception-can-not-perform-this-action-after-onsa
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == REQUEST_PERMISSION_ACCESS_COARSE_LOCATION)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Zugriff auf Standort erhalten.", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Der Zugriff auf den Standort ist essentiell.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void performDeviceListItemClick(BluetoothTag item)
    {
        writeToLog(item.getName() + "(" + item.getAddress() + ") wurde ausgewählt.");
        if (item.getDevice() == null)
        {
            Toast.makeText(getApplicationContext(), "Nicht in der Nähe.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            connectToDevice(item.getDevice());
        }
    }

    public void newProgress(final String text)
    {
        ProgressFragment fragment = (ProgressFragment) getSupportFragmentManager().findFragmentByTag(ProgressFragment.class.getSimpleName());
        if (fragment == null)
        {
            ProgressFragment progressFragment = new ProgressFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ProgressFragment.PROGRESS_FRAGMENT_ARGUMENT_TEXT, text);
            progressFragment.setArguments(bundle);
            openFragment(progressFragment);
        }
        else
        {
            fragment.changeProgressText(text);
        }
    }

    public void handleConnectionTimeOut()
    {
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                BluetoothManager bluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
                List<BluetoothDevice> devices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
                if (devices.size() == 0)
                {
                    closeProgressFragment();
                    Toast.makeText(getApplicationContext(), "Verbindung unterbrochen.", Toast.LENGTH_SHORT).show();
                }
            }
        }, 5000);
    }

    public void connectToDevice(BluetoothDevice device)
    {
        if (device.getName().contains("SensorTag"))
        {
            writeToLog("Starte Verbindung zu " + device.getName());
            mGatt = device.connectGatt(this, false, new BluetoothTexasInstrumentsCallback(this));
            newProgress("Verbinde...");
            handleConnectionTimeOut();
        }
        else if (isDeveloperMode())
        {
            writeToLog("Starte Verbindung zu " + device.getName());
            mGatt = device.connectGatt(this, false, new BluetoothMainCallback(this));
            newProgress("Verbinde...");
            handleConnectionTimeOut();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Gerät unbekannt. Kein auslesen möglich.", Toast.LENGTH_SHORT).show();
        }
    }

    public void openFragment(Fragment fragment)
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        String name = fragment.getClass().getSimpleName();
        if (name.equals(DevicesListFragment.class.getSimpleName()))
        {
            ft.setCustomAnimations(android.R.anim.fade_in, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_left);
            ft.add(R.id.activity_layout, fragment, name);
        }
        else
        {
            if (name.equals(DeviceServicesListFragment.class.getSimpleName()) || name.equals(DeviceValuesListFragment.class.getSimpleName()))
            {
                ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
                ft.replace(R.id.activity_layout, fragment, name);
            }
            else
            {
                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                ft.add(R.id.activity_layout, fragment, name);
            }
            ft.addToBackStack(name);
            updateDeviceListView(DeviceListViewOption.BACK_BUTTON_VISIBILITY, true);
            isClose = false;
        }

        ft.commit();

        if (name.equals(LogFragment.class.getSimpleName()))
        {
            updateDeviceListView(DeviceListViewOption.BUTTON_BAR_VISIBILITY, false);
        }
        if (name.equals(OptionsFragment.class.getSimpleName()) || name.equals(ProgressFragment.class.getSimpleName()))
        {
            updateDeviceListView(DeviceListViewOption.BUTTON_BAR_ELEVATION, false);
        }
        writeToLog(name + " wurde geöffnet.");
    }

    public void writeToLog(String text)
    {
        Log.d("BLUECHECK_ANDROID_APP", text);
        logTexts.add(LogFragment.getCurrentTimeString() + " --- " + text);
        LogFragment fragment = (LogFragment) getSupportFragmentManager().findFragmentByTag(LogFragment.class.getSimpleName());
        if (fragment != null)
        {
            fragment.appendLogText(LogFragment.getCurrentTimeString() + " --- " + text);
        }
    }

    @Override
    public void onBackPressed()
    {
        writeToLog("BackButton wurde gedrückt.");
        if (getSupportFragmentManager().getBackStackEntryCount() == 0)
        {
            updateDeviceListView(DeviceListViewOption.BACK_BUTTON_VISIBILITY, false);
            if (!isClose)
            {
                isClose = true;
                Toast.makeText(getApplicationContext(), "Erneut drücken zum Beenden.", Toast.LENGTH_LONG).show();
            }
            else
            {
                MainActivity.this.finish();
            }
            updateDeviceListView(DeviceListViewOption.BUTTON_BAR_VISIBILITY, true);
        }
        else
        {
            String lastFragmentName = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount()-1).getName();
            if (lastFragmentName.equals(ProgressFragment.class.getSimpleName()))
            {
                if (this.isBleScanning || mBluetoothAdapter.isDiscovering())
                {
                    stopBleScan(false);
                }
                else if (mGatt != null)
                {
                    mGatt.disconnect();
                }
                updateDeviceListView(DeviceListViewOption.BUTTON_BAR_VISIBILITY, true);
            }
            if (lastFragmentName.equals(OptionsFragment.class.getSimpleName()))
            {
                updateDeviceListView(DeviceListViewOption.BUTTON_BAR_ELEVATION, true);
            }
            if (lastFragmentName.equals(DeviceValuesListFragment.class.getSimpleName()) || lastFragmentName.equals(DeviceServicesListFragment.class.getSimpleName()))
            {
                if (mGatt != null)
                {
                    mGatt.disconnect();
                }
                updateDeviceListView(DeviceListViewOption.RSSI_VALUE_VISIBILITY, false);
            }
            if (getSupportFragmentManager().getBackStackEntryCount() == 1)
            {
                updateDeviceListView(DeviceListViewOption.BACK_BUTTON_VISIBILITY, false);
                updateDeviceListView(DeviceListViewOption.BUTTON_BAR_VISIBILITY, true);
                super.onBackPressed();
            }
            if (getSupportFragmentManager().getBackStackEntryCount() > 1)
            {
                super.onBackPressed();
            }
        }
    }


    public void startBleScan()
    {
        if (!mBluetoothAdapter.isEnabled())
        {
            writeToLog("Bluetooth ist nicht aktiviert.");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
        }
        else
        {
            setDeviceList(new ArrayList<BluetoothTag>());
            newProgress("Scanne nach Geräten...");
            writeToLog("Scanne nach Geräten...");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                bleScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
                isBleScanning = true;
                bleScanner.startScan(bleCallback = new ScanCallback()
                {
                    @Override
                    public void onScanResult(int callbackType, ScanResult result)
                    {
                        super.onScanResult(callbackType, result);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        {
                            if (result == null || result.getDevice() == null || TextUtils.isEmpty(result.getDevice().getName()))
                            {
                                return;
                            }
                            if (!getBluetoothAddresses(getDevicesList()).contains(result.getDevice().getAddress()))
                            {
                                addDevice(new BluetoothTag(result.getDevice().getName(), result.getDevice(), result.getDevice().getAddress()));
                            }
                        }
                    }

                    @Override
                    public void onBatchScanResults(List<ScanResult> results) { super.onBatchScanResults(results); }

                    @Override
                    public void onScanFailed(int errorCode)
                    {
                        super.onScanFailed(errorCode);
                    }
                });
            }
            else
            {
                mBluetoothAdapter.startLeScan(mLeScanCallback = new BluetoothAdapter.LeScanCallback()
                {
                    @Override
                    public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if (!getBluetoothAddresses(getDevicesList()).contains(device.getAddress()))
                                {
                                    addDevice(new BluetoothTag(device.getName(), device, device.getAddress()));
                                }
                            }
                        });
                    }
                });
            }

            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    stopBleScan(true);
                }
            }, 5000);
        }


    }

    private void stopBleScan(boolean isFinished)
    {
        newProgress("Scannen abgeschlossen...");
        writeToLog("Scan 1 ist abgeschlossen.");
        isScanOneFinished = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            bleScanner.stopScan(bleCallback);
            isBleScanning = false;
        }
        else
        {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

        if (isFinished && !isDeveloperMode())
        {
            sendData(new Delivery(getDevicesList(), deliveryID));
            newProgress("Sende Daten...");
        }
        else if (isFinished && isDeveloperMode())
        {
            closeProgressFragment();
        }
    }

    public void sendData(Delivery delivery)
    {
        isSendingSuccessful = cloudConnectionInitiator.sendDelivery(delivery);
    }

    public void closeProgressFragment()
    {
        ProgressFragment fragment = (ProgressFragment) getSupportFragmentManager().findFragmentByTag(ProgressFragment.class.getSimpleName());
        if (fragment != null)
        {
            fragment.close();
            updateDeviceListView(DeviceListViewOption.BUTTON_BAR_ELEVATION, true);
        }
    }

    @Override
    protected void onStop()
    {
        if (mGatt != null)
        {
            mGatt.close();
            mGatt = null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if (bleScanner != null)
            {
                bleScanner.stopScan(bleCallback);
            }
        }
        else
        {
            if (mBluetoothAdapter != null)
            {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }
        super.onStop();
    }

    @Override
    protected void onResume()
    {
        if (!isSendingSuccessful)
        {
            cloudConnectionInitiator.repeatSending();
        }
        super.onResume();
    }

    public void sendData(String deviceValue)
    {
        isSendingSuccessful = cloudConnectionInitiator.sendMeasurement(deviceValue);
    }

    public void deleteLogText()
    {
        this.logTexts = new ArrayList<>();
    }

    public ArrayList<String> getLogTexts()
    {
        return this.logTexts;
    }

    public void setRssiPercentageValue(int rssiValue)
    {
        DevicesListFragment fragment = (DevicesListFragment) getSupportFragmentManager().findFragmentByTag(DevicesListFragment.class.getSimpleName());
        if (fragment != null)
        {
            fragment.setRssiPercentageValue(rssiValue);
        }
    }

    public boolean isShowUUIDInLog()
    {
        return isShowUUIDInLog;
    }

    public void setShowUUIDInLog(boolean showUUIDInLog)
    {
        isShowUUIDInLog = showUUIDInLog;
    }

    public void setValueChangedInterval(ChooserListItem valueChangedInterval)
    {
        SettingsFragment fragment = (SettingsFragment) getSupportFragmentManager().findFragmentByTag(SettingsFragment.class.getSimpleName());
        if (fragment != null)
        {
            fragment.setValueChangedInterval(valueChangedInterval);
        }
        this.valueChangedInterval = valueChangedInterval;
    }

    public ChooserListItem getValueChangedInterval()
    {
        return valueChangedInterval;
    }

    private void updateDeviceListView(DeviceListViewOption option, boolean enable)
    {
        DevicesListFragment fragment = (DevicesListFragment) getSupportFragmentManager().findFragmentByTag(DevicesListFragment.class.getSimpleName());
        if (fragment != null)
        {
            switch (option)
            {
                case BUTTON_BAR_ELEVATION:
                    fragment.setButtonBarElevation(enable);
                    break;
                case BUTTON_BAR_VISIBILITY:
                    fragment.setButtonBarVisibility(enable);
                    break;
                case SEND_BUTTON_VISIBILITY:
                    fragment.setSendButtonVisibility(enable);
                    break;
                case BACK_BUTTON_VISIBILITY:
                    fragment.setBackButtonVisibility(enable);
                    break;
                case RSSI_VALUE_VISIBILITY:
                    fragment.setRssiValueVisibility(enable);
                    break;
                default:
                    break;
            }
        }
    }


    public boolean isDeveloperMode()
    {
        return isDeveloperMode;
    }

    public void setBackButtonGone()
    {
        if (getSupportFragmentManager().getBackStackEntryCount() <= 1)
        {
            updateDeviceListView(DeviceListViewOption.BACK_BUTTON_VISIBILITY, false);
        }
    }

    public void setDeveloperMode(boolean isActive)
    {
        this.isDeveloperMode = isActive;
        updateDeviceListView(DeviceListViewOption.SEND_BUTTON_VISIBILITY, this.isDeveloperMode);
    }

    public BluetoothHandler getHandler()
    {
        return handler;
    }

    private ArrayList<BluetoothTag> getDevicesList()
    {
        ArrayList<BluetoothTag> list = new ArrayList<>();
        DevicesListFragment fragment = (DevicesListFragment) getSupportFragmentManager().findFragmentByTag(DevicesListFragment.class.getSimpleName());
        if (fragment != null)
        {
            list = fragment.getDevicesList();
        }
        return list;
    }

    private ArrayList<String> getBluetoothAddresses(ArrayList<BluetoothTag> tags)
    {
        ArrayList<String> addresses = new ArrayList<>();
        for (BluetoothTag tag : tags)
        {
            addresses.add(tag.getAddress());
        }
        return addresses;
    }

    private void addDevice(BluetoothTag bluetoothTag)
    {
        this.devices.add(bluetoothTag);
        DevicesListFragment fragment = (DevicesListFragment) getSupportFragmentManager().findFragmentByTag(DevicesListFragment.class.getSimpleName());
        if (fragment != null)
        {
            fragment.addDevice(bluetoothTag);
            writeToLog(bluetoothTag.getName() + "(" + bluetoothTag.getAddress() + ") gefunden.");
        }
    }

    public ArrayList<BluetoothTag> getDevices()
    {
        return devices;
    }

    public void setDeviceList(ArrayList<BluetoothTag> devices)
    {
        DevicesListFragment fragment = (DevicesListFragment) getSupportFragmentManager().findFragmentByTag(DevicesListFragment.class.getSimpleName());
        if (fragment != null)
        {
            fragment.refreshDeviceList(devices);
        }
        this.devices = devices;
    }

    public boolean isScanOneFinished()
    {
        return isScanOneFinished;
    }

    public void setScanOneFinished(boolean scanOneFinished)
    {
        isScanOneFinished = scanOneFinished;
    }

    public String getDeliveryID()
    {
        return deliveryID;
    }

    public void setDeliveryID(String id)
    {
        deliveryID = id;
    }
}
