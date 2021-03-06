package com.bachelor.stwagene.bluecheck.Cloud;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.bachelor.stwagene.bluecheck.Fragments.DeliveryResultFragment;
import com.bachelor.stwagene.bluecheck.Main.MainActivity;
import com.bachelor.stwagene.bluecheck.Model.Delivery;

/**
 * Created by stwagene on 19.08.2016.
 */
public class CloudConnectionInitiator
{
    private final MainActivity activity;
    private String measurementToSend = null;
    private Delivery deliveryToSend = null;

    public CloudConnectionInitiator(MainActivity activity)
    {
        this.activity = activity;
    }

    private boolean isNetworkConnected()
    {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public void repeatSending()
    {
        if (!isNetworkConnected())
        {
            getNoNetworkDialog().show();
        }
        else
        {
            AlertDialog.Builder builder = getDialog("Sollen die Daten erneut gesendet werden?");
            builder.setPositiveButton("Senden",
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, final int i)
                        {
                            send();
                        }
                    });
            builder.show();
        }
    }

    private AlertDialog.Builder getNoNetworkDialog()
    {
        AlertDialog.Builder builder = getDialog("Es besteht keine Internetverbindung. Willst du eine Verbindung herstellen?");
        builder.setPositiveButton("Aktivieren",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, final int i)
                    {
                        activity.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                    }
                });
        return builder;
    }

    public boolean sendDelivery(final Delivery delivery)
    {
        this.deliveryToSend = delivery;

        boolean isSuccessful = send();
        if (isSuccessful)
        {
            this.deliveryToSend = null;
        }

        return isSuccessful;
    }

    public boolean sendMeasurement(final String measurement)
    {
        this.measurementToSend = measurement;

        boolean isSuccessful = send();
        if (isSuccessful)
        {
            this.measurementToSend = null;
        }

        return isSuccessful;
    }

    private boolean send()
    {
        //TODO Mock entfernen, wenn alles implementiert wurde
        //TODO lieferung umsetzen und dummy entfernen
        boolean isConnected = isNetworkConnected();
        if (isConnected)
        {
            this.activity.newProgress("Sende Daten...");
            //TODO adapter nur einmal initialisieren
            ICloudCommunication adapter = null;
            if (this.measurementToSend != null)
            {
                adapter = CloudCommunicationFactory.getCloudAdapter(CloudCommunicationFactory.CloudAdapterType.CUMULOCITY);
                adapter.sendMeasurement(Double.parseDouble(this.measurementToSend));
            }
            else if (this.deliveryToSend != null)
            {
                adapter = CloudCommunicationFactory.getCloudAdapter(CloudCommunicationFactory.CloudAdapterType.MOCK);
                adapter.sendDelivery(this.deliveryToSend);
            }
            checkAdapterState(adapter);
        }
        else
        {
            activity.closeProgressFragment();
            getNoNetworkDialog().show();
        }
        return isConnected;
    }

    private static Handler handler = new Handler(Looper.getMainLooper());

    private void checkAdapterState(final ICloudCommunication adapter)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                boolean isFinished = false;
                while (!isFinished)
                {
                    if (AsyncTask.Status.FINISHED.equals(((AsyncTask)adapter).getStatus()))
                    {
                        isFinished = true;
                    }
                }

                Delivery delivery = null;
                try
                {
                    delivery = (Delivery) ((AsyncTask)adapter).get();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                if (delivery != null)
                {
                    final Delivery finalDelivery = delivery;
                    handler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            activity.setDeviceList(finalDelivery.getPackages());
                        }
                    });
                }
                else
                {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(activity, "Die Daten wurden gesendet.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        activity.closeProgressFragment();
                        activity.openFragment(new DeliveryResultFragment());
                    }
                });
            }
        }).start();
    }

    private AlertDialog.Builder getDialog (String message)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        builder.setNegativeButton("Abbrechen",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, final int i)
                    {
                        CloudConnectionInitiator.this.measurementToSend = null;
                        CloudConnectionInitiator.this.deliveryToSend = null;
                        Toast.makeText(activity, "Die Daten wurden nicht gesendet.", Toast.LENGTH_SHORT).show();
                    }
                });
        builder.create();
        return builder;
    }
}
