package com.bachelor.stwagene.bluecheck.Main;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bachelor.stwagene.bluecheck.Bluetooth.BluetoothCallbackFactory;
import com.bachelor.stwagene.bluecheck.Bluetooth.BluetoothHandler;
import com.bachelor.stwagene.bluecheck.Cloud.CloudConnectionInitiator;
import com.bachelor.stwagene.bluecheck.Fragments.ChooserFragment;
import com.bachelor.stwagene.bluecheck.Fragments.DeliveryResultFragment;
import com.bachelor.stwagene.bluecheck.Fragments.DeliverySelectionFragment;
import com.bachelor.stwagene.bluecheck.Fragments.DeviceServicesListFragment;
import com.bachelor.stwagene.bluecheck.Fragments.DeviceValuesListFragment;
import com.bachelor.stwagene.bluecheck.Fragments.DevicesListFragment;
import com.bachelor.stwagene.bluecheck.Fragments.LogFragment;
import com.bachelor.stwagene.bluecheck.Fragments.OptionsFragment;
import com.bachelor.stwagene.bluecheck.Fragments.ProgressFragment;
import com.bachelor.stwagene.bluecheck.Fragments.SettingsFragment;
import com.bachelor.stwagene.bluecheck.Fragments.StartFragment;
import com.bachelor.stwagene.bluecheck.Model.BluetoothTag;
import com.bachelor.stwagene.bluecheck.Model.Delivery;
import com.bachelor.stwagene.bluecheck.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    private static final int REQUEST_PERMISSION_ACCESS_STORAGE = 4;
    private ArrayList<String> logTexts = new ArrayList<>();
    private boolean isClose = false;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner bleScanner;
    private ScanCallback bleCallback;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    private BluetoothHandler handler = new BluetoothHandler(this);
    private BluetoothGatt mGatt;
    private boolean isBleScanning = false;
    private boolean isSendingSuccessful = true;
    private CloudConnectionInitiator cloudConnectionInitiator;
    private ArrayList<BluetoothTag> devices = new ArrayList<>();
    private boolean isScanOneFinished = false;
    private LinearLayout buttonBar;
    private TextView scanTwo;
    private boolean isScanTwoFinished = false;
    private ImageView backButton;
    private ActionBar actionBar;
    private TextView rssiPercentageTextView;
    private boolean isScanOne = true;
    private boolean isCloseProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //TODO info button an dem Warenitem, um alle Informationen zu sehen
        //TODO String File für Texte anlegen
        //TODO sortieren der Liste
        //TODO Conroller für die Gateway Funktionalität
        //TODO Liste der Values wird beim Wechseln zwischen Entwickler und Kunde nicht geändert
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);

        checkBluetoothOfDevice();

        initActionBar();

        writeToLog("BlueCheck wurde gestartet.");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            requestLocationPermission();
            requestStoragePermission();
        }

        initScanButtons();

        cloudConnectionInitiator = new CloudConnectionInitiator(this);

        openFragment(new DeliverySelectionFragment());
    }

    private void initActionBar()
    {
        actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setElevation(0);
            actionBar.setCustomView(R.layout.toolbar_layout);
        }
        setButtonBarElevation(true);

        initToolbar();
    }

    private void initToolbar()
    {
        backButton = (ImageView) actionBar.getCustomView().findViewById(R.id.back_icon);
        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });

        ImageView menu = (ImageView) actionBar.getCustomView().findViewById(R.id.menu_icon);
        menu.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                writeToLog("Menü-Icon angeklickt.");
                ProgressFragment progress = (ProgressFragment) getSupportFragmentManager().findFragmentByTag(ProgressFragment.class.getSimpleName());
                if (progress == null)
                {
                    OptionsFragment fragment = (OptionsFragment) getSupportFragmentManager().findFragmentByTag(OptionsFragment.class.getSimpleName());
                    if (fragment != null)
                    {
                        onBackPressed();
                    }
                    else
                    {
                        openFragment(new OptionsFragment());
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Nicht während eines Scans!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        rssiPercentageTextView = (TextView) actionBar.getCustomView().findViewById(R.id.connection_rssi_percentage);
    }

    private void initScanButtons()
    {
        buttonBar = (LinearLayout) findViewById(R.id.button_bar_scans);
        setButtonBarElevation(true);

        Button scanOne = (Button) findViewById(R.id.button_scan_one);

        scanOne.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                writeToLog("Scan 1 Button wurde gedrückt.");
                isScanOneFinished = false;
                isScanTwoFinished = false;
                isScanOne = true;
                DevicesListFragment fragment = (DevicesListFragment) getSupportFragmentManager().findFragmentByTag(DevicesListFragment.class.getSimpleName());
                if (fragment != null)
                {
                    fragment.clearDeviceList();
                }
                startBleScan();
                scanTwo.setTextColor(getResources().getColor(android.R.color.white));
            }
        });

        scanTwo = (Button) findViewById(R.id.button_scan_two);

        scanTwo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isScanOneFinished())
                {
                    isScanTwoFinished = false;
                    isScanOne = false;
                    startBleScan();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Zuerst Scan 1 susgeführen!", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    private void requestStoragePermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_ACCESS_STORAGE);
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
        if (requestCode == REQUEST_PERMISSION_ACCESS_STORAGE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Zugriff auf Speicher erhalten.", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Der Zugriff auf den Speicher ist essentiell.", Toast.LENGTH_SHORT).show();
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

    public void connectToDevice(BluetoothDevice device)
    {
        BluetoothCallbackFactory.BluetoothCallbackType type = device.getName().contains("SensorTag")
                                ? BluetoothCallbackFactory.BluetoothCallbackType.TEXAS_INSTRUMENTS
                                : BluetoothCallbackFactory.BluetoothCallbackType.MAIN;
        BluetoothGattCallback callback = BluetoothCallbackFactory.getBluetoothCallback(type, this);

        if (device.getName().contains("SensorTag"))
        {
            writeToLog("Starte Verbindung zu " + device.getName());
            mGatt = device.connectGatt(this, false, callback);
            newProgress("Verbinde...");
        }
        else if (getSharedPreferences().getBoolean(SettingsFragment.IS_DEVELOPER_MODE, true))
        {
            writeToLog("Starte Verbindung zu " + device.getName());
            mGatt = device.connectGatt(this, false, callback);
            newProgress("Verbinde...");
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
        if (name.equals(DeliverySelectionFragment.class.getSimpleName()))
        {
            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
            ft.add(R.id.content_container, fragment, name);
        }
        else if (name.equals(DeliveryResultFragment.class.getSimpleName())
                || name.equals(StartFragment.class.getSimpleName()))
        {
            setRssiPercentageVisible(false);
            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
            ft.replace(R.id.content_container, fragment, name);
        }
        else
        {
            if (name.equals(DevicesListFragment.class.getSimpleName()))
            {
                setRssiPercentageVisible(false);
                setButtonBarVisibility(true);
                ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
                ft.add(R.id.content_container, fragment, name);
            }
            else if (name.equals(DeviceServicesListFragment.class.getSimpleName()) || name.equals(DeviceValuesListFragment.class.getSimpleName()))
            {
                ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
                ft.replace(R.id.content_container, fragment, name);
                setButtonBarVisibility(false);
            }
            else
            {
                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                ft.add(R.id.content_container, fragment, name);
            }
            if (!name.equals(ChooserFragment.class.getSimpleName()))
            {
                ft.addToBackStack(name);
            }
            setBackButtonVisible(true);
            isClose = false;
        }

        ft.commit();

        if (name.equals(LogFragment.class.getSimpleName()) || name.equals(DeliverySelectionFragment.class.getSimpleName()))
        {
            setButtonBarVisibility(false);
        }
        if (name.equals(OptionsFragment.class.getSimpleName()))
        {
            setButtonBarElevation(false);
        }
        if (name.equals(ProgressFragment.class.getSimpleName()))
        {
            setButtonBarVisibility(false);
        }
        writeToLog(name + " wurde geöffnet.");
    }

    public void setButtonBarVisibility(boolean visible)
    {
        this.buttonBar.setVisibility(visible ? View.VISIBLE : View.GONE);
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
            setBackButtonVisible(false);
            if (!isClose)
            {
                isClose = true;
                Toast.makeText(getApplicationContext(), "Erneut drücken zum Beenden.", Toast.LENGTH_LONG).show();
            }
            else
            {
                MainActivity.this.finish();
            }
            setButtonBarVisibility(true);
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
                else if (mGatt != null && !isCloseProgress)
                {
                    mGatt.disconnect();
                }
                if (isCloseProgress)
                {
                    isCloseProgress = false;
                }
                setButtonBarVisibility(true);
            }
            if (lastFragmentName.equals(OptionsFragment.class.getSimpleName()))
            {
                setButtonBarElevation(true);
            }
            if (lastFragmentName.equals(DeviceValuesListFragment.class.getSimpleName()) || lastFragmentName.equals(DeviceServicesListFragment.class.getSimpleName()))
            {
                if (mGatt != null)
                {
                    mGatt.disconnect();
                }
                setRssiPercentageVisible(false);
            }
            if (getSupportFragmentManager().getBackStackEntryCount() == 1)
            {
                setBackButtonVisible(false);
                setButtonBarVisibility(true);
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
        DevicesListFragment fragment = (DevicesListFragment) getSupportFragmentManager().findFragmentByTag(DevicesListFragment.class.getSimpleName());
        if(fragment != null)
        {
            onBackPressed();
        }

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
                            if (!getBluetoothAddresses(devices).contains(result.getDevice().getAddress()))
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
                                if (!getBluetoothAddresses(devices).contains(device.getAddress()))
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
        if (isScanOne)
        {
            isScanOneFinished = isFinished;
        }
        else
        {
            isScanTwoFinished = isFinished;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            bleScanner.stopScan(bleCallback);
            isBleScanning = false;
        }
        else
        {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

        if (isFinished)
        {
            sendData(new Delivery(devices, getSharedPreferences().getString(SettingsFragment.CURRENT_DELIVERY, "")));
            newProgress("Sende Daten...");
        }
    }

    public ArrayList<String> getCurrentDeliveries()
    {
        //TODO Daten aus der Cloud verwenden
        ArrayList<String> list = new ArrayList<>();
        list.add("ABCD1234");
        list.add("7890VBNM");
        list.add("QWER3456");
        list.add("FGHJ6789");
        return list;
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
            isCloseProgress = true;
            onBackPressed();
            setRssiPercentageVisible(false);
            setButtonBarVisibility(true);
            setButtonBarElevation(true);
        }
    }

    public void setButtonBarElevation(boolean elevate)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if (buttonBar != null)
            {
                if (elevate)
                {
                    buttonBar.setElevation(30);
                }
                else
                {
                    buttonBar.setElevation(0);
                }
            }
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
        if (this.rssiPercentageTextView.getVisibility() != View.VISIBLE)
        {
            setRssiPercentageVisible(true);
        }

        if (rssiValue < -100)
        {
            this.rssiPercentageTextView.setText(1+"%");
        }
        else if (rssiValue > -25)
        {
            this.rssiPercentageTextView.setText(100+"%");
        }
        else
        {
            this.rssiPercentageTextView.setText(String.format(Locale.GERMAN, "%.1f", ((double)rssiValue+100.0)/75.0*100.0)+"%");
        }
    }

    public BluetoothHandler getHandler()
    {
        return handler;
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
        writeToLog(bluetoothTag.getName() + "(" + bluetoothTag.getAddress() + ") gefunden.");
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

    public boolean isScanTwoFinished()
    {
        return isScanTwoFinished;
    }

    public SharedPreferences getSharedPreferences()
    {
        return this.getSharedPreferences("com.bachelor.BlueCheck.Settings", Context.MODE_PRIVATE);
    }

    public void setRssiPercentageVisible(boolean rssiPercentageVisible)
    {
        this.rssiPercentageTextView.setVisibility(rssiPercentageVisible ? View.VISIBLE : View.GONE);
    }

    public void setBackButtonVisible(boolean backButtonVisible)
    {
        this.backButton.setVisibility(backButtonVisible ? View.VISIBLE : View.GONE);
    }

    public void reset()
    {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
