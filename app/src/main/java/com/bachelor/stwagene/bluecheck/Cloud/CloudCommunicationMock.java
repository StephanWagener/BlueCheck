package com.bachelor.stwagene.bluecheck.Cloud;

import android.os.AsyncTask;

import com.bachelor.stwagene.bluecheck.Main.MainActivity;
import com.bachelor.stwagene.bluecheck.Model.BluetoothTag;
import com.bachelor.stwagene.bluecheck.Model.Delivery;
import com.bachelor.stwagene.bluecheck.Model.DeliveryProduct;

import java.util.ArrayList;

/**
 * Created by stwagene on 31.08.2016.
 */
public class CloudCommunicationMock extends AsyncTask implements ICloudCommunication
{
    private ArrayList<Delivery> deliveries = new ArrayList<>();

    public CloudCommunicationMock(){}

    private void init()
    {
        //erste Lieferung initialisieren
        ArrayList<BluetoothTag> tags1 = new ArrayList<>();
        BluetoothTag tag1 = new BluetoothTag("CC2650 SensorTag", "B0:B4:48:BD:AF:85");
        tag1.setProduct(new DeliveryProduct("Max Mustermann\nMusterstraße 21\n12345 Musterstadt", "12kg, 17cm", "1213HVFH"));
        tags1.add(tag1);

        BluetoothTag tag2 = new BluetoothTag("Tag 2", "AA:BB:00:00:EE:FF");
        tag2.setProduct(new DeliveryProduct("Max Mustermann\nMusterstraße 21\n12345 Musterstadt", "54kg, 120cm", "3423JIOP"));
        tags1.add(tag2);

        BluetoothTag tag3 = new BluetoothTag("Tag 3", "11:11:00:00:11:11");
        tag3.setProduct(new DeliveryProduct("Max Mustermann\nMusterstraße 21\n12345 Musterstadt", "20kg, 43cm", "3456ASXD"));
        tags1.add(tag3);

        BluetoothTag tag4 = new BluetoothTag("Tag 4", "AA:AA:AA:AA:AA:AA");
        tag4.setProduct(new DeliveryProduct("Max Mustermann\nMusterstraße 21\n12345 Musterstadt", "33kg, 75cm", "23846DIK"));
        tags1.add(tag4);
        deliveries.add(new Delivery(tags1, "ABCD1234"));

        //zweite Lieferung initialisieren
        ArrayList<BluetoothTag> tags2 = new ArrayList<>();
        BluetoothTag tag5 = new BluetoothTag("Tag 1", "FF:FF:EE:FF:EE:FF");
        tag5.setProduct(new DeliveryProduct("Max Mustermann\nMusterstraße 21\n12345 Musterstadt", "4kg, 30cm", "234DFXSX"));
        tags2.add(tag5);

        BluetoothTag tag6 = new BluetoothTag("Tag 2", "BB:AA:BB:AA:CC:FF");
        tag6.setProduct(new DeliveryProduct("Max Mustermann\nMusterstraße 21\n12345 Musterstadt", "24kg, 84cm", "2345DFGF"));
        tags2.add(tag6);

        BluetoothTag tag7 = new BluetoothTag("Tag 3", "56:34:89:A1:CC:12");
        tag7.setProduct(new DeliveryProduct("Max Mustermann\nMusterstraße 21\n12345 Musterstadt", "2kg, 8cm", "6569OKOP"));
        tags2.add(tag7);

        BluetoothTag tag8 = new BluetoothTag("Tag 4", "DD:C3:B4:A2:FF:A6");
        tag8.setProduct(new DeliveryProduct("Max Mustermann\nMusterstraße 21\n12345 Musterstadt", "32kg, 94cm", "5789BCXV"));
        tags2.add(tag8);
        deliveries.add(new Delivery(tags2, "7890VBNM"));
    }

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
        if (deliveries.isEmpty())
        {
            init();
        }

        try
        {
            Thread.sleep(2500);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        if (params[0] instanceof Delivery)
        {
            return getCheckedDelivery((Delivery) params[0]);
        }

        return null;
    }

    private Delivery getCheckedDelivery(Delivery delivery)
    {
        for (BluetoothTag bluetoothTag : delivery.getPackages())
        {
            checkLoadStatus(bluetoothTag, delivery.getId());
        }

        checkMissingPackages(delivery);

        return delivery;
    }

    private void checkMissingPackages(Delivery delivery)
    {
        for (BluetoothTag tag : getRightDelivery(delivery.getId()).getPackages())
        {
            if (!getBluetoothAddresses(delivery.getPackages()).contains(tag.getAddress()))
            {
                tag.getProduct().setLoadStatus(MainActivity.MISSING_PRODUCT);
                delivery.getPackages().add(tag);
            }
        }
    }

    private void checkLoadStatus(BluetoothTag tagToProof, String id)
    {
        outerloop : for (Delivery currentDelivery : deliveries)
        {
            boolean isRightDelivery = false;
            if (currentDelivery.getId().equals(getRightDelivery(id).getId()))
            {
                isRightDelivery = true;
            }
            for (BluetoothTag tag : currentDelivery.getPackages())
            {
                boolean found = tag.getAddress().equals(tagToProof.getAddress());
                if (found)
                {
                    tagToProof.setProduct(tag.getProduct());
                    if (isRightDelivery)
                    {
                        tagToProof.getProduct().setLoadStatus(MainActivity.RIGHT_LOADED_PRODUCT);
                    }
                    else
                    {
                        tagToProof.getProduct().setLoadStatus(MainActivity.FALSE_LOADED_PRODUCT);
                    }
                    break outerloop;
                }
            }
        }
        if (tagToProof.getProduct() == null)
        {
            tagToProof.setProduct(new DeliveryProduct("N/A", "N/A", MainActivity.UNKNOWN_PRODUCT,"N/A"));
        }
    }

    private Delivery getRightDelivery(String id)
    {
        Delivery rightDelivery = null;
        for (int i = 0; i < deliveries.size(); i++)
        {
            if (deliveries.get(i).getId().equals(id))
            {
                rightDelivery = deliveries.get(i);
            }
        }
        return rightDelivery;
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
}