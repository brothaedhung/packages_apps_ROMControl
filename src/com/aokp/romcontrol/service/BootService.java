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

import com.aokp.romcontrol.AOKPPreferenceFragment;
import com.aokp.romcontrol.R;
import com.aokp.romcontrol.performance.CPUSettings;
import com.aokp.romcontrol.service.CodeReceiver;
import com.aokp.romcontrol.util.AbstractAsyncSuCMDProcessor;
import com.aokp.romcontrol.util.CMDProcessor;
import com.aokp.romcontrol.util.Helpers;
import com.aokp.romcontrol.service.CodeReceiver;

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

            if (HeadphoneService.getUserHeadphoneAudioMode(c) != -1
                    || HeadphoneService.getUserBTAudioMode(c) != -1) {
                c.startService(new Intent(c, HeadphoneService.class));
            }

            if (FlipService.getUserFlipAudioMode(c) != -1
                    || FlipService.getUserCallSilent(c) != 0)
                c.startService(new Intent(c, FlipService.class));

/*            // Swipe2Wake
            final String swipe2waketemp = preferences.getBoolean(
                CPUSettings.SWIPE2WAKE, false)?"1":"0";

            CMDProcessor.runSuCommand("busybox echo " + swipe2waketemp + 
                " > " + CPUSettings.SWIPE2WAKE_PATH);
*/

            // GPU OC
            final String gpu_octemp = preferences.getBoolean(
                CPUSettings.GPU_OC, true)?"1":"0";

            if (gpu_octemp == "1") {
                CMDProcessor.runSuCommand("busybox sh /system/etc/gpu_oc_on");
            }

            // ZRAM
            final String zram_temp = preferences.getBoolean(
                CPUSettings.ZRAM, false)?"1":"0";

            if (zram_temp == "1") {
                CMDProcessor.runSuCommand("busybox mount -o remount,rw /system");
                CMDProcessor.runSuCommand("busybox sh /system/etc/90zramSH");
                CMDProcessor.runSuCommand("busybox cp -f /system/etc/90zram /system/etc/init.d/90zram");
                CMDProcessor.runSuCommand("busybox chmod 755 /system/etc/init.d/90zram");
                CMDProcessor.runSuCommand("busybox mount -o remount,ro /system");
            }

            // Increase min audio freq
            final String audiofreqtemp = preferences.getBoolean(
                CPUSettings.AUDIOFREQ, false)?"204000":"51000";

            CMDProcessor.runSuCommand("busybox echo " + audiofreqtemp + 
                " > " + CPUSettings.AUDIOFREQ_PATH);

            // Wlan, BT and USB tether hack
            final String tetherhacktemp = preferences.getBoolean(
                CPUSettings.TETHER_HACK, false)?"1":"0";

            if (tetherhacktemp == "1") {
                CMDProcessor.runSuCommand("busybox mount -o remount,rw /system");
                CMDProcessor.runSuCommand("busybox sh /system/etc/50tetherhackSH");
                CMDProcessor.runSuCommand("busybox cp -f /system/etc/50tetherhack /system/etc/init.d/50tetherhack");
                CMDProcessor.runSuCommand("busybox chmod 755 /system/etc/init.d/50tetherhack");
                CMDProcessor.runSuCommand("busybox mount -o remount,ro /system");
            }

/*            // Smartdimmer
            final String smartdimmertemp = preferences.getBoolean(
                CPUSettings.SMARTDIMMER, false)?"1":"0";

            CMDProcessor.runSuCommand("busybox echo " + smartdimmertemp + 
                " > " + CPUSettings.SMARTDIMMER_PATH);
*/

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
