package com.aokp.romcontrol.performance;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

import com.aokp.romcontrol.AOKPPreferenceFragment;
import com.aokp.romcontrol.R;
import com.aokp.romcontrol.service.CodeReceiver;
import com.aokp.romcontrol.util.AbstractAsyncSuCMDProcessor;
import com.aokp.romcontrol.util.CMDProcessor;
import com.aokp.romcontrol.util.Helpers;
import com.aokp.romcontrol.service.CodeReceiver;


public class CPUSettings extends Fragment {

    public static final String TAG = "CPUSettings";

//    public static final String SWIPE2WAKE_PATH = "/sys/android_touch/sweep2wake";
//    public static final String SWIPE2WAKE = "swipe2wake";
    public static final String GPU_OC_PATH = "/sys/devices/system/cpu/cpu0/cpufreq/gpu_oc";
    public static final String GPU_OC = "gpu_oc";
    public static final String AUDIOFREQ_PATH = "/sys/module/snd_soc_tlv320aic3008/parameters/audio_min_freq";
    public static final String AUDIOFREQ = "audiofreq";
    public static final String ZRAM = "zram";
    public static final String TETHER_HACK = "tether_hack";
//    public static final String SMARTDIMMER_PATH = "/sys/devices/tegradc.0/smartdimmer/enable";
//    public static final String SMARTDIMMER = "smartdimmer";
    public static final String SEMDOCWIFE = "semdocwife";
    public static final String STEAK = "steak";
    public static final String BJ = "bj";

//    private Switch mSwipe2Wake;
    private Switch mGpuOc;
    private Switch mAudioFreq;
    private Switch mZram;
    private Switch mTetherHack;
//    private Switch mSmartDimmer;
    private Switch mSemdocWife;
    private Switch mSteak;
    private Switch mBj;
    private Activity mActivity;

    private static SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup root,
            Bundle savedInstanceState) {
        mActivity = getActivity();
        View view = inflater.inflate(R.layout.cpu_settings, root, false);

        preferences = PreferenceManager.getDefaultSharedPreferences(mActivity);

/*        // Swipe2Wake
        mSwipe2Wake = (Switch) view.findViewById(R.id.swipe2wake);
        mSwipe2Wake.setChecked(preferences.getBoolean(SWIPE2WAKE, false));
        mSwipe2Wake.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton v, boolean checked) {
                final SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(SWIPE2WAKE, checked);
                editor.commit();

                CMDProcessor.runSuCommand("busybox echo " + (checked?"1":"0") + " > " + SWIPE2WAKE_PATH);
            }
        });
*/

        // GPU OC
        mGpuOc = (Switch) view.findViewById(R.id.gpu_oc);
        mGpuOc.setChecked(preferences.getBoolean(GPU_OC, true));
        mGpuOc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton v, boolean checked) {
                final SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(GPU_OC, checked);
                editor.commit();

                final String gpu_ocscript = preferences.getBoolean(
                GPU_OC, true)?"1":"0";

                if (gpu_ocscript == "1") {
                    CMDProcessor.runSuCommand("busybox sh /system/etc/gpu_oc_on");
                    Toast.makeText(getActivity(), "GPU speed set to 520Mhz!!!", Toast.LENGTH_LONG).show();
                } 
                if (gpu_ocscript == "0") {
                     CMDProcessor.runSuCommand("busybox sh /system/etc/gpu_oc_off");
                     Toast.makeText(getActivity(), "GPU speed set to 416Mhz!!!", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Audio frequency
        mAudioFreq = (Switch) view.findViewById(R.id.audiofreq);
        mAudioFreq.setChecked(preferences.getBoolean(AUDIOFREQ, false));
        mAudioFreq.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton v, boolean checked) {
                final SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(AUDIOFREQ, checked);
                editor.commit();

            final String audiofreqtemp = preferences.getBoolean(
                AUDIOFREQ, false)?"1":"0";

                if (audiofreqtemp == "1") {
                    CMDProcessor.runSuCommand("busybox echo " + "204000" + " > " + AUDIOFREQ_PATH);
                Toast.makeText(getActivity(), "Speed while playing audio set to 204Mhz!!!", Toast.LENGTH_LONG).show();
                }
                if (audiofreqtemp == "0") {
                    CMDProcessor.runSuCommand("busybox echo " + "51000" + " > " + AUDIOFREQ_PATH);
                Toast.makeText(getActivity(), "Speed while playing audio set to 51Mhz!!!", Toast.LENGTH_LONG).show();
                }
            }
        });

        // ZRAM
        mZram = (Switch) view.findViewById(R.id.zram);
        mZram.setChecked(preferences.getBoolean(ZRAM, false));
        mZram.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton v, boolean checked) {
                final SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(ZRAM, checked);
                editor.commit();

                final String zram_script = preferences.getBoolean(
                ZRAM, false)?"1":"0";

                if (zram_script == "1") {
                    CMDProcessor.runSuCommand("busybox mount -o remount,rw /system");
                    CMDProcessor.runSuCommand("busybox sh /system/etc/90zramSH");
                    CMDProcessor.runSuCommand("busybox cp -f /system/etc/90zram /system/etc/init.d/90zram");
                    CMDProcessor.runSuCommand("busybox chmod 755 /system/etc/init.d/90zram");
                    CMDProcessor.runSuCommand("busybox mount -o remount,ro /system");
                    Toast.makeText(getActivity(), "ZRAM enabled and set on boot!!!", Toast.LENGTH_LONG).show();
                } 
                if (zram_script == "0") {
                     CMDProcessor.runSuCommand("busybox mount -o remount,rw /system");
                     CMDProcessor.runSuCommand("busybox rm -f /system/etc/init.d/90zram");
                     CMDProcessor.runSuCommand("busybox mount -o remount,ro /system");
                    Toast.makeText(getActivity(), "ZRAM script deleted, please reboot!!!", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Wlan, BT and USB tether hack
        mTetherHack = (Switch) view.findViewById(R.id.tether_hack);
        mTetherHack.setChecked(preferences.getBoolean(TETHER_HACK, false));
        mTetherHack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton v, boolean checked) {
                final SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(TETHER_HACK, checked);
                editor.commit();

            final String tether_hacktemp = preferences.getBoolean(
                TETHER_HACK, false)?"1":"0";

                if (tether_hacktemp == "1") {
                    CMDProcessor.runSuCommand("busybox mount -o remount,rw /system");
                    CMDProcessor.runSuCommand("busybox sh /system/etc/50tetherhackSH");
                    CMDProcessor.runSuCommand("busybox cp -f /system/etc/50tetherhack /system/etc/init.d/50tetherhack");
                    CMDProcessor.runSuCommand("busybox chmod 755 /system/etc/init.d/50tetherhack");
                    CMDProcessor.runSuCommand("busybox mount -o remount,ro /system");
                    Toast.makeText(getActivity(), "Tethering hack enabled and set on boot!!!", Toast.LENGTH_LONG).show();
                }
                if (tether_hacktemp == "0") {
                    CMDProcessor.runSuCommand("busybox mount -o remount,rw /system");
                    CMDProcessor.runSuCommand("busybox sh /system/etc/50tetherhackSH_OFF");
                    CMDProcessor.runSuCommand("busybox rm -f /system/etc/init.d/50tetherhack");
                    CMDProcessor.runSuCommand("busybox mount -o remount,ro /system");
                    Toast.makeText(getActivity(), "Tethering hack is disabled and removed from boot!!!", Toast.LENGTH_LONG).show();
                }
            }
        });

