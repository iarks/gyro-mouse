package iarks.org.bitbucket.gyromouse;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.util.Log;
import android.widget.TextView;

public class PreferencesActivity extends PreferenceActivity
{
    SharedPreferences  SP;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SP.registerOnSharedPreferenceChangeListener(new PreferenceChanged());

        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }


    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.activity_preferences);
        }
    }

    private class PreferenceChanged implements SharedPreferences.OnSharedPreferenceChangeListener
    {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
        {
            if (key.equals("tcpPort")|| key.equals("udpPort"))
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




//    @Override
//    public boolean onPreferenceClick(Preference preference)
//    {
//        if(preference.getKey().equals("reset"))
//        {
//            Log.e(getClass().getName(),"Reset Preferences");
//            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PreferencesActivity.this);
//            SharedPreferences.Editor editor = preferences.edit();
//            editor.clear();
//            editor.apply();
//            return true;
//        }
//        return false;
//    }
}