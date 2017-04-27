package by.dvd.mappoint;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;


public class SettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_map_type)));

        setHasOptionsMenu(true);
    }


    public void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(this);
        onPreferenceChange(preference, preferences.getString(preference.getKey(), null));
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String stringValue = newValue.toString();

        ListPreference listPreference = (ListPreference) preference;
        int index = listPreference.findIndexOfValue(stringValue);
        preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);

        return true;
    }


}
