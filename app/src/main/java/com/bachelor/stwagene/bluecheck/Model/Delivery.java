package com.bachelor.stwagene.bluecheck.Model;

import java.util.ArrayList;

/**
 * Created by stwagene on 06.09.2016.
 */
public class Delivery
{
    private String id;
    //TODO List verwenden
    private ArrayList<BluetoothTag> packages;

    public Delivery (ArrayList<BluetoothTag> packages, String id)
    {
        this.packages = packages;
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public ArrayList<BluetoothTag> getPackages()
    {
        return packages;
    }

    public void setPackages(ArrayList<BluetoothTag> packages)
    {
        this.packages = packages;
    }
}
