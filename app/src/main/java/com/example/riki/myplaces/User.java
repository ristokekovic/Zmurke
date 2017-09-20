package com.example.riki.myplaces;

import java.io.Serializable;

/**
 * Created by Riki on 8/21/2017.
 */

public class User implements Serializable{
    public String firstName;
    public String lastName;
    public String email;
    public int id;
    public String apiToken;
    public String avatar;
    public int currentLocation;
    public int currentGame;
    public int safeZone;
    public boolean inSafeZone;
    public boolean beenInSafeZone;
    public boolean hunter;
    public boolean caught;
    public int points;
    public int timer;

    public User()
    {

    }

    public User(String firstName, String lastName, int uid, int currentLocation, int currentGame, int safeZone, boolean inSafeZone, boolean hunter, int points)
    {
        this.id = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.currentLocation = currentLocation;
        this.currentGame = currentGame;
        this.safeZone = safeZone;
        this.inSafeZone = inSafeZone;
        this.hunter = hunter;
        this.points = points;

    }
}
