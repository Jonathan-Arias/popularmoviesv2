package io.github.jonathan_arias.popularmoviesv2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;

public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        for (int i = 0; i < preferenceScreen.getPreferenceCount(); i++){
            Preference p = preferenceScreen.getPreference(i);
            if (p instanceof ListPreference){
                String val = sharedPreferences.getString(p.getKey(), "");
                setPreferenceSummary(p, val);
            }
        }
    }

    public static String getPreferredSortOrder(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String preferredSort = context.getString(R.string.pref_sort_key);
        String defaultSort = context.getString(R.string.pref_sort_default);
        return sharedPreferences.getString(preferredSort, defaultSort);
    }

    private void setPreferenceSummary(Preference p, String val){
        String key = p.getKey();
        if (p instanceof ListPreference){
            ListPreference listPreference = (ListPreference) p;
            int prefIndex = listPreference.findIndexOfValue(val);
            if (prefIndex >= 0){
                p.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Preference preference = findPreference(s);
        if (preference != null){
            if (preference instanceof ListPreference){
                setPreferenceSummary(preference, sharedPreferences.getString(s, ""));
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        /* Unregister the preference change listener */
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        /* Register the preference change listener */
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }
}