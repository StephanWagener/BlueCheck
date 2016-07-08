package com.bachelor.stwagene.bluecheck.Model;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by stwagene on 20.06.2016.
 */
public class MeasurementValue
{
    private String name;
    private String type;
    private String dataType;
    private String date;
    private String id;
    private String value;
    private long timeInMillis;

    public MeasurementValue () {}

    public MeasurementValue (String name, String type, String dType, String id, String value)
    {
        this.name = name;
        this.type = type;
        this.dataType = dType;
        this.id = id;
        this.value = value;
    }

    public MeasurementValue (String name, String type, String dType, String date, String id, String value)
    {
        this.name = name;
        this.type = type;
        this.dataType = dType;
        this.date = date;
        this.id = id;
        this.value = value;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getDataType()
    {
        return dataType;
    }

    public void setDataType(String dataType)
    {
        this.dataType = dataType;
    }

    public String getDate()
    {
        if (date == null)
        {
            date = getCurrentTime(new GregorianCalendar());
        }
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
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

    public String getJsonString()
    {
        return "{\"" + name + "\":{\"" + type + "\":{\"value\":" + value + ",\"unit\":\"" + dataType + "\"}},\"time\":\"" + getDate() + "\",\"source\":{\"id\":\"" + id
                + "\"},\"type\":\"" + name + "\"}";
    }
}
