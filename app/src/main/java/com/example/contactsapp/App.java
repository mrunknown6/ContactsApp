package com.example.contactsapp;

import android.app.Application;

import java.util.Timer;
import java.util.TimerTask;

public class App extends Application {

    private static long SECOND_TO_MILLISECOND = 1000;
    private static long SESSION_LENGTH = 3 * 60 * SECOND_TO_MILLISECOND; // 3 minutes

    private SessionListener listener;
    private Timer timer;

    public void startUserSession() {
        cancelTimer();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                listener.onSessionLogOut();
            }
        }, SESSION_LENGTH);
    }

    private void cancelTimer() {
        if (timer != null)
            timer.cancel();
    }

    public void registerSessionListener(BaseActivity listener) {
        this.listener = listener;
    }

    public void onUserInteracted() {
        startUserSession();
    }
}
