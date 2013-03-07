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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

import com.aokp.romcontrol.R;

import com.aokp.romcontrol.util.CMDProcessor;
import com.aokp.romcontrol.util.Helpers;

public class CPUSettings extends Fragment {

    public static final String TAG = "CPUSettings";

//    public static final String SWIPE2WAKE_PATH = "/sys/android_touch/sweep2wake";
//    public static final String SWIPE2WAKE = "swipe2wake";
    public static final String GPU_OC_PATH = "/sys/devices/system/cpu/cpu0/cpufreq/gpu_oc";
    public static final String GPU_OC = "gpu_oc";
    public static final String AUDIOFREQ_PATH = "/sys/module/snd_soc_tlv320aic3008/parameters/audio_min_freq";
    public static final String AUDIOFREQ = "audiofreq";
    public static final String ZRAM = "zram";
//    public static final String SMARTDIMMER_PATH = "/sys/devices/tegradc.0/smartdimmer/enable";
//    public static final String SMARTDIMMER = "smartdimmer";
    public static final String SEMDOCWIFE = "semdocwife";

//    private Switch mSwipe2Wake;
    private Switch mGpuOc;
    private Switch mAudioFreq;
    private Switch mZram;
//    private Switch mSmartDimmer;
    private Switch mSemdocWife;
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

                CMDProcessor cmd = new CMDProcessor();
                    cmd.su.runWaitFor("busybox echo " + (checked?"1":"0") + " > " + SWIPE2WAKE_PATH);
            }
        });
*/

        // GPU OC
        mGpuOc = (Switch) view.findViewById(R.id.gpu_oc);
        mGpuOc.setChecked(preferences.getBoolean(GPU_OC, false));
        mGpuOc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton v, boolean checked) {
                final SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(GPU_OC, checked);
                editor.commit();

                final String gpu_ocscript = preferences.getBoolean(
                GPU_OC, false)?"1":"0";

                if (gpu_ocscript == "1") {
                    CMDProcessor cmd = new CMDProcessor();
                        cmd.su.runWaitFor("busybox sh /system/etc/gpu_oc_on");
                } 
                if (gpu_ocscript == "0") {
                     CMDProcessor cmd = new CMDProcessor();
                        cmd.su.runWaitFor("busybox sh /system/etc/gpu_oc_off");
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

                CMDProcessor cmd = new CMDProcessor();
                    cmd.su.runWaitFor("busybox echo " + (checked?"204000":"51000") + " > " + AUDIOFREQ_PATH);
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
                    CMDProcessor cmd = new CMDProcessor();
                        cmd.su.runWaitFor("busybox mount -o remount,rw /system");
                        cmd.su.runWaitFor("busybox sh /system/etc/90zramSH");
                        cmd.su.runWaitFor("busybox cp -f /system/etc/90zram /system/etc/init.d/90zram");
                        cmd.su.runWaitFor("busybox chmod 755 /system/etc/init.d/90zram");
                        cmd.su.runWaitFor("busybox mount -o remount,ro /system");
                } 
                if (zram_script == "0") {
                     CMDProcessor cmd = new CMDProcessor();
                        cmd.su.runWaitFor("busybox mount -o remount,rw /system");
                        cmd.su.runWaitFor("busybox rm -f /system/etc/init.d/90zram");
                        cmd.su.runWaitFor("busybox mount -o remount,ro /system");
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

                CMDProcessor cmd = new CMDProcessor();
                    cmd.su.runWaitFor("busybox echo " + (checked?"1":"0") + " > " + SMARTDIMMER_PATH);
            }
        });
*/

        // Mrs. Semdoc
        mSemdocWife = (Switch) view.findViewById(R.id.semdocwife);
        mSemdocWife.setChecked(preferences.getBoolean(SEMDOCWIFE, false));
		
        return view;
    }

}
