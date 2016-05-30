package com.bachelor.stwagene.bluecheck.Main;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.bachelor.stwagene.bluecheck.Bluetooth.BluetoothCallback;
import com.bachelor.stwagene.bluecheck.Bluetooth.BluetoothHandler;
import com.bachelor.stwagene.bluecheck.Cloud.CloudConnectionManager;
import com.bachelor.stwagene.bluecheck.Fragments.DeviceValueFragment;
import com.bachelor.stwagene.bluecheck.Fragments.LogFragment;
import com.bachelor.stwagene.bluecheck.Fragments.OptionsFragment;
import com.bachelor.stwagene.bluecheck.Fragments.ShowProgressFragment;
import com.bachelor.stwagene.bluecheck.ListAdapter.DevicesListAdapter;
import com.bachelor.stwagene.bluecheck.Model.BleDevice;
import com.bachelor.stwagene.bluecheck.Model.TISensorTagData;
import com.bachelor.stwagene.bluecheck.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Handles the communication between all Fragments and the events of the main layout.
 *
 * Created by stwagene on 02.05.2016.
 */
public class MainActivity extends AppCompatActivity
{
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private ArrayList<String> logTexts = new ArrayList<>();
    private boolean isScanOneFinished = false;
    private boolean isClose = false;
    private LinearLayout buttonBar;
    private DevicesListAdapter deviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner bleScanner;
    private ScanCallback bleCallback;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    public BluetoothHandler handler = new BluetoothHandler(this);
    private BluetoothGatt mGatt;
    private ListView devicesList;
    private boolean isBleScanning = false;
    private Button scanTwo;
    private int currentDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);

        buttonBar = (LinearLayout) findViewById(R.id.button_bar_scans);

        writeToLog("BlueCheck wurde gestartet.");

        initActionBar();

        initMenu();

        initButtonScans();

        initDeviceList();
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
        if (requestCode == 1)
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
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initActionBar()
    {
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setCustomView(R.layout.toolbar_layout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            buttonBar.setElevation(30);
        }
    }

    private void initMenu()
    {
        ImageView menu = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.menu_icon);
        menu.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                writeToLog("Menü-Icon angeklickt.");
                OptionsFragment fragment = (OptionsFragment) getSupportFragmentManager().findFragmentByTag("OptionsFragment");
                if (fragment != null)
                {
                    onBackPressed();
                }
                else
                {
                    openFragment(new OptionsFragment(), "OptionsFragment");
                }

            }
        });
    }

    private void initButtonScans()
    {
        Button scanOne = (Button) findViewById(R.id.button_scan_one);
        scanTwo = (Button) findViewById(R.id.button_scan_two);

        if (scanOne != null)
        {
            scanOne.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    scanTwo.setTextColor(getResources().getColor(R.color.grey));
                    writeToLog("Scan 1 Button wurde gedrückt.");
                    isScanOneFinished = false;
                    if (mBluetoothAdapter.isDiscovering() || isBleScanning)
                    {
                        writeToLog("Scan 1 Button wurde während des laufenden Scans erneut gedrückt.");
                        Toast.makeText(getApplicationContext(), "Es wird bereits gescannt.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        ShowProgressFragment fragment = new ShowProgressFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("PROGRESS", "Scanne nach Geräten...");
                        fragment.setArguments(bundle);
                        openFragment(fragment, "ShowProgressFragment");
                        deviceListAdapter.clear();
                        deviceListAdapter.notifyDataSetChanged();
                        startBleScan();
                    }
                }
            });
        }

        if (scanTwo != null)
        {
            scanTwo.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (isScanOneFinished)
                    {
                        Toast.makeText(getApplicationContext(), "Noch nicht verfügbar.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Scan 1 wurde noch nicht ausgeführt.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void initDeviceList()
    {
        devicesList = (ListView) findViewById(R.id.ble_device_list);
        deviceListAdapter = new DevicesListAdapter(this, R.layout.device_list_item, new ArrayList<BleDevice>());

        if (devicesList != null)
        {
            devicesList.setAdapter(deviceListAdapter);
        }
    }

    public void performDeviceListItemClick(int position)
    {
        connectToDevice(deviceListAdapter.getItem(position).getDevice());
        writeToLog(deviceListAdapter.getItem(position).getName() + " wurde ausgewählt.");
        setCurrentDevice(position);
        ShowProgressFragment fragment = new ShowProgressFragment();
        Bundle bundle = new Bundle();
        bundle.putString("PROGRESS", "Verbinde...");
        fragment.setArguments(bundle);
        openFragment(fragment, "ShowProgressFragment");
    }

    public void connectToDevice(BluetoothDevice device)
    {
        writeToLog("Starte Verbindung zu " + device.getName());
        mGatt = device.connectGatt(this, false, new BluetoothCallback(new TISensorTagData(), this));
    }

    public void openFragment(Fragment fragment, String name)
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
        ft.add(R.id.activity_layout, fragment, name);
        ft.addToBackStack(name);
        ft.commit();
        isClose = false;
        if (name.equals("LogFragment"))
        {
            buttonBar.setVisibility(View.GONE);
        }
        if (name.equals("OptionsFragment") || name.equals("ShowProgressFragment"))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                buttonBar.setElevation(0);
            }
        }
        writeToLog(name + " wurde geöffnet.");
    }

    public void writeToLog(String text)
    {
        Log.d("BLUECHECK_ANDROID_APP", text);
        logTexts.add(getCurrentTimeString() + " --- " + text);
        LogFragment fragment = (LogFragment) getSupportFragmentManager().findFragmentByTag("LogFragment");
        if (fragment != null)
        {
            fragment.appendLogText(getCurrentTimeString() + " --- " + text);
        }
    }

    public String getCurrentTimeString()
    {
        StringBuilder time = new StringBuilder();
        Calendar c = Calendar.getInstance();

        int hour = c.get(Calendar.HOUR_OF_DAY);
        if (hour < 10)
        {
            time.append("0");
        }
        time.append(hour);
        time.append(":");

        int minute = c.get(Calendar.MINUTE);
        if (minute < 10)
        {
            time.append("0");
        }
        time.append(minute);
        time.append(":");

        int seconds = c.get(Calendar.SECOND);
        if (seconds < 10)
        {
            time.append("0");
        }
        time.append(seconds);
        time.append(":");

        int milliseconds = c.get(Calendar.MILLISECOND);
        if (milliseconds < 10)
        {
            time.append("0");
        }
        else if (milliseconds < 100)
        {
            time.append("0");
        }
        time.append(milliseconds);

        return time.toString();
    }

    @Override
    public void onBackPressed()
    {
        writeToLog("BackButton wurde gedrückt.");
        if (getSupportFragmentManager().getBackStackEntryCount() == 0)
        {
            if (!isClose)
            {
                isClose = true;
                Toast.makeText(getApplicationContext(), "Erneut drücken zum Beenden.", Toast.LENGTH_LONG).show();
            }
            else
            {
                MainActivity.this.finish();
            }
            buttonBar.setVisibility(View.VISIBLE);
        }
        String lastFragmentName = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount()-1).getName();
        if (lastFragmentName.equals("ShowProgressFragment"))
        {
            if (this.isBleScanning || mBluetoothAdapter.isDiscovering())
            {
                stopBleScan();
            }
            else if (mGatt != null)
            {
                mGatt.disconnect();
                mGatt.close();
            }
            buttonBar.setVisibility(View.VISIBLE);
        }
        if (lastFragmentName.equals("OptionsFragment"))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                buttonBar.setElevation(30);
            }
        }
        if (lastFragmentName.equals("DeviceValueFragment"))
        {
            if (mGatt != null)
            {
                mGatt.disconnect();
                mGatt.close();
            }
        }
        if (getSupportFragmentManager().getBackStackEntryCount() == 1)
        {
            buttonBar.setVisibility(View.VISIBLE);
            super.onBackPressed();
        }
        if (getSupportFragmentManager().getBackStackEntryCount() > 1)
        {
            super.onBackPressed();
        }
    }


    private void startBleScan()
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
            Toast.makeText(getApplicationContext(), "Scanne nach Geräten...", Toast.LENGTH_SHORT).show();
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
                            if (!deviceListAdapter.contains(result.getDevice().getName()))
                            {
                                addDevice(new BleDevice(result.getDevice().getName(), result.getDevice()));
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
                                if (!deviceListAdapter.contains(device.getName()))
                                {
                                    addDevice(new BleDevice(device.getName(), device));
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
                    stopBleScan();
                }
            }, 5000);
        }


    }

    private void refreshLoadFragment(String s)
    {
        ShowProgressFragment fragment = (ShowProgressFragment) getSupportFragmentManager().findFragmentByTag("ShowProgressFragment");
        if (fragment != null)
        {
            fragment.changeProgressText(s);
        }
    }

    private void stopBleScan()
    {
        refreshLoadFragment("Scannen abgeschlossen...");
        writeToLog("Scan 1 ist abgeschlossen.");
        isScanOneFinished = true;
        scanTwo.setTextColor(getResources().getColor(android.R.color.white));
        Toast.makeText(getApplicationContext(), "Scan 1 ist abgeschlossen.", Toast.LENGTH_SHORT).show();
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
    }

    private void closeProgressFragment()
    {
        ShowProgressFragment fragment = (ShowProgressFragment) getSupportFragmentManager().findFragmentByTag("ShowProgressFragment");
        if (fragment != null)
        {
            fragment.close();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                buttonBar.setElevation(30);
            }
        }
    }

    private void addDevice(BleDevice bleDevice)
    {
        deviceListAdapter.add(bleDevice);
        deviceListAdapter.notifyDataSetChanged();
        writeToLog("Gerät (" + bleDevice.getName() + ") gefunden.");
        Toast.makeText(getApplicationContext(), "Gerät (" + bleDevice.getName() + ") gefunden.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy()
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
        super.onDestroy();
    }

    @Override
    protected void onResume()
    {
        checkBluetoothOfDevice();
        super.onResume();
    }

    public void setValue(double value)
    {
        DeviceValueFragment devceValueFragment = (DeviceValueFragment) getSupportFragmentManager().findFragmentByTag("DeviceValueFragment");
        if (devceValueFragment == null)
        {
            DeviceValueFragment fragment = new DeviceValueFragment();
            Bundle bundle = new Bundle();
            bundle.putDouble("VALUE", value);
            bundle.putString("NAME", deviceListAdapter.getItem(getCurrentDevice()).getName());
            fragment.setArguments(bundle);
            openFragment(fragment, "DeviceValueFragment");
        }
        else
        {
            devceValueFragment.setValue(value);
        }
    }

    public void sendData(String deviceValue)
    {
        if (isNetworkConnected())
        {
            CloudConnectionManager manager = new CloudConnectionManager(this);
            manager.execute(deviceValue);
        }
        else
        {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Es besteht keine Internetverbindung. Willst du eine Verbindung herstellen?");
            builder.setPositiveButton("Aktivieren",
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, final int i)
                        {
                            startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                        }
                    });
            builder.setNegativeButton("Ablehnen",
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, final int i)
                        {
                            Toast.makeText(getApplicationContext(), "Die Daten wurden nicht gesendet.", Toast.LENGTH_SHORT).show();
                        }
                    });
            builder.create().show();
        }
    }

    private boolean isNetworkConnected()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public ArrayList<String> getLogTexts()
    {
        return this.logTexts;
    }

    public void setCurrentDevice(int currentDevice)
    {
        this.currentDevice = currentDevice;
    }

    public int getCurrentDevice()
    {
        return currentDevice;
    }
}
