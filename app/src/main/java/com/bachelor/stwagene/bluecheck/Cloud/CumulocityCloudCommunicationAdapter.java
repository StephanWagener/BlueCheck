package com.bachelor.stwagene.bluecheck.Cloud;

import android.os.AsyncTask;
import android.util.Log;

import com.bachelor.stwagene.bluecheck.Model.BluetoothTag;
import com.bachelor.stwagene.bluecheck.Model.Delivery;
import com.bachelor.stwagene.bluecheck.Model.DeviceValue;
import com.bachelor.stwagene.bluecheck.Model.MeasurementValue;
import com.bachelor.stwagene.bluecheck.Model.Source;
import com.bachelor.stwagene.bluecheck.Model.ValueType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
public class CumulocityCloudCommunicationAdapter extends AsyncTask implements ICloudCommunication
{
    private String httpUser;
    private String httpPwd;
    private String httpHost;

    //TODO senden der Liste bzw Lieferung umsetzen
    public CumulocityCloudCommunicationAdapter() {}

    @Override
    protected Object doInBackground(Object[] params)
    {
        init();
        Delivery delivery = null;
        if (params[0] instanceof Delivery)
        {
            //TODO noch umsetzen
            delivery = new Delivery(new ArrayList<BluetoothTag>(), "DEF456");
        }
        else
        {
            post(getMeasurementJsonString((Double) params[0]));
        }
        return delivery;
    }

    private void init()
    {
        //TODO den Anwender Auswählen lassen
        this.httpUser = "device_blueTag";
        this.httpPwd = "VXnzzlVbQR";
        this.httpHost = "https://asterix.ram.m2m.telekom.com/";
    }

    private Map<String, String> post(String jSon)
    {
        String addURL = "measurement/measurements";
        String type = "POST";
        return sendRestCall(type, jSon, addURL);
    }

    private Map<String, String> sendRestCall(String type, String jSon, String addURL)
    {
        Log.d(this.getClass().getSimpleName(), "Initialisieren des" + type + "-Befehls.");
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
            Log.d(this.getClass().getSimpleName(),"HTTP-Verbindung hergestellt.");
            OutputStream out = httpsCon.getOutputStream();

            OutputStreamWriter osw = new OutputStreamWriter(out);
            osw.write(jSon);
            osw.flush();
            Log.d(this.getClass().getSimpleName(), type + "-REST-Befehl wurde abgesetzt.");
            // R�ckgabe lesen und zur�ckgeben.
            // Bearbeiten des R�ckgabestrings
            credentialsMap = convertString(httpsCon);

            osw.close();
            httpsCon.disconnect();
            Log.d(this.getClass().getSimpleName(), "Response der Cloud: " + credentialsMap.toString());
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

        if (parts[parts.length - 1].equals("measurements"))
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

        return credentialsMap;
    }

    private String printReader(HttpsURLConnection httpsCon) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(httpsCon.getInputStream()));
        return reader.readLine();
    }

    private String getMeasurementJsonString(double value)
    {
        MeasurementValue measurementValue = new MeasurementValue();
        DeviceValue deviceTemparature = new DeviceValue();
        ValueType tempType = new ValueType();
        tempType.setValue(value);
        tempType.setUnit("°C");
        deviceTemparature.setValueType(tempType);
        measurementValue.setDeviceValue(deviceTemparature);
        Source source = new Source();
        source.setId("4045668");
        measurementValue.setSource(source);
        measurementValue.setType("deviceTemparature");
        measurementValue.getTime();
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        try
        {
            json = mapper.writeValueAsString(measurementValue);
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    public void sendMeasurement(double value)
    {
        this.execute(value);
    }

    @Override
    public void sendDelivery(Delivery delivery)
    {
        //TODO implementieren
    }
}
