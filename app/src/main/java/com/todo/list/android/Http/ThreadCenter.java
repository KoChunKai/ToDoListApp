package com.todo.list.android.Http;

import android.content.Context;

import com.todo.list.android.MyApp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kevin on 2016/12/23.
 */
public class ThreadCenter {

    private static ThreadCenter instance = null;
    private static Context context;

    public static ThreadCenter getInstance(){
        if(instance == null){
            instance = new ThreadCenter();
            context = MyApp.getContext();
        }else{
            if(context != MyApp.getContext()){
                instance.executorService.shutdownNow();
                instance = new ThreadCenter();
                context = MyApp.getContext();
            }
        }
        return instance;
    }

    private ExecutorService executorService;

    private ThreadCenter(){
        executorService = Executors.newFixedThreadPool(1);
    }

    public void start(Runnable runnable){
        executorService.execute(runnable);
    }

}
