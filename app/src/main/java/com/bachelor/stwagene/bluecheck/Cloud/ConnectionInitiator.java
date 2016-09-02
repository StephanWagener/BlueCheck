package com.bachelor.stwagene.bluecheck.Cloud;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.bachelor.stwagene.bluecheck.Main.MainActivity;
import com.bachelor.stwagene.bluecheck.Model.BluetoothTag;

import java.util.ArrayList;

/**
 * Created by stwagene on 19.08.2016.
 */
public class ConnectionInitiator
{
    private final MainActivity activity;
    private String measurementToSend = null;
    private ArrayList<BluetoothTag> tagsToSend = new ArrayList<>();

    public ConnectionInitiator(MainActivity activity)
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
            builder.show();
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

    public boolean sendDevicesList(final ArrayList<BluetoothTag> tags)
    {
        this.tagsToSend = tags;
        boolean isSuccessful = isNetworkConnected();

        if (isSuccessful)
        {
            send();
            tagsToSend = null;
        }

        return isSuccessful;
    }

    public boolean sendMeasurement(final String measurement)
    {
        this.measurementToSend = measurement;
        boolean isSuccessful = isNetworkConnected();

        if (isSuccessful)
        {
            send();
            measurementToSend = null;
        }

        return isSuccessful;
    }

    private void send()
    {
        //TODO Mock entfernen, wenn alles implementiert wurde
        //TODO lieferung umsetzen und dummy entfernen
        if (measurementToSend != null)
        {
            TelekomCloudCommunicationAdapter adapter = new TelekomCloudCommunicationAdapter(activity);
            adapter.sendMeasurement(Double.parseDouble(measurementToSend));
        }
        else if (tagsToSend != null)
        {
            TelekomCloudMock mock = new TelekomCloudMock(this.activity);
            mock.sendBlePackageList(this.activity.getBluetoothAddresses(tagsToSend), "Lieferung ABC");
        }
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
                        Toast.makeText(activity, "Die Daten wurden nicht gesendet.", Toast.LENGTH_SHORT).show();
                    }
                });
        builder.create();
        return builder;
    }

}
