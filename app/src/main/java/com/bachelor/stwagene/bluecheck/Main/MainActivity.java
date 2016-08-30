package com.bachelor.stwagene.bluecheck.Main;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
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
import com.bachelor.stwagene.bluecheck.Cloud.CloudConnectionManager;
import com.bachelor.stwagene.bluecheck.Cloud.ConnectionReviser;
import com.bachelor.stwagene.bluecheck.Fragments.DevicesListFragment;
import com.bachelor.stwagene.bluecheck.Fragments.LogFragment;
import com.bachelor.stwagene.bluecheck.Fragments.ProgressFragment;
import com.bachelor.stwagene.bluecheck.Fragments.SettingsFragment;
import com.bachelor.stwagene.bluecheck.Model.BleDevice;
import com.bachelor.stwagene.bluecheck.Model.ChooserListItem;
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
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final int REQUEST_PERMISSION_ACCESS_COARSE_LOCATION = 2;
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
    private ConnectionReviser connectionReviser;
    private ArrayList<BleDevice> devices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //TODO String File für Texte anlegen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);

        checkBluetoothOfDevice();

        openFragment(new DevicesListFragment(), "DevicesListFragment");

        writeToLog("BlueCheck wurde gestartet.");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            requestLocationPermission();
        }
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
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
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
        if (requestCode == REQUEST_ENABLE_BLUETOOTH)
        {
            if (resultCode == RESULT_OK)
            {
                writeToLog("Bluetooth wurde aktiviert.");
                Toast.makeText(getApplicationContext(), "Bluetooth wurde aktiviert.", Toast.LENGTH_SHORT).show();
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

    public void performDeviceListItemClick(BleDevice item)
    {
        connectToDevice(item.getDevice());
        writeToLog(item.getName() + "(" + item.getAddress() + ") wurde ausgewählt.");
        ProgressFragment fragment = new ProgressFragment();
        Bundle bundle = new Bundle();
        bundle.putString("PROGRESS", "Verbinde...");
        fragment.setArguments(bundle);
        openFragment(fragment, "ProgressFragment");
        handleConnectionTimeOut();
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
        writeToLog("Starte Verbindung zu " + device.getName());
        BluetoothGattCallback callback;
        if (device.getName().contains("SensorTag"))
        {
            callback = new BluetoothTexasInstrumentsCallback(this);
        }
        else
        {
            callback = new BluetoothMainCallback(this);
        }
        mGatt = device.connectGatt(this, false, callback);
    }

    public void openFragment(Fragment fragment, String name)
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (name.equals("DevicesListFragment"))
        {
            ft.setCustomAnimations(android.R.anim.fade_in, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_left);
            ft.add(R.id.activity_layout, fragment, name);
        }
        else
        {
            if (name.equals("DeviceServicesListFragment") || name.equals("DeviceValuesListFragment"))
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

        if (name.equals("LogFragment"))
        {
            updateDeviceListView(DeviceListViewOption.BUTTON_BAR_VISIBILITY, false);
        }
        if (name.equals("OptionsFragment") || name.equals("ProgressFragment"))
        {
            updateDeviceListView(DeviceListViewOption.BUTTON_BAR_ELEVATION, false);
        }
        writeToLog(name + " wurde geöffnet.");
    }

    public void writeToLog(String text)
    {
        Log.d("BLUECHECK_ANDROID_APP", text);
        logTexts.add(LogFragment.getCurrentTimeString() + " --- " + text);
        LogFragment fragment = (LogFragment) getSupportFragmentManager().findFragmentByTag("LogFragment");
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
            if (lastFragmentName.equals("ProgressFragment"))
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
            if (lastFragmentName.equals("OptionsFragment"))
            {
                updateDeviceListView(DeviceListViewOption.BUTTON_BAR_ELEVATION, true);
            }
            if (lastFragmentName.equals("DeviceValuesListFragment") || lastFragmentName.equals("DeviceServicesListFragment"))
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
                            if (!getDeviceIdList().contains(result.getDevice().getAddress()))
                            {
                                addDevice(new BleDevice(result.getDevice().getName(), result.getDevice(), result.getDevice().getAddress()));
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
                                if (!getDeviceIdList().contains(device.getAddress()))
                                {
                                    addDevice(new BleDevice(device.getName(), device, device.getAddress()));
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

    public void refreshLoadFragment(String s)
    {
        ProgressFragment fragment = (ProgressFragment) getSupportFragmentManager().findFragmentByTag("ProgressFragment");
        if (fragment != null)
        {
            fragment.changeProgressText(s);
        }
    }

    private void stopBleScan(boolean isFinished)
    {
        refreshLoadFragment("Scannen abgeschlossen...");
        writeToLog("Scan 1 ist abgeschlossen.");
        updateDeviceListView(DeviceListViewOption.SET_SCAN_ONE_FINISHED, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            bleScanner.stopScan(bleCallback);
            isBleScanning = false;
        }
        else
        {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        closeProgressFragment();
        if (isFinished && !isDeveloperMode())
        {
            initPut();
        }
    }

    public void initPut()
    {
        CloudConnectionManager manager = new CloudConnectionManager(this);
        manager.execute(false, getDeviceIdList());
    }

    private void closeProgressFragment()
    {
        ProgressFragment fragment = (ProgressFragment) getSupportFragmentManager().findFragmentByTag("ProgressFragment");
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
            connectionReviser.tryToSend(null, true);
        }
        super.onResume();
    }

    public void sendData(String deviceValue)
    {
        connectionReviser = new ConnectionReviser(this);
        isSendingSuccessful = connectionReviser.tryToSend(deviceValue, false);
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
        DevicesListFragment fragment = (DevicesListFragment) getSupportFragmentManager().findFragmentByTag("DevicesListFragment");
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
        SettingsFragment fragment = (SettingsFragment) getSupportFragmentManager().findFragmentByTag("SettingsFragment");
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
        DevicesListFragment fragment = (DevicesListFragment) getSupportFragmentManager().findFragmentByTag("DevicesListFragment");
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
                case SET_SCAN_ONE_FINISHED:
                    fragment.setScanOneFinished();
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

    private ArrayList<String> getDeviceIdList()
    {
        ArrayList<String> list = new ArrayList<>();
        DevicesListFragment fragment = (DevicesListFragment) getSupportFragmentManager().findFragmentByTag("DevicesListFragment");
        if (fragment != null)
        {
            list = fragment.getDeviceIdList();
        }
        return list;
    }

    private void addDevice(BleDevice bleDevice)
    {
        DevicesListFragment fragment = (DevicesListFragment) getSupportFragmentManager().findFragmentByTag("DevicesListFragment");
        if (fragment != null)
        {
            fragment.addDevice(bleDevice);
            writeToLog(bleDevice.getName() + "(" + bleDevice.getAddress() + ") gefunden.");
        }
    }

    public ArrayList<BleDevice> getDevices()
    {
        return devices;
    }

    public void setDevices(ArrayList<BleDevice> devices)
    {
        this.devices = devices;
    }
}
