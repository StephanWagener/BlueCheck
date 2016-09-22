package com.bachelor.stwagene.bluecheck.Model;

/**
 * Created by stwagene on 31.08.2016.
 */
public class DeliveryProduct
{
    private String name;
    private String dimensions;
    private String description;
    private String id;
    private String addressor;
    private String recipient;
    private int loadStatus = -1;

    public DeliveryProduct(String id, String name, String description, String addressor, String recipient, int loadStatus)
    {
        this.id = id;
        this.name = name;
        this.description = description;
        this.addressor = addressor;
        this.recipient = recipient;
        this.loadStatus = loadStatus;
    }

    public DeliveryProduct(String recipient, String dimensions, int loadStatus, String id)
    {
        this.id = id;
        this.recipient = recipient;
        this.loadStatus = loadStatus;
        this.dimensions = dimensions;
    }

    public DeliveryProduct(String recipient, String dimensions, String id)
    {
        this.id = id;
        this.recipient = recipient;
        this.dimensions = dimensions;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getAddressor()
    {
        return addressor;
    }

    public void setAddressor(String addressor)
    {
        this.addressor = addressor;
    }

    public String getRecipient()
    {
        return recipient;
    }

    public void setRecipient(String recipient)
    {
        this.recipient = recipient;
    }

    public int getLoadStatus()
    {
        return loadStatus;
    }

    public void setLoadStatus(int loadStatus)
    {
        this.loadStatus = loadStatus;
    }

    public String getDimensions()
    {
        return dimensions;
    }

    public void setDimensions(String dimensions)
    {
        this.dimensions = dimensions;
    }
}
