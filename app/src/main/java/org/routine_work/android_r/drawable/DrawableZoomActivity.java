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
package org.routine_work.android_r.drawable;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.routine_work.android_r.DashboardActivity;
import org.routine_work.android_r.PreferencesActivity;
import org.routine_work.android_r.R;
import org.routine_work.utils.Log;
import org.routine_work.utils.SystemResourceUtils;

public class DrawableZoomActivity extends Activity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String EXTRA_DRAWABLE_ID = "drawable_id_key";
    public static final String EXTRA_DRAWABLE_NAME = "drawable_name_key";
    private static final int DEFAULT_DRAWABLE_ID = android.R.drawable.sym_def_app_icon;
    private static final String DEFAULT_DRAWABLE_NAME = "sym_def_app_icon";
    private static final String LOG_TAG = "android.R";
    // preferences
    private SharedPreferences sharedPreferences;
    private String bgColorPreferenceKey;
    private String bgColorDefaultValue;

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

        return result;
    }

    /**
     * SharedPreferences.OnSharedPreferenceChangeListener
     *
     * @param prefs
     * @param prefKey
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String prefKey) {
        Log.v(LOG_TAG, "Hello ");
        Log.d(LOG_TAG, "prefKey => " + prefKey);

        if (bgColorPreferenceKey.equals(prefKey)) {
            updateBackgroundColor();
        }

        Log.v(LOG_TAG, "Bye");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = true;

        if (event.getAction() == MotionEvent.ACTION_UP) {
            finish();
        } else {
            result = super.onTouchEvent(event);
        }

        return result;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean result = true;

        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            finish();
        } else {
            result = super.onKeyUp(keyCode, event);
        }

        return result;
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawable_zoom_activity);

        String preferenceName = getPackageName() + "_preferences";
        sharedPreferences = getSharedPreferences(preferenceName, MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        bgColorPreferenceKey = getString(R.string.drawable_background_color_key);
        bgColorDefaultValue = getString(R.string.drawable_background_color_default_value);

        // update image background color
        updateBackgroundColor();

        // set image
        int drawableID = getIntent().getIntExtra(EXTRA_DRAWABLE_ID, DEFAULT_DRAWABLE_ID);
        ImageView iconImageView = (ImageView) findViewById(R.id.drawable_imageview);
        iconImageView.setImageResource(drawableID);

        // set title
        String drawableName = getIntent().getStringExtra(EXTRA_DRAWABLE_NAME);
        if (drawableName == null) {
            drawableName = DEFAULT_DRAWABLE_NAME;
        }
        setTitle(drawableName);

        // test
        Drawable drawable = getResources().getDrawable(drawableID);
        TextView drawableTypeTextView = (TextView) findViewById(R.id.drawable_type_textview);
        drawableTypeTextView.setText(drawable.getClass().getSimpleName());

        if (BitmapDrawable.class.equals(drawable.getClass())) {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), drawableID, opt);

            TextView drawableSizeTextView = (TextView) findViewById(R.id.drawable_size_textview);
            drawableSizeTextView.setText(opt.outWidth + " x " + opt.outHeight);
        }
    }

    @Override
    protected void onDestroy() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    private void updateBackgroundColor() {
        String bgColorString = sharedPreferences.getString(bgColorPreferenceKey, bgColorDefaultValue);
        int bgColor = Color.parseColor(bgColorString);
        Log.d(LOG_TAG, "bgColor => " + Integer.toHexString(bgColor));
        View drawableFrameLayout = findViewById(R.id.drawable_framelayout);
        drawableFrameLayout.setBackgroundColor(bgColor);
    }
}
