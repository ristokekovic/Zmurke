package com.example.riki.myplaces;

import android.app.Application;
import android.content.Context;

/**
 * Created by Riki on 3/30/2017.
 */

public class MyPlacesApplication extends Application {
    private static MyPlacesApplication instance;

    public MyPlacesApplication() {
        instance = this;
    }

    public static Context getContext(){
        return instance;
    }
}
