package com.bachelor.stwagene.bluecheck.Main;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bachelor.stwagene.bluecheck.Bluetooth.BluetoothCallback;
import com.bachelor.stwagene.bluecheck.Bluetooth.BluetoothHandler;
import com.bachelor.stwagene.bluecheck.Cloud.CloudConnectionManager;
import com.bachelor.stwagene.bluecheck.Fragments.LogFragment;
import com.bachelor.stwagene.bluecheck.Fragments.OptionsFragment;
import com.bachelor.stwagene.bluecheck.Fragments.ProgressFragment;
import com.bachelor.stwagene.bluecheck.Fragments.SettingsFragment;
import com.bachelor.stwagene.bluecheck.ListManagement.DevicesListAdapter;
import com.bachelor.stwagene.bluecheck.Model.BleDevice;
import com.bachelor.stwagene.bluecheck.Model.ChooserListItem;
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
    private boolean isBleScanning = false;
    private Button scanTwo;
    private TextView rssiPercentageTextView;
    private boolean isShowUUIDInLog = true;
    private ChooserListItem valueChangedInterval = new ChooserListItem(1, "Jeder");
    private ImageView backButton;
    private boolean isDeveloperMode = true;
    private Button sendIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);

        buttonBar = (LinearLayout) findViewById(R.id.button_bar_scans);

        checkBluetoothOfDevice();

        writeToLog("BlueCheck wurde gestartet.");

        initActionBar();

        initToolbar();

        initButtonScans();

        initDeviceList();

        initSendButton();
    }

    private void initSendButton()
    {
        sendIdList = (Button) findViewById(R.id.send_id_list);

        if (isDeveloperMode())
        {
            sendIdList.setVisibility(View.VISIBLE);
        }
        else
        {
            sendIdList.setVisibility(View.GONE);
        }

        sendIdList.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                initPut();
            }
        });
    }

    private void initPut()
    {
        ArrayList<String> deviceIdList = new ArrayList<>();
        for (int i = 0; i < deviceListAdapter.getItems().size(); i++)
        {
            deviceIdList.add(deviceListAdapter.getItems().get(i).getDevice().getAddress());
        }
        if (deviceIdList.size() < 3)
        {
            deviceIdList.add("00:11:22:AA:BB:CC");
            deviceIdList.add("10:20:30:40:50:60");
            deviceIdList.add("CC:BB:AA:FF:EE:DD");
            deviceIdList.add("00:11:00:11:00:11");
        }
        CloudConnectionManager manager = new CloudConnectionManager(MainActivity.this);
        manager.execute(false, deviceIdList);
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

    private void initToolbar()
    {
        backButton = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.back_icon);
        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });

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

        rssiPercentageTextView = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.connection_rssi_percentage);
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
                        ProgressFragment fragment = new ProgressFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("PROGRESS", "Scanne nach Geräten...");
                        fragment.setArguments(bundle);
                        openFragment(fragment, "ProgressFragment");
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
        ListView devicesList = (ListView) findViewById(R.id.ble_device_list);
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
        mGatt = device.connectGatt(this, false, new BluetoothCallback(this));
    }

    public void openFragment(Fragment fragment, String name)
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
        ft.add(R.id.activity_layout, fragment, name);
        ft.addToBackStack(name);
        ft.commit();

        backButton.setVisibility(View.VISIBLE);
        isClose = false;
        if (name.equals("LogFragment"))
        {
            buttonBar.setVisibility(View.GONE);
        }
        if (name.equals("OptionsFragment") || name.equals("ProgressFragment"))
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
            backButton.setVisibility(View.GONE);
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
            if (lastFragmentName.equals("DeviceValuesListFragment") || lastFragmentName.equals("DeviceServicesListFragment"))
            {
                if (mGatt != null)
                {
                    mGatt.disconnect();
                    mGatt.close();
                }
                this.rssiPercentageTextView.setVisibility(View.GONE);
            }
            if (getSupportFragmentManager().getBackStackEntryCount() == 1)
            {
                backButton.setVisibility(View.GONE);
                buttonBar.setVisibility(View.VISIBLE);
                super.onBackPressed();
            }
            if (getSupportFragmentManager().getBackStackEntryCount() > 1)
            {
                super.onBackPressed();
            }
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
        isScanOneFinished = isFinished;
        scanTwo.setTextColor(getResources().getColor(android.R.color.white));
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

    private void closeProgressFragment()
    {
        ProgressFragment fragment = (ProgressFragment) getSupportFragmentManager().findFragmentByTag("ProgressFragment");
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

    public void sendData(String deviceValue)
    {
        if (isNetworkConnected())
        {
            CloudConnectionManager manager = new CloudConnectionManager(this);
            manager.execute(true, deviceValue);
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
        this.rssiPercentageTextView.setVisibility(View.VISIBLE);
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
            this.rssiPercentageTextView.setText(String.format("%.1f", ((double)rssiValue+100.0)/75.0*100.0)+"%");
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

    public boolean isDeveloperMode()
    {
        return isDeveloperMode;
    }

    public void setBackButtonGone()
    {
        if (getSupportFragmentManager().getBackStackEntryCount() <= 1)
        {
            backButton.setVisibility(View.GONE);
        }
    }

    public void setDeveloperMode(boolean isActive)
    {
        this.isDeveloperMode = isActive;
        if (isDeveloperMode())
        {
            sendIdList.setVisibility(View.VISIBLE);
        }
        else
        {
            sendIdList.setVisibility(View.GONE);
        }
    }
}
