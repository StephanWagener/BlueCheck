package com.bachelor.stwagene.bluecheck.Cloud;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.bachelor.stwagene.bluecheck.Main.MainActivity;

/**
 * Created by stwagene on 19.08.2016.
 */
public class ConnectionReviser
{
    private final MainActivity activity;
    private String dataToSend = "";

    public ConnectionReviser (MainActivity activity)
    {
        this.activity = activity;
    }

    private boolean isNetworkConnected()
    {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public boolean tryToSend(final String data, boolean isRepetition)
    {
        if (data != null)
        {
            this.dataToSend = data;
        }
        boolean isSuccessful = isNetworkConnected();

        if (!isSuccessful)
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
        else if (isRepetition)
        {
            AlertDialog.Builder builder = getDialog("Sollen die Daten erneut gesendet werden?");
            builder.setPositiveButton("Senden",
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, final int i)
                        {
                            send(dataToSend);
                        }
                    });
            builder.show();
        }
        else
        {
            send(dataToSend);
        }

        return isSuccessful;
    }

    private void send(String data)
    {
        CloudConnectionManager manager = new CloudConnectionManager(activity);
        manager.execute(true, data);
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
