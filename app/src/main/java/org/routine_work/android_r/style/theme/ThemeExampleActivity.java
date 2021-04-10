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
package org.routine_work.android_r.style.theme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import org.routine_work.android_r.DashboardActivity;
import org.routine_work.android_r.PreferencesActivity;
import org.routine_work.android_r.R;
import org.routine_work.utils.Log;
import org.routine_work.utils.SystemResourceUtils;

/**
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class ThemeExampleActivity extends Activity {

    public static final String EXTRA_KEY_THEME_NAME = "theme_name";
    public static final String EXTRA_KEY_THEME_ID = "theme_id";
    private static final String LOG_TAG = "android.R";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // get extra data
        Intent intent = getIntent();
        String themeName = intent.getStringExtra(EXTRA_KEY_THEME_NAME);
        int themeID = intent.getIntExtra(EXTRA_KEY_THEME_ID, -1);

        // set activity theme
        if (themeID != -1) {
            setTheme(themeID);
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.theme_example_activity);

        // set theme name
        if (themeName != null) {
            TextView themeNameTextView = (TextView) findViewById(R.id.theme_name_textview);
            themeNameTextView.setText(themeName);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.preference_menu, menu);
        menuInflater.inflate(R.menu.quit_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = true;
        Log.v(LOG_TAG, "Hello");

        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.preference_menuitem:
                Intent preferencesIntent = new Intent(this, PreferencesActivity.class);
                startActivity(preferencesIntent);
                break;
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

        Log.v(LOG_TAG, "Bye");
        return result;
    }
}
