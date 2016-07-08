package com.bachelor.stwagene.bluecheck.Cloud;

import android.os.AsyncTask;
import android.widget.Toast;

import com.bachelor.stwagene.bluecheck.Main.MainActivity;
import com.bachelor.stwagene.bluecheck.Model.MeasurementValue;

import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Manages the communication with the Cloud.
 *
 * Created by stwagene on 04.05.2016.
 */
public class CloudConnectionManager extends AsyncTask
{
    private final MainActivity activity;
    private String httpUser;
    private String httpPwd;
    private String httpHost;
    private boolean isPost;
    private ArrayList<String> deviceIdList = new ArrayList<>();

    public CloudConnectionManager (MainActivity activity)
    {
        this.activity = activity;
    }

    @Override
    protected Object doInBackground(Object[] params)
    {
        init();
        Map<String, String> response;
        isPost = (boolean) params[0];
        if (isPost)
        {
            response = post(getPostJsonString((String) params[1]));
        }
        else
        {
            deviceIdList = (ArrayList<String>) params[1];
            response = put(getPutJsonString());
        }
        return response;
    }

    private void init()
    {
        this.httpUser = "device_blueTag";
        this.httpPwd = "VXnzzlVbQR";
        this.httpHost = "https://asterix.ram.m2m.telekom.com/";
    }

    @Override
    protected void onPostExecute(Object o)
    {
        if (isPost)
        {
            activity.writeToLog("Senden der Daten abgeschlossen. Gesendeter Wert: " + ((Map<String, String>)o).get("value"));
            Toast.makeText(activity.getApplicationContext(), "Der Wert wurde gesendet.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            activity.writeToLog("Senden der Liste abgeschlossen. Gesendete Liste: " + deviceIdList.toString());
            Toast.makeText(activity.getApplicationContext(), "Die Liste wurde gesendet.", Toast.LENGTH_SHORT).show();
        }
    }

    private Map<String, String> put(String jSon)
    {
        String addURL = "inventory/managedObjects/4045668";
        String type = "PUT";
        return sendRestCall(type, jSon, addURL);
    }

    private Map<String, String> post(String jSon)
    {
        String addURL = "measurement/measurements";
        String type = "POST";
        return sendRestCall(type, jSon, addURL);
    }

    private Map<String, String> sendRestCall(String type, String jSon, String addURL)
    {
        activity.writeToLog("Initialisieren des" + type + "-Befehls.");
        Map<String, String> credentialsMap = new HashMap<String, String>();
        // URL-Verbindung in die Cloud herstellen
        try
        {
            URL url = newURL(addURL);
            // Verbindung �ffnen
            HttpsURLConnection httpsCon = (HttpsURLConnection) url.openConnection();
            // Output und Inout aktivieren
            httpsCon.setDoOutput(true);
            httpsCon.setDoInput(true);
            // RequestMode auf den Type stellen
            httpsCon.setRequestMethod(type);

            httpsCon = createHttpsCon(httpsCon, addURL);
            // Json String per REST absetzen
            activity.writeToLog("HTTP-Verbindung hergestellt.");
            OutputStream out = httpsCon.getOutputStream();

            OutputStreamWriter osw = new OutputStreamWriter(out);
            osw.write(jSon);
            osw.flush();
            activity.writeToLog(type + "-REST-Befehl wurde abgesetzt.");
            // R�ckgabe lesen und zur�ckgeben.
            // Bearbeiten des R�ckgabestrings
            credentialsMap = convertString(httpsCon);

            osw.close();
            httpsCon.disconnect();
            activity.writeToLog("Response der Cloud: " + credentialsMap.toString());
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        return credentialsMap;
    }

    private HttpsURLConnection createHttpsCon(HttpsURLConnection httpsCon, String addUrl) {
        httpsCon.setUseCaches(false);
        byte[] base64Bytes = Base64.encodeBase64((this.httpUser + ":" + this.httpPwd).getBytes());
        String auth = new String(base64Bytes);

        // Einstellungen f�r die Verbindung setzen
        httpsCon.setRequestProperty("Authorization", "Basic " + auth);
        httpsCon.setRequestProperty("X-Id", "voith");
        String[] parts = addUrl.split("/");
        String type = "";

        // Notwendig da sonst Error 406.
        if (!isPost && parts[parts.length - 2].equals("managedObjects"))
        {
            type = "managedObject";
        }
        else if (parts[parts.length - 1].equals("measurements"))
        {
            type = "measurement";
        }

        String PREFIX_CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.";
        httpsCon.setRequestProperty("Content-Type", PREFIX_CONTENT_TYPE + type + "+json");
        httpsCon.setRequestProperty("Accept", PREFIX_CONTENT_TYPE + type + "+json");

        return httpsCon;
    }

    private URL newURL(String url) throws IOException
    {
        Authenticator.setDefault(new Authenticator()
        {
            @Override
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(httpUser, httpPwd.toCharArray());
            }
        });
        return new URL(this.httpHost + url);
    }

    private Map<String, String> convertString(HttpsURLConnection httpsCon) throws IOException
    {
        String line = printReader(httpsCon);
        line = line.replace("\"", "");
        line = line.replace("{", "");
        line = line.replace("}", "");
        Map<String, String> credentialsMap = new HashMap<String, String>();

        // bearbeiten der R�ckgabe von Cumulocity

        if (isPost)
        {
            String[] lineparts = line.split(",");
            // Alle Inhalte in eine Map schreiben.
            for (String linepart : lineparts)
            {
                String key = linepart.split(":")[0];
                String value;
                try
                {
                    value = linepart.split(":")[1];
                } catch (Exception e)
                {
                    value = "";
                }
                credentialsMap.put(key, value);
            }
        }
        else
        {
            String key = "ID_LIST";
            credentialsMap.put(key, line);
        }

        return credentialsMap;
    }

    private String printReader(HttpsURLConnection httpsCon) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(httpsCon.getInputStream()));
        return reader.readLine();
    }

    private String getPutJsonString()
    {
        String sn = "blueTagIdList";
        String type = "list";
        StringBuilder list = new StringBuilder();
        for (int i = 0; i < deviceIdList.size(); i++)
        {
            list.append("\"");
            list.append(deviceIdList.get(i));
            list.append("\"");
            if (i != (deviceIdList.size()-1))
            {
                list.append(",");
            }
        }
        return "{\"" + sn + "\":{\"" + type + "\":[" + list.toString() + "]}}";
    }

    private String getPostJsonString(String valueOfDevice)
    {
        MeasurementValue measurementValue = new MeasurementValue();
        measurementValue.setName("deviceTemparature");
        measurementValue.setType("tempType");
        measurementValue.setDataType("°C");
        measurementValue.setId("4045668");
        measurementValue.setValue(valueOfDevice);
        return measurementValue.getJsonString();
    }
}
