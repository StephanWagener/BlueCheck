package com.bachelor.stwagene.bluecheck.Model;

import com.bachelor.stwagene.bluecheck.Main.MainActivity;

/**
 * Created by student on 08.10.2016.
 */

public class DeliveryResultListItem
{
    private String text;
    private int number;
    private int type;

    public DeliveryResultListItem(int number, int type)
    {
        this.number = number;
        this.type = type;
        switch (type)
        {
            case MainActivity.FALSE_LOADED_PRODUCT:
                this.text = "Falsch verladen";
                break;
            case MainActivity.RIGHT_LOADED_PRODUCT:
                this.text = "Richtig verladen";
                break;
            case MainActivity.UNKNOWN_PRODUCT:
                this.text = "Unbekannt";
                break;
            case MainActivity.MISSING_PRODUCT:
                this.text = "Fehlend";
                break;
            default:
                break;
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }
}
