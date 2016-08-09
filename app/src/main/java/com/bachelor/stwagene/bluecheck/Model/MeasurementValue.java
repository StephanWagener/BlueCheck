package com.bachelor.stwagene.bluecheck.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by stwagene on 20.06.2016.
 */
public class MeasurementValue
{
    private DeviceTemperature deviceTemparature;
    private String time;
    private Source source;
    private String type;
    @JsonIgnore
    private long timeInMillis;

    public MeasurementValue () {}

    public MeasurementValue (DeviceTemperature deviceTemparature,Source source)
    {
        this.deviceTemparature = deviceTemparature;
        this.source = source;
    }

    public MeasurementValue (DeviceTemperature deviceTemparature, String time, Source source)
    {
        this.deviceTemparature = deviceTemparature;
        this.time = time;
        this.source = source;
    }

    public DeviceTemperature getDeviceTemparature()
    {
        return deviceTemparature;
    }

    public void setDeviceTemparature(DeviceTemperature deviceTemparature)
    {
        this.deviceTemparature = deviceTemparature;
    }

    public String getTime()
    {
        if (time == null)
        {
            time = getCurrentTime(new GregorianCalendar());
        }
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public Source getSource()
    {
        return source;
    }

    public void setSource(Source source)
    {
        this.source = source;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * Liefert die Zeit von der DateTime zur�ck, in dem Format, welches die
     * Cloud erwartet
     *
     * @return String aktuelle Zeit
     */
    private String getCurrentTime(GregorianCalendar gc)
    {
        timeInMillis = gc.getTimeInMillis();
        String y = String.valueOf(gc.get(Calendar.YEAR));
        // Monat plus 1, weil GregorianCalendar Monat mit 0 beginnt.
        String m = String.valueOf(gc.get(Calendar.MONTH) + 1);
        String d = String.valueOf(gc.get(Calendar.DAY_OF_MONTH));
        // UTC in Zeitzone Berlin
        String h = String.valueOf(gc.get(Calendar.HOUR_OF_DAY));
        String min = String.valueOf(gc.get(Calendar.MINUTE));
        String sec = String.valueOf(gc.get(Calendar.SECOND));

        return convertTime(y, m, d, h, min, sec);
    }

    /**
     * Konvertiert anhand der �bergebenen einzel Elemente die Zeit in das von
     * der CoT erwartete
     *
     * @param year   Jahr f�r CoT Zeit
     * @param month  Monat f�r CoT Zeit
     * @param day    Tag f�r CoT Zeit
     * @param hours  Stunden f�r CoT Zeit
     * @param minute Minuten f�r CoT Zeit
     * @param second Sekunden f�r CoT Zeit
     * @return
     */
    private String convertTime(String year, String month, String day, String hours, String minute, String second)
    {
        String result = "";
        String tzString = "";

        // Offset für die System-Default Timezone unter Einbeziehung von Sommer-/Winterzeit.
        int tzOffset = TimeZone.getDefault().getOffset(timeInMillis) / 60 / 60 / 1000;

        // Vorzeichen f�r die Timezone bestimmen.
        switch (Integer.signum(tzOffset))
        {
            case 1:
                // Timezone positiv
                result = "+";
                break;
            case -1:
                // Timezone negativ
                result = "-";
                break;
            default:
                result = "+";
                break;
        }
        if (Math.abs(tzOffset) < 10)
        {
            // String auf zwei Stellen erweitern f�r Timezone
            tzString = "0" + String.valueOf(tzOffset) + ":00";
        }
        else
        {
            tzString = String.valueOf(tzOffset) + ":00";
        }
        return year + "-" + month + "-" + day + "T" + hours + ":" + minute + ":" + second + result + tzString;
    }
}
