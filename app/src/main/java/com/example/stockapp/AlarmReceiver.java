package com.example.stockapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class AlarmReceiver extends BroadcastReceiver {
    private static final int PERIOD=15000; // 15 minutes
    private static final int INITIAL_DELAY=15000;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            scheduleAlarms(context);
        } else {
            DetailUpdateService.enqueueWork(context);
        }
    }

    static void scheduleAlarms (Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + INITIAL_DELAY, PERIOD, pendingIntent);
    }
}
