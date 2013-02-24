package com.aokp.romcontrol.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.aokp.romcontrol.R;

import com.aokp.romcontrol.performance.CPUSettings;
import com.aokp.romcontrol.util.CMDProcessor;
import com.aokp.romcontrol.util.Helpers;

public class BootService extends Service {

    public static boolean servicesStarted = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            stopSelf();
        }
        new BootWorker(this).execute();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class BootWorker extends AsyncTask<Void, Void, Void> {

        Context c;

        public BootWorker(Context c) {
            this.c = c;
        }

        @Override
        protected Void doInBackground(Void... args) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
            final CMDProcessor cmd = new CMDProcessor();

            if (HeadphoneService.getUserHeadphoneAudioMode(c) != -1
                    || HeadphoneService.getUserBTAudioMode(c) != -1) {
                c.startService(new Intent(c, HeadphoneService.class));
            }

            if (FlipService.getUserFlipAudioMode(c) != -1
                    || FlipService.getUserCallSilent(c) != 0)
                c.startService(new Intent(c, FlipService.class));

            final String swipe2waketemp = preferences.getBoolean(
                CPUSettings.SWIPE2WAKE, false)?"1":"0";

            cmd.su.runWaitFor("busybox echo " + swipe2waketemp + 
                " > " + CPUSettings.SWIPE2WAKE_PATH);

            final String gpu_octemp = preferences.getBoolean(
                CPUSettings.GPU_OC, false)?"1":"0";

            if (gpu_octemp == "1") {
                     cmd.su.runWaitFor("busybox sh /system/etc/gpu_oc_on");
                } else {
                     cmd.su.runWaitFor("busybox sh /system/etc/gpu_oc_off");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            servicesStarted = true;
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
