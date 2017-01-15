package com.todo.list.android;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by kevin on 2016/1/30.
 */
public class MyApp extends Application {

    private static MyApp instance;

    public static Context getContext(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
