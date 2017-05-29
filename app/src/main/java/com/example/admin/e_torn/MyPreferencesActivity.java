package com.example.admin.e_torn;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.admin.e_torn.models.User;
import com.example.admin.e_torn.services.RetrofitManager;
import com.example.admin.e_torn.services.UserService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Patango on 27/04/2017.
 */

public class MyPreferencesActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    final static String TAG = "MyPreferencesActivity";

    SharedPreferences sharedPreferences;

    ETornApplication app;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (ETornApplication) getApplication();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        MyPreferenceFragment myPreferenceFragment = new MyPreferenceFragment();
        myPreferenceFragment.setApp(app);

        getFragmentManager().beginTransaction().replace(android.R.id.content, myPreferenceFragment).commit();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "Key: " + key);
        Log.d(TAG, "Value: " + sharedPreferences.getString(key, "5"));

        app.setMongoUserPreferences(sharedPreferences);
    }

    public static class MyPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

        ETornApplication app;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            Preference preference = findPreference("turn_pref_editText");
            preference.setOnPreferenceClickListener(this);
            preference.setOnPreferenceClickListener(this);
        }

        public void setApp (ETornApplication app) {
            this.app = app;
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            SharedPreferences sharedPreferences = preference.getSharedPreferences();
            String key = "turn_pref_editText";

           app.setMongoUserPreferences(sharedPreferences);

            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}
