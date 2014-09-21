/*
 * Copyright (C) 2012 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.cyanogenmod;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.MSimTelephonyManager;

import com.android.settings.R;
import com.android.settings.colorpicker.ColorPickerPreference;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class StatusBar extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "StatusBar";

    private static final String STATUS_BAR_BATTERY = "status_bar_battery";
    private static final String STATUS_BAR_SIGNAL = "status_bar_signal";

    private static final String STATUS_BAR_BATTERY_SHOW_PERCENT = "status_bar_battery_show_percent";
    private static final String STATUS_BAR_CLOCK_STYLE = "status_bar_clock";
    private static final String STATUS_BAR_CLOCK_COLOR = "status_bar_clock_color";

    private static final String STATUS_BAR_DOW = "status_bar_dow";
    private static final String STATUS_BAR_AM_PM = "status_bar_am_pm";

    private static final String STATUS_BAR_STYLE_HIDDEN = "4";
    private static final String STATUS_BAR_STYLE_TEXT = "6";
    
    private static final String STATUS_BAR_STYLE_LANDSCAPE = "5";
    private static final String STATUS_BAR_STYLE_PORTRAIT = "0";

    private static final String NETWORK_TRAFFIC_STATE = "network_traffic_meter";
    private static final String NETWORK_TRAFFIC_COLOR_UP = "network_traffic_color_up";
    private static final String NETWORK_TRAFFIC_COLOR_DOWN = "network_traffic_color_down";
    private static final String NETWORK_TRAFFIC_COLOR_ICON = "network_traffic_color_icon";
    private static final String NETWORK_TRAFFIC_UNIT = "network_traffic_unit";
    private static final String NETWORK_TRAFFIC_INTERVAL = "network_traffic_interval";

    private static final String CLEAR_ALL_POSITION = "clear_all_position";

    private static final String RAM_BAR_COLOR_LEFT = "rambar_color_left";
    private static final String RAM_BAR_COLOR_RIGHT = "rambar_color_right";
    
    private static final String BATTERY_COLOR = "status_bar_battery_color";
    private static final String BATTERY_COLOR_LOW = "status_bar_battery_color_low";
    private static final String BATTERY_COLOR_CRIT = "status_bar_battery_color_crit";
    private static final String BATTERY_TEXT_COLOR_LOW = "status_bar_battery_text_color_low";
    private static final String BATTERY_TEXT_COLOR = "status_bar_battery_text_color";
    private static final String BATTERY_CHARGE_COLOR = "status_bar_battery_charge_color";
    private static final String BATTERY_BACK_COLOR = "status_bar_battery_back_color";
    private static final String BATTERY_BOLT_COLOR = "status_bar_battery_bolt_color";
    
    private static final String USE_BATTERY_TEXT_COLOR_LOW = "use_battery_text_color_low";
    
    private static final String BATTERY_LEVEL_CRIT = "status_bar_battery_level_crit";
    private static final String BATTERY_LEVEL_LOW = "status_bar_battery_level_low";

    private static final String BATTERY_FONT = "status_bar_battery_font";

    private ListPreference mStatusBarClockStyle;
    private ColorPickerPreference mStatusBarClockColor;
    private ListPreference mStatusBarDowStyle;
    private ListPreference mStatusBarAmPmStyle;
    private ListPreference mStatusBarBattery;
    private SystemSettingCheckBoxPreference mStatusBarBatteryShowPercent;
    private ListPreference mStatusBarCmSignal;
    private CheckBoxPreference mStatusBarBrightnessControl;

    private ListPreference mNetworkTrafficState;
    private ListPreference mNetworkTrafficUnit;
    private ListPreference mNetworkTrafficInterval;
    private ColorPickerPreference mNetworkTrafficColorUp;
    private ColorPickerPreference mNetworkTrafficColorDown;
    private ColorPickerPreference mNetworkTrafficColorIcon;

    private ListPreference mClearAllPosition;

    private ColorPickerPreference mRamBarColorLeft;
    private ColorPickerPreference mRamBarColorRight;
    
    private ColorPickerPreference mBatteryColor;
    private ColorPickerPreference mBatteryColorLow;
    private ColorPickerPreference mBatteryColorCrit;
    private ColorPickerPreference mBatteryTextColor;
    private ColorPickerPreference mBatteryTextColorLow;
    private ColorPickerPreference mBatteryChargeColor;
    private ColorPickerPreference mBatteryBackColor;
    private ColorPickerPreference mBatteryBoltColor;
    private SystemSettingCheckBoxPreference mShowBatteryTextLow;
    
    private ListPreference mBatteryLevelCrit;
    private ListPreference mBatteryLevelLow;

    private ListPreference mBatteryFont;

    private ContentObserver mSettingsObserver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.status_bar);

        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        mStatusBarClockStyle = (ListPreference) findPreference(STATUS_BAR_CLOCK_STYLE);

        mStatusBarClockColor = (ColorPickerPreference) findPreference(STATUS_BAR_CLOCK_COLOR);
        mStatusBarClockColor.setOnPreferenceChangeListener(this);

        mStatusBarDowStyle = (ListPreference) findPreference(STATUS_BAR_DOW);

        mStatusBarAmPmStyle = (ListPreference) findPreference(STATUS_BAR_AM_PM);

        mStatusBarBattery = (ListPreference) findPreference(STATUS_BAR_BATTERY);
        mStatusBarBatteryShowPercent =
                (SystemSettingCheckBoxPreference) findPreference(STATUS_BAR_BATTERY_SHOW_PERCENT);
        mStatusBarCmSignal = (ListPreference) prefSet.findPreference(STATUS_BAR_SIGNAL);

        mStatusBarBrightnessControl = (CheckBoxPreference)
                prefSet.findPreference(Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL);
        refreshBrightnessControl();

        mNetworkTrafficState = (ListPreference) findPreference(NETWORK_TRAFFIC_STATE);
        mNetworkTrafficUnit = (ListPreference) findPreference(NETWORK_TRAFFIC_UNIT);
        mNetworkTrafficInterval = (ListPreference) findPreference(NETWORK_TRAFFIC_INTERVAL);
        mNetworkTrafficColorUp = (ColorPickerPreference) findPreference(NETWORK_TRAFFIC_COLOR_UP);
        mNetworkTrafficColorUp.setOnPreferenceChangeListener(this);
        mNetworkTrafficColorDown = (ColorPickerPreference) findPreference(NETWORK_TRAFFIC_COLOR_DOWN);
        mNetworkTrafficColorDown.setOnPreferenceChangeListener(this);
        mNetworkTrafficColorIcon = (ColorPickerPreference) findPreference(NETWORK_TRAFFIC_COLOR_ICON);
        mNetworkTrafficColorIcon.setOnPreferenceChangeListener(this);

        mClearAllPosition = (ListPreference) findPreference(CLEAR_ALL_POSITION);

        mRamBarColorLeft = (ColorPickerPreference) findPreference(RAM_BAR_COLOR_LEFT);
        mRamBarColorLeft.setOnPreferenceChangeListener(this);
        mRamBarColorRight = (ColorPickerPreference) findPreference(RAM_BAR_COLOR_RIGHT);       
        mRamBarColorRight.setOnPreferenceChangeListener(this);
        
        mBatteryColor = (ColorPickerPreference) findPreference(BATTERY_COLOR);
        mBatteryColor.setOnPreferenceChangeListener(this);
        mBatteryColorLow = (ColorPickerPreference) findPreference(BATTERY_COLOR_LOW);       
        mBatteryColorLow.setOnPreferenceChangeListener(this);
        mBatteryColorCrit = (ColorPickerPreference) findPreference(BATTERY_COLOR_CRIT);
        mBatteryColorCrit.setOnPreferenceChangeListener(this);
        mBatteryTextColor = (ColorPickerPreference) findPreference(BATTERY_TEXT_COLOR);       
        mBatteryTextColor.setOnPreferenceChangeListener(this);
        mBatteryTextColorLow = (ColorPickerPreference) findPreference(BATTERY_TEXT_COLOR_LOW);
        mBatteryTextColorLow.setOnPreferenceChangeListener(this);
        mBatteryChargeColor = (ColorPickerPreference) findPreference(BATTERY_CHARGE_COLOR);       
        mBatteryChargeColor.setOnPreferenceChangeListener(this);
        mBatteryBackColor = (ColorPickerPreference) findPreference(BATTERY_BACK_COLOR);
        mBatteryBackColor.setOnPreferenceChangeListener(this);
        mBatteryBoltColor = (ColorPickerPreference) findPreference(BATTERY_BOLT_COLOR);       
        mBatteryBoltColor.setOnPreferenceChangeListener(this);
        
        mBatteryLevelCrit = (ListPreference) findPreference(BATTERY_LEVEL_CRIT);
        mBatteryLevelLow = (ListPreference) findPreference(BATTERY_LEVEL_LOW);

        mBatteryFont = (ListPreference) findPreference(BATTERY_FONT);
        
        mShowBatteryTextLow =
                (SystemSettingCheckBoxPreference) findPreference(USE_BATTERY_TEXT_COLOR_LOW);

        int clockStyle = Settings.System.getInt(resolver, Settings.System.STATUS_BAR_CLOCK, 1);
        mStatusBarClockStyle.setValue(String.valueOf(clockStyle));
        mStatusBarClockStyle.setSummary(mStatusBarClockStyle.getEntry());
        mStatusBarClockStyle.setOnPreferenceChangeListener(this);

        int dowStyle = Settings.System.getInt(resolver, Settings.System.STATUS_BAR_DOW, 2);
        mStatusBarDowStyle.setValue(String.valueOf(dowStyle));
        mStatusBarDowStyle.setSummary(mStatusBarDowStyle.getEntry());
        mStatusBarDowStyle.setOnPreferenceChangeListener(this);

        int amPmStyle = Settings.System.getInt(resolver, Settings.System.STATUS_BAR_AM_PM, 2);
        mStatusBarAmPmStyle.setValue(String.valueOf(amPmStyle));
        mStatusBarAmPmStyle.setSummary(mStatusBarAmPmStyle.getEntry());
        mStatusBarAmPmStyle.setOnPreferenceChangeListener(this);

        int batteryStyle = Settings.System.getInt(resolver, Settings.System.STATUS_BAR_BATTERY, 0);
        mStatusBarBattery.setValue(String.valueOf(batteryStyle));
        mStatusBarBattery.setSummary(mStatusBarBattery.getEntry());
        mStatusBarBattery.setOnPreferenceChangeListener(this);

        int signalStyle = Settings.System.getInt(resolver, Settings.System.STATUS_BAR_SIGNAL_TEXT, 0);
        mStatusBarCmSignal.setValue(String.valueOf(signalStyle));
        mStatusBarCmSignal.setSummary(mStatusBarCmSignal.getEntry());
        mStatusBarCmSignal.setOnPreferenceChangeListener(this);

        int trafficState = Settings.System.getInt(resolver, Settings.System.NETWORK_TRAFFIC_STATE, 0);
        mNetworkTrafficState.setValue(String.valueOf(trafficState));
        mNetworkTrafficState.setSummary(mNetworkTrafficState.getEntry());
        mNetworkTrafficState.setOnPreferenceChangeListener(this);

        int trafficUnit = Settings.System.getInt(resolver, Settings.System.NETWORK_TRAFFIC_UNIT, 1);
        mNetworkTrafficUnit.setValue(String.valueOf(trafficUnit));
        mNetworkTrafficUnit.setSummary(mNetworkTrafficUnit.getEntry());
        mNetworkTrafficUnit.setOnPreferenceChangeListener(this);

        int trafficInt = Settings.System.getInt(resolver, Settings.System.NETWORK_TRAFFIC_INTERVAL, 1000);
        mNetworkTrafficInterval.setValue(String.valueOf(trafficInt));
        mNetworkTrafficInterval.setSummary(mNetworkTrafficInterval.getEntry());
        mNetworkTrafficInterval.setOnPreferenceChangeListener(this);

        int clearPosition = Settings.System.getInt(resolver, Settings.System.CLEAR_ALL_POSITION, 0);
        mClearAllPosition.setValue(String.valueOf(clearPosition));
        mClearAllPosition.setSummary(mClearAllPosition.getEntry());
        mClearAllPosition.setOnPreferenceChangeListener(this);
        
        int batteryCrit = Settings.System.getInt(resolver, Settings.System.STATUS_BAR_BATTERY_LEVEL_CRIT, 4);
        mBatteryLevelCrit.setValue(String.valueOf(batteryCrit));
        mBatteryLevelCrit.setSummary(mBatteryLevelCrit.getEntry());
        mBatteryLevelCrit.setOnPreferenceChangeListener(this);
        
        int batteryLow = Settings.System.getInt(resolver, Settings.System.STATUS_BAR_BATTERY_LEVEL_LOW, 15);
        mBatteryLevelLow.setValue(String.valueOf(batteryLow));
        mBatteryLevelLow.setSummary(mBatteryLevelLow.getEntry());
        mBatteryLevelLow.setOnPreferenceChangeListener(this);

        int batteryFont = Settings.System.getInt(resolver, Settings.System.STATUS_BAR_BATTERY_FONT, 1);
        mBatteryFont.setValue(String.valueOf(batteryFont));
        mBatteryFont.setSummary(mBatteryFont.getEntry());
        mBatteryFont.setOnPreferenceChangeListener(this);

        if (Utils.isWifiOnly(getActivity())
                || (MSimTelephonyManager.getDefault().isMultiSimEnabled())) {
            prefSet.removePreference(mStatusBarCmSignal);
        }

        enableStatusBarBatteryDependents(mStatusBarBattery.getValue());
        enableBatteryTextOptions(mStatusBarBattery.getValue());

        mSettingsObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                refreshBrightnessControl();
                enableBatteryTextOptions(mStatusBarBattery.getValue());
            }

            @Override
            public void onChange(boolean selfChange) {
                onChange(selfChange, null);
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        getContentResolver().registerContentObserver(
                Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE),
                true, mSettingsObserver);
        getContentResolver().registerContentObserver(
                Settings.System.getUriFor(Settings.System.STATUS_BAR_BATTERY),
                true, mSettingsObserver);
    }

    @Override
    public void onPause() {
        super.onPause();
        getContentResolver().unregisterContentObserver(mSettingsObserver);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mStatusBarBattery) {
            int batteryStyle = Integer.valueOf((String) newValue);
            int index = mStatusBarBattery.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver, Settings.System.STATUS_BAR_BATTERY, batteryStyle);
            mStatusBarBattery.setSummary(mStatusBarBattery.getEntries()[index]);

            enableStatusBarBatteryDependents((String) newValue);
            return true;
        } else if (preference == mStatusBarCmSignal) {
            int signalStyle = Integer.valueOf((String) newValue);
            int index = mStatusBarCmSignal.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver, Settings.System.STATUS_BAR_SIGNAL_TEXT, signalStyle);
            mStatusBarCmSignal.setSummary(mStatusBarCmSignal.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarClockStyle) {
            int clockStyle = Integer.valueOf((String) newValue);
            int index = mStatusBarClockStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver, Settings.System.STATUS_BAR_CLOCK, clockStyle);
            mStatusBarClockStyle.setSummary(mStatusBarClockStyle.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarDowStyle) {
            int dowStyle = Integer.valueOf((String) newValue);
            int index = mStatusBarDowStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver, Settings.System.STATUS_BAR_DOW, dowStyle);
            mStatusBarDowStyle.setSummary(mStatusBarDowStyle.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarAmPmStyle) {
            int amPmStyle = Integer.valueOf((String) newValue);
            int index = mStatusBarAmPmStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver, Settings.System.STATUS_BAR_AM_PM, amPmStyle);
            mStatusBarAmPmStyle.setSummary(mStatusBarAmPmStyle.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarClockColor){
            int value = (Integer) newValue;
            Settings.System.putInt(resolver, STATUS_BAR_CLOCK_COLOR, value);
            return true;
        } else if (preference == mNetworkTrafficState) {
            int networkState = Integer.valueOf((String) newValue);
            int index = mNetworkTrafficState.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver, Settings.System.NETWORK_TRAFFIC_STATE, networkState);
            mNetworkTrafficState.setSummary(mNetworkTrafficState.getEntries()[index]);
            return true;
        } else if (preference == mNetworkTrafficUnit) {
            int networkUnit = Integer.valueOf((String) newValue);
            int index = mNetworkTrafficUnit.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver, Settings.System.NETWORK_TRAFFIC_UNIT, networkUnit);
            mNetworkTrafficUnit.setSummary(mNetworkTrafficUnit.getEntries()[index]);
            return true;
        } else if (preference == mNetworkTrafficInterval) {
            int networkInterval = Integer.valueOf((String) newValue);
            int index = mNetworkTrafficInterval.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver, Settings.System.NETWORK_TRAFFIC_INTERVAL, networkInterval);
            mNetworkTrafficInterval.setSummary(mNetworkTrafficInterval.getEntries()[index]);
            return true;
        } else if (preference == mNetworkTrafficColorUp){
            int value = (Integer) newValue;
            Settings.System.putInt(resolver, NETWORK_TRAFFIC_COLOR_UP, value);
            return true;
        } else if (preference == mNetworkTrafficColorDown){
            int value = (Integer) newValue;
            Settings.System.putInt(resolver, NETWORK_TRAFFIC_COLOR_DOWN, value);
            return true;
        } else if (preference == mNetworkTrafficColorIcon){
            int value = (Integer) newValue;
            Settings.System.putInt(resolver, NETWORK_TRAFFIC_COLOR_ICON, value);
            return true;
        } else if (preference == mClearAllPosition) {
            int position = Integer.valueOf((String) newValue);
            int index = mClearAllPosition.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver, Settings.System.CLEAR_ALL_POSITION, position);
            mClearAllPosition.setSummary(mClearAllPosition.getEntries()[index]);
            return true;
        } else if (preference == mRamBarColorLeft){
            int value = (Integer) newValue;
            Settings.System.putInt(resolver, RAM_BAR_COLOR_LEFT, value);
            return true;
        } else if (preference == mRamBarColorRight){
            int value = (Integer) newValue;
            Settings.System.putInt(resolver, RAM_BAR_COLOR_RIGHT, value);
            return true;
        } else if (preference == mBatteryColor){
            int value = (Integer) newValue;
            Settings.System.putInt(resolver, BATTERY_COLOR, value);
            return true;
        } else if (preference == mBatteryColorLow){
            int value = (Integer) newValue;
            Settings.System.putInt(resolver, BATTERY_COLOR_LOW, value);
            return true;
        } else if (preference == mBatteryColorCrit){
            int value = (Integer) newValue;
            Settings.System.putInt(resolver, BATTERY_COLOR_CRIT, value);
            return true;
        } else if (preference == mBatteryTextColorLow){
            int value = (Integer) newValue;
            Settings.System.putInt(resolver, BATTERY_TEXT_COLOR_LOW, value);
            return true;
        } else if (preference == mBatteryTextColor){
            int value = (Integer) newValue;
            Settings.System.putInt(resolver, BATTERY_TEXT_COLOR, value);
            return true;
        } else if (preference == mBatteryChargeColor){
            int value = (Integer) newValue;
            Settings.System.putInt(resolver, BATTERY_CHARGE_COLOR, value);
            return true;
        } else if (preference == mBatteryBackColor){
            int value = (Integer) newValue;
            Settings.System.putInt(resolver, BATTERY_BACK_COLOR, value);
            return true;
        } else if (preference == mBatteryBoltColor){
            int value = (Integer) newValue;
            Settings.System.putInt(resolver, BATTERY_BOLT_COLOR, value);
            return true;
        } else if (preference == mBatteryTextColor){
            int value = (Integer) newValue;
            Settings.System.putInt(resolver, BATTERY_TEXT_COLOR, value);
            return true;
        } else if (preference == mBatteryTextColorLow){
            int value = (Integer) newValue;
            Settings.System.putInt(resolver, BATTERY_TEXT_COLOR_LOW, value);
            return true;
        } else if (preference == mBatteryLevelCrit) {
            int level = Integer.valueOf((String) newValue);
            int index = mBatteryLevelCrit.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver, Settings.System.STATUS_BAR_BATTERY_LEVEL_CRIT, level);
            mBatteryLevelCrit.setSummary(mBatteryLevelCrit.getEntries()[index]);
            return true;
        } else if (preference == mBatteryLevelLow) {
            int level = Integer.valueOf((String) newValue);
            int index = mBatteryLevelLow.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver, Settings.System.STATUS_BAR_BATTERY_LEVEL_LOW, level);
            mBatteryLevelLow.setSummary(mBatteryLevelLow.getEntries()[index]);
            return true;
        } else if (preference == mBatteryFont) {
            int font = Integer.valueOf((String) newValue);
            int index = mBatteryFont.findIndexOfValue((String) newValue);
            Settings.System.putInt(resolver, Settings.System.STATUS_BAR_BATTERY_FONT, font);
            mBatteryFont.setSummary(mBatteryFont.getEntries()[index]);
            return true;
        }
        return false;
    }

    private void refreshBrightnessControl() {
        try {
            if (Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE)
                    == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                mStatusBarBrightnessControl.setSummary(R.string.status_bar_toggle_info);
            } else {
                mStatusBarBrightnessControl.setSummary(R.string.status_bar_toggle_brightness_summary);
            }
        } catch (SettingNotFoundException e) {
            // Do nothing
        }
    }

    private void enableStatusBarBatteryDependents(String value) {
        boolean enabled = !(value.equals(STATUS_BAR_STYLE_TEXT)
                || value.equals(STATUS_BAR_STYLE_HIDDEN));
        mStatusBarBatteryShowPercent.setEnabled(enabled);
    }
    
    private void enableBatteryTextOptions(String value) { 
        boolean enabled = (value.equals(STATUS_BAR_STYLE_LANDSCAPE)
                || value.equals(STATUS_BAR_STYLE_PORTRAIT));
        mBatteryTextColorLow.setEnabled(enabled);
        mShowBatteryTextLow.setEnabled(enabled);
    }
}
