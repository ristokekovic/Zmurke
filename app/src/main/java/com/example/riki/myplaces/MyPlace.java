package com.example.riki.myplaces;

/**
 * Created by Riki on 3/23/2017.
 */

public class MyPlace {

    long ID;
    String name;
    String description;
    String longitude;
    String latitude;

    public MyPlace(String nme, String desc) {
        this.name = nme;
        this.description = desc;
    }

    public long getID()
    {
        return ID;
    }

    public void setID(long ID)
    {
        this.ID = ID;
    }

    public MyPlace(String nme)
    {
        this.name = nme;
    }

    public String getName()
    {
        return name;
    }

    public String getDesc()
    {
        return description;
    }

    public void setName(String nme)
    {
        this.name = nme;
    }

    public void setDesc(String desc)
    {
        this.description = desc;
    }

    public String getLongitude()
    {
        return longitude;
    }

    public String getLatitude()
    {
        return latitude;
    }

    public void setLongitude(String longitude)
    {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude)
    {
        this.latitude = latitude;
    }

    @Override
    public String toString()
    {
        return this.name;
    }
}
