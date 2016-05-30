package com.bachelor.stwagene.bluecheck.Model;

/**
 * Created by stwagene on 12.05.2016.
 */
public class Option
{
    private String name;
    private int iconID;
    private OptionType type;

    public Option (String name, int iconID, OptionType type)
    {
        this.type = type;
        this.name = name;
        this.iconID = iconID;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getIconID()
    {
        return iconID;
    }

    public void setIconID(int iconID)
    {
        this.iconID = iconID;
    }

    public OptionType getType()
    {
        return type;
    }

    public void setType(OptionType type)
    {
        this.type = type;
    }
}
