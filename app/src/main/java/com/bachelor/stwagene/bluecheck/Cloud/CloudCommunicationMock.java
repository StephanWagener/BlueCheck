package com.bachelor.stwagene.bluecheck.Cloud;

import android.os.AsyncTask;

import com.bachelor.stwagene.bluecheck.Model.BluetoothTag;
import com.bachelor.stwagene.bluecheck.Model.Delivery;
import com.bachelor.stwagene.bluecheck.Model.DeliveryProduct;

import java.util.ArrayList;

/**
 * Created by stwagene on 31.08.2016.
 */
public class CloudCommunicationMock extends AsyncTask implements ICloudCommunication
{
    public CloudCommunicationMock(){}

    @Override
    public void sendMeasurement(double value)
    {
        this.execute(value);
    }

    @Override
    public void sendDelivery(Delivery delivery)
    {
        this.execute(delivery);
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

        Delivery delivery = null;
        if (params[0] instanceof Delivery)
        {
            ArrayList<BluetoothTag> tags = new ArrayList<>();
            tags.add(new BluetoothTag("Tag 0", "00:11:22:33:44:55"));
            tags.add(new BluetoothTag("Tag 1", "AA:BB:CC:DD:EE:FF"));
            tags.add(new BluetoothTag("Tag 2", "A1:B2:C3:D4:E5:F6"));
            tags.add(new BluetoothTag("Tag 3", "00:11:22:22:11:00"));

            for (int i = 1; i < tags.size() + 1; i++)
            {
                tags.get(i - 1).setProduct(new DeliveryProduct("Max Mustermann\nMusterstraße 21\n12345 Musterstadt",
                        (i * 12) + "kg, " + (i * 23) + "cm",
                        (i - 1), (i * 123456) + ""));
            }
            delivery = new Delivery(tags, "ABC123");
        }

        return delivery;
    }
}