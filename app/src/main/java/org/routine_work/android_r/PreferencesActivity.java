/*
 *  The MIT License
 *
 *  Copyright 2011-2012 Masahiko, SAWAI <masahiko.sawai@gmail.com>.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package org.routine_work.android_r;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.routine_work.utils.Log;
import org.routine_work.utils.SystemResourceUtils;

/**
 * @author sawai
 */
public class PreferencesActivity extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOG_TAG = "android.R";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "Hello");

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        sharedPreferences = getPreferenceManager().getSharedPreferences();

        updateSummary();

        Log.v(LOG_TAG, "Bye");
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getPreferenceScreen().getSharedPreferences();
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        SharedPreferences preferences = getPreferenceScreen().getSharedPreferences();
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.quit_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = true;

        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.quit_menuitem:
                DashboardActivity.quitApplication(this);
                break;
            default:
                if (itemId == SystemResourceUtils.getResourceId("android.R$id.home")) {
                    DashboardActivity.goDashboardActivity(this);
                } else {
                    result = super.onOptionsItemSelected(item);
                }
        }

        return result;
    }

    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        Log.v(LOG_TAG, "Hello");
        Log.i(LOG_TAG, "shared preference " + key + " is changed.");

        updateSummary();

        Log.v(LOG_TAG, "Bye");
    }

    private void updateSummary() {
        String prefKey;
        CharSequence summary;

        prefKey = getString(R.string.dashboard_view_mode_key);
        ListPreference dashboardViewModePreference = (ListPreference) getPreferenceScreen().findPreference(prefKey);
        summary = dashboardViewModePreference.getEntry();
        dashboardViewModePreference.setSummary(summary);

        /*
         * String bgColorKey =
         * getString(R.string.drawable_background_color_key); String
         * bgColordefaultValue =
         * getString(R.string.drawable_background_color_default_value); String
         * bgColorValue = sharedPreferences.getString(bgColorKey,
         * bgColordefaultValue);
         *
         * BackgroundColorListAdapterFactory factory = new
         * BackgroundColorListAdapterFactory(this); String[] colorNames =
         * factory.getColorNames(); String[] colorValues =
         * factory.getColorValues(); String bgColorName = null; for (int i = 0;
         * i < colorValues.length; i++) { if
         * (bgColorValue.equals(colorValues[i])) { bgColorName = colorNames[i];
         * break; } }
         *
         * if (bgColorName != null) { BackgroundColorDialogPreference
         * bgColorPreference = (BackgroundColorDialogPreference)
         * getPreferenceScreen().findPreference(bgColorKey);
         * bgColorPreference.setSummary(bgColorName); }
         *
         */

        /*
         * prefKey = getString(R.string.drawable_background_color_key);
         * ListPreference bgColorPreference = (ListPreference)
         * getPreferenceScreen().findPreference(bgColorKey); summary =
         * bgColorPreference.getEntry(); bgColorPreference.setSummary(summary);
         */
    }
}
