package com.bachelor.stwagene.bluecheck.Cloud;

import android.os.AsyncTask;
import android.widget.Toast;

import com.bachelor.stwagene.bluecheck.Fragments.DevicesListFragment;
import com.bachelor.stwagene.bluecheck.Main.MainActivity;
import com.bachelor.stwagene.bluecheck.Model.BluetoothTag;
import com.bachelor.stwagene.bluecheck.Model.DeliveryProduct;

import java.util.ArrayList;

/**
 * Created by stwagene on 31.08.2016.
 */
public class TelekomCloudMock extends AsyncTask implements ICloudCommunication
{
    private final MainActivity activity;
    private ArrayList<BluetoothTag> tags = new ArrayList<>();

    public TelekomCloudMock(MainActivity activity)
    {
        this.activity = activity;
    }

    @Override
    public void sendMeasurement(double value)
    {
        this.execute(value);
    }

    @Override
    public void sendBlePackageList(ArrayList<String> addresses, String deliveryID)
    {
        this.execute(addresses, deliveryID);
    }

    @Override
    protected Object doInBackground(Object[] params)
    {
        try
        {
            Thread.sleep(2500);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        if (params.length == 2)
        {
            DevicesListFragment fragment = (DevicesListFragment) this.activity.getSupportFragmentManager().findFragmentByTag(DevicesListFragment.class.getSimpleName());
            if (fragment != null)
            {
                tags.addAll(fragment.getDevicesList());
            }
            if (tags.size() < 3)
            {
                tags.add(new BluetoothTag("Tag 1", "AA:BB:CC:DD:EE:FF"));
                tags.add(new BluetoothTag("Tag 2", "A1:B2:C3:D4:E5:F6"));
                tags.add(new BluetoothTag("Tag 3", "00:11:22:22:11:00"));
            }
            for (int i = 0; i < tags.size(); i++)
            {
                tags.get(i).setProduct(new DeliveryProduct("Ware " + i, i, (i*123456)+""));
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Object o)
    {
        if(o == true)
        {
            this.activity.setDeviceList(tags);
            this.activity.closeProgressFragment();
        }
        else
        {
            Toast.makeText(activity.getApplicationContext(), "Die Daten wurden gesendet.", Toast.LENGTH_SHORT).show();
        }
    }
}
