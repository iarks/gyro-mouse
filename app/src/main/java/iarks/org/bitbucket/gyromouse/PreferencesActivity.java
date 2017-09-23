package iarks.org.bitbucket.gyromouse;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.util.Log;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;
import xdroid.toaster.Toaster;

public class PreferencesActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
    static SharedPreferences SP;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SP.registerOnSharedPreferenceChangeListener(this);
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.activity_preferences);
            android.preference.Preference pref = findPreference("reset");
            pref.setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(android.preference.Preference preference)
                {
                    Log.e(getClass().getName(),"Reset Preferences");
                    SharedPreferences.Editor editor = SP.edit();
                    editor.clear();
                    editor.apply();
                    resetPrefViews();
                    Globals.advanceChanged=1;
                    Toaster.toast("All values reset");
                    Toaster.toast("App may need to restart");

                    return true;
                }
            });
        }

        @Override
        public void onResume()
        {
            super.onResume();
        }

        void resetPrefViews()
        {
            android.preference.EditTextPreference tcpPref = (android.preference.EditTextPreference)findPreference("tcpPort");
            tcpPref.setText("13000");

            android.preference.EditTextPreference udpPref = (android.preference.EditTextPreference)findPreference("udpPort");
            udpPref.setText("9050");
        }
    }



//    private class PreferenceChanged implements SharedPreferences.OnSharedPreferenceChangeListener
//    {
//        @Override
//        public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
//        {
//            Log.e(getClass().getName(),"prefschangecalled");
//            if (key.equals("tcpPort"))
//            {
//                Log.e(getClass().getName(),"Changes made");
//                Globals.advanceChanged =1;
//            }
//            if (key.equals("udpPort"))
//            {
//                Log.e(getClass().getName(),"Changes made");
//                Globals.advanceChanged =1;
//            }
//            else
//            {
//                Globals.advanceChanged=0;
//            }
//        }
//    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
    {
        Log.e(getClass().getName(),"prefschangecalled");
        if (key.equals("tcpPort"))
        {
            Log.e(getClass().getName(),"Changes made");
            Globals.advanceChanged =1;
        }
        if (key.equals("udpPort"))
        {
            Log.e(getClass().getName(),"Changes made");
            Globals.advanceChanged =1;
        }
        else
        {
            Globals.advanceChanged=0;
        }
    }
}