package com.bachelor.stwagene.bluecheck.Model;

/**
 * Created by stwagene on 02.06.2016.
 */
public class ChooserListItem
{
    private String text;
    private int value;

    public ChooserListItem (int value)
    {
        this.value = value;
        this.text = value+"";
    }

    public ChooserListItem (int value, String text)
    {
        this.value = value;
        this.text = text;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public int getValue()
    {
        return value;
    }

    public void setValue(int value)
    {
        this.value = value;
    }
}
