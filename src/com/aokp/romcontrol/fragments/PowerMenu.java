
package com.aokp.romcontrol.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.aokp.romcontrol.AOKPPreferenceFragment;
import com.aokp.romcontrol.R;
import com.aokp.romcontrol.R.xml;

public class PowerMenu extends AOKPPreferenceFragment implements OnPreferenceChangeListener {

    //private static final String PREF_POWER_SAVER = "show_power_saver";
    private static final String PREF_SCREENSHOT = "show_screenshot";
    private static final String PREF_TORCH_TOGGLE = "show_torch_toggle";
    private static final String PREF_AIRPLANE_TOGGLE = "show_airplane_toggle";
    private static final String PREF_NAVBAR_HIDE = "show_navbar_hide";
    private static final String PREF_REBOOT_KEYGUARD = "show_reboot_keyguard";
    private static final String KEY_EXPANDED_DESKTOP = "power_menu_expanded_desktop";

    //CheckBoxPreference mShowPowerSaver;
    CheckBoxPreference mShowScreenShot;
    CheckBoxPreference mShowTorchToggle;
    CheckBoxPreference mShowAirplaneToggle;
    CheckBoxPreference mShowNavBarHide;
    CheckBoxPreference mShowRebootKeyguard;
    ListPreference mExpandedDesktopPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs_powermenu);
        /*
        mShowPowerSaver = (CheckBoxPreference) findPreference(PREF_POWER_SAVER);
        int powerSaverVal = 0;
        try {
            powerSaverVal = Settings.Secure.getInt(mContentRes,
             Settings.Secure.POWER_SAVER_MODE);
        } catch (SettingNotFoundException e) {
            mShowPowerSaver.setEnabled(false);
            mShowPowerSaver
                    .setSummary("You need to enable power saver before you can see it in the power menu.");
        }
        mShowPowerSaver.setChecked(powerSaverVal != 0); */

        mShowTorchToggle = (CheckBoxPreference) findPreference(PREF_TORCH_TOGGLE);
        mShowTorchToggle.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.POWER_DIALOG_SHOW_TORCH_TOGGLE, false));

        mShowScreenShot = (CheckBoxPreference) findPreference(PREF_SCREENSHOT);
        mShowScreenShot.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.POWER_DIALOG_SHOW_SCREENSHOT, false));

        mShowAirplaneToggle = (CheckBoxPreference) findPreference(PREF_AIRPLANE_TOGGLE);
        mShowAirplaneToggle.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.POWER_DIALOG_SHOW_AIRPLANE_TOGGLE, true));

        mShowNavBarHide = (CheckBoxPreference) findPreference(PREF_NAVBAR_HIDE);
        mShowNavBarHide.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.POWER_DIALOG_SHOW_NAVBAR_HIDE, false));

        mShowRebootKeyguard = (CheckBoxPreference) findPreference(PREF_REBOOT_KEYGUARD);
        mShowRebootKeyguard.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.POWER_DIALOG_SHOW_REBOOT_KEYGUARD, true));

        PreferenceScreen prefSet = getPreferenceScreen();
        mExpandedDesktopPref = (ListPreference) prefSet.findPreference(KEY_EXPANDED_DESKTOP);
        mExpandedDesktopPref.setOnPreferenceChangeListener(this);
        int expandedDesktopValue = Settings.System.getInt(getContentResolver(),
            Settings.System.EXPANDED_DESKTOP_STYLE, 0);
        mExpandedDesktopPref.setValue(String.valueOf(expandedDesktopValue));
        updateExpandedDesktopSummary(expandedDesktopValue);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mShowScreenShot) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.POWER_DIALOG_SHOW_SCREENSHOT,
                    ((CheckBoxPreference)preference).isChecked());
            return true;
        /*
        } else if (preference == mShowPowerSaver) {
            Settings.System.putInt(mContentRes,
                    Settings.System.POWER_DIALOG_SHOW_POWER_SAVER,
                    ((CheckBoxPreference)preference).isChecked() ? 1 : 0);
            return true; */
        } else if (preference == mShowTorchToggle) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.POWER_DIALOG_SHOW_TORCH_TOGGLE,
                    ((CheckBoxPreference)preference).isChecked());
            return true;
        } else if (preference == mShowAirplaneToggle) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.POWER_DIALOG_SHOW_AIRPLANE_TOGGLE,
                    ((CheckBoxPreference)preference).isChecked());
            return true;
        } else if (preference == mShowNavBarHide) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.POWER_DIALOG_SHOW_NAVBAR_HIDE,
                    ((CheckBoxPreference)preference).isChecked());
            return true;
        } else if (preference == mShowRebootKeyguard) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.POWER_DIALOG_SHOW_REBOOT_KEYGUARD,
                    ((CheckBoxPreference)preference).isChecked());
            return true;
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mExpandedDesktopPref) {
            int expandedDesktopValue = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.EXPANDED_DESKTOP_STYLE, expandedDesktopValue);
            updateExpandedDesktopSummary(expandedDesktopValue);
            return true;
        }
        return false;
    }

    private void updateExpandedDesktopSummary(int value) {
        Resources res = getResources();

        if (value == 0) {
            // Expanded desktop deactivated
            Settings.System.putInt(getContentResolver(),
                    Settings.System.POWER_MENU_EXPANDED_DESKTOP_ENABLED, 0);
            mExpandedDesktopPref.setSummary(res.getString(R.string.expanded_desktop_disabled));
        } else if (value == 1) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.POWER_MENU_EXPANDED_DESKTOP_ENABLED, 1);
            mExpandedDesktopPref.setSummary(res.getString(R.string.expanded_desktop_status_bar));
        } else if (value == 2) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.POWER_MENU_EXPANDED_DESKTOP_ENABLED, 1);
            mExpandedDesktopPref.setSummary(res.getString(R.string.expanded_desktop_no_status_bar));
        }
    }
}
