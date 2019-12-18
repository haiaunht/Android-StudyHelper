/**Study Helper
 * Hai-Au Bui
 * CSD230
 * Professor Ballou
 */
package edu.lwtech.studyhelper;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener{

    //data members
    public static String PREFERENCE_THEME = "pref_theme";
    public static String PREFERENCE_SUBJECT_ORDER = "pref_subject_order";
    public static String PREFERENCE_DEFAULT_QUESTION = "pref_default_question";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        // Access the default shared prefs
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());

        setPrefSummarySubjectOrder(sharedPrefs);
        setPrefSummaryDefaultQuestion(sharedPrefs);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PREFERENCE_THEME)) {
            // Recreate the activity so the theme takes effect
            getActivity().recreate();
        }
        else if (key.equals(PREFERENCE_SUBJECT_ORDER)) {
            setPrefSummarySubjectOrder(sharedPreferences);
        }
        else if (key.equals(PREFERENCE_DEFAULT_QUESTION)) {
            setPrefSummaryDefaultQuestion(sharedPreferences);
        }
    }

    // Set the summary to the currently selected subject order
    private void setPrefSummarySubjectOrder(SharedPreferences sharedPrefs) {
        String order = sharedPrefs.getString(PREFERENCE_SUBJECT_ORDER, "1");
        String[] subjectOrders = getResources().getStringArray(R.array.pref_subject_order);

        //return the Preference associated with the given key
        Preference subjectOrderPref = findPreference(PREFERENCE_SUBJECT_ORDER);
        subjectOrderPref.setSummary(subjectOrders[Integer.parseInt(order)]);
    }

    // Set the summary to the default question
    private void setPrefSummaryDefaultQuestion(SharedPreferences sharedPrefs) {
        String defaultQuestion = sharedPrefs.getString(PREFERENCE_DEFAULT_QUESTION, "");
        defaultQuestion = defaultQuestion.trim();
        Preference questionPref = findPreference(PREFERENCE_DEFAULT_QUESTION);

        //setSummary() sets a Preference's summary text
        if (defaultQuestion.length() == 0) {
            questionPref.setSummary(getResources().getString(R.string.pref_none));
        }
        else {
            questionPref.setSummary(defaultQuestion);
        }
    }
}

