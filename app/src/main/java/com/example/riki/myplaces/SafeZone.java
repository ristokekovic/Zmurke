package com.example.riki.myplaces;


public class SafeZone
{
    public int id;
    public double latitude;
    public double longitude;
    public int user_id;
    public int color;
    public String name;


    public SafeZone(int id, double latitude, double longitude, int user_id, int color, String name)
    {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.user_id = user_id;
        this.color = color;
        this.name = name;
    }
}
