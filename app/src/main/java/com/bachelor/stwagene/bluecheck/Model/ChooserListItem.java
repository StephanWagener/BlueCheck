package com.bachelor.stwagene.bluecheck.Model;

/**
 * Created by stwagene on 02.06.2016.
 */
public class ChooserListItem
{
    private String text;
    private int value;

    public ChooserListItem (ChooserListOption option)
    {
        switch (option)
        {
            case EVERYONE:
                this.value = 1;
                this.text = "Jeder";
                break;
            case ONLY_ONCE:
                this.value = 0;
                this.text = "Einmalig";
                break;
            case EVERY_FIFTH:
                this.value = 5;
                this.text = "Jeder 5te";
                break;
            case EVERY_TENTH:
                this.value = 10;
                this.text = "Jeder 10te";
                break;
            case EVERY_THIRTIETH:
                this.value = 30;
                this.text = "Jeder 30te";
                break;
            case EVERY_SIXTIETH:
                this.value = 60;
                this.text = "Jeder 60te";
                break;
        }
    }

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
