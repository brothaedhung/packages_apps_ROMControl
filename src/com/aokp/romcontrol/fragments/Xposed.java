
package com.aokp.romcontrol.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;

import com.aokp.romcontrol.AOKPPreferenceFragment;
import com.aokp.romcontrol.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Xposed extends AOKPPreferenceFragment {

    public static final String TAG = "Xposed";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.xposed_settings);
    }
}
