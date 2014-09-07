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
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.cyanogenmod.SystemSettingCheckBoxPreference;
import com.android.settings.colorpicker.ColorPickerPreference;
import com.android.settings.SettingsPreferenceFragment;

public class RecentsPanel extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "RecentsPanel";

    private static final String CLEAR_ALL_SHOW = "clear_all_show";
    private static final String CLEAR_ALL_POSITION = "clear_all_position";

    private static final String RAM_BAR_SHOW = "ram_bar_show";
    private static final String RAM_BAR_COLOR_LEFT = "rambar_color_left";
    private static final String RAM_BAR_COLOR_RIGHT = "rambar_color_right";

    private CheckBoxPreference mClearAllShow;
    private ListPreference mClearAllPosition;

    private CheckBoxPreference mRamBarShow;
    private ColorPickerPreference mRamBarColorLeft;
    private ColorPickerPreference mRamBarColorRight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.recents_interface_settings);

        mClearAllShow = (SystemSettingCheckBoxPreference) findPreference(CLEAR_ALL_SHOW);
        mClearAllPosition = (ListPreference) findPreference(CLEAR_ALL_POSITION);
        
        mRamBarShow = (SystemSettingCheckBoxPreference) findPreference(RAM_BAR_SHOW);
        mRamBarColorLeft = (ColorPickerPreference) findPreference(RAM_BAR_COLOR_LEFT);
        mRamBarColorRight = (ColorPickerPreference) findPreference(RAM_BAR_COLOR_RIGHT);
        mRamBarColorLeft.setOnPreferenceChangeListener(this);
        mRamBarColorRight.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();

        if (preference == mClearAllPosition) {
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
        }
        return false;
    }
}