/*        // Smartdimmer
        mSmartDimmer = (Switch) view.findViewById(R.id.smartdimmer);
        mSmartDimmer.setChecked(preferences.getBoolean(SMARTDIMMER, false));
        mSmartDimmer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton v, boolean checked) {
                final SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(SMARTDIMMER, checked);
                editor.commit();

                CMDProcessor.runSuCommand("busybox echo " + (checked?"1":"0") + " > " + SMARTDIMMER_PATH);
            }
        });
*/

        // Mrs. Semdoc
        mSemdocWife = (Switch) view.findViewById(R.id.semdocwife);
        mSemdocWife.setChecked(preferences.getBoolean(SEMDOCWIFE, false));
        mSemdocWife.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton v, boolean checked) {
                final SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(SEMDOCWIFE, checked);
                editor.commit();
            final String semdoctemp = preferences.getBoolean(
                SEMDOCWIFE, false)?"1":"0";
                if (semdoctemp == "1") {
                    Toast.makeText(getActivity(), "Semdoc's wife is ready and willing!!!", Toast.LENGTH_LONG).show();
                }
                if (semdoctemp == "0") {
                    Toast.makeText(getActivity(), "Semdoc's wife has a headache!!!", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Steak
        mSteak = (Switch) view.findViewById(R.id.steak);
        mSteak.setChecked(preferences.getBoolean(STEAK, false));
        mSteak.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton v, boolean checked) {
                final SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(STEAK, checked);
                editor.commit();
            final String steaktemp = preferences.getBoolean(
                STEAK, false)?"1":"0";
                if (steaktemp == "1") {
                    Toast.makeText(getActivity(), "Steak is ready!!!", Toast.LENGTH_LONG).show();
                }
                if (steaktemp == "0") {
                    Toast.makeText(getActivity(), "I hope it wasn't too raw!!!", Toast.LENGTH_LONG).show();
                }
            }
        });

        // BJ
        mBj = (Switch) view.findViewById(R.id.bj);
        mBj.setChecked(preferences.getBoolean(BJ, false));
        mBj.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton v, boolean checked) {
                final SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(BJ, checked);
                editor.commit();
            final String bjtemp = preferences.getBoolean(
                BJ, false)?"1":"0";
                if (bjtemp == "1") {
                    Toast.makeText(getActivity(), "Put down your trousers and close your eyes!!!", Toast.LENGTH_LONG).show();
                }
                if (bjtemp == "0") {
                    Toast.makeText(getActivity(), "Already done???", Toast.LENGTH_LONG).show();
                }
            }
        });
		
        return view;
    }

}
