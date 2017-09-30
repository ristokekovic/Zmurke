package com.example.riki.myplaces;

/**
 * Created by Marijah on 9/21/2017.
 */

import java.util.List;

public class Friendship {


    private String uid;
    private List<String> friends;

    public Friendship(String uid, List<String> friends) {
        this.uid = uid;
        this.friends = friends;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public Friendship(String uid1, String uid2){
        //do nothing, this is for Friends.java
    }
}
