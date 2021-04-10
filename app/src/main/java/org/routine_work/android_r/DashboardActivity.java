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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.routine_work.android_r.array.ArrayViewerActivity;
import org.routine_work.android_r.color.ColorViewerActivity;
import org.routine_work.android_r.dimension.DimensionViewerActivity;
import org.routine_work.android_r.drawable.DrawableViewerActivity;
import org.routine_work.android_r.layout.list_item.ListItemViewerActivity;
import org.routine_work.android_r.string.StringViewerActivity;
import org.routine_work.android_r.style.theme.ThemeViewerActivity;
import org.routine_work.utils.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class DashboardActivity extends Activity
        implements OnItemClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String ACTION_QUIT = DashboardActivity.class.getPackage().getName() + ".ACTION_QUIT";
    private static final String LOG_TAG = "android.R";
    // adater mapping
    private static final String MAP_KEY_ICON = "icon";
    private static final String MAP_KEY_LABEL = "label";
    private static final String MAP_KEY_TITLE = "title";
    private static final String MAP_KEY_CLASSNAME = "className";
    private static final String[] MAPPING_FROM =
            {
                    MAP_KEY_ICON,
                    MAP_KEY_LABEL,
                    MAP_KEY_TITLE,
                    MAP_KEY_CLASSNAME,
            };
    private static final int[] MAPPING_TO =
            {
                    R.id.icon_imageview,
                    R.id.label_textview,
                    R.id.title_textview,
                    R.id.classname_textview,
            };

    static {
        boolean debug = true;
        if (debug) {
            Log.setOutputLevel(Log.VERBOSE);
            Log.setTraceMode(true);
            Log.setIndentMode(true);
            Log.i(LOG_TAG, "Enable Debug Log");
        }
    }

    private SharedPreferences sharedPreferences;
    // preferences : view mode
    private String viewModePreferenceKey;
    private String viewModeGridValue;
    private String viewModeListValue;
    private String viewModeDefaultValue;
    // data
    private List<Map<String, String>> listData;
    // view
    private GridView gridView;
    private ListView listView;

    public static void quitApplication(Context context) {
        // Start root activity with ACTION_QUIT
        Intent quitIntent = new Intent(context, DashboardActivity.class);
        quitIntent.setAction(ACTION_QUIT);
        quitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(quitIntent);
    }

    public static void goDashboardActivity(Context context) {
        // Start DashboardActivity with CLEAR_TOP flag.
        Intent dashboardIntent = new Intent(context, DashboardActivity.class);
        dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(dashboardIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "Hello");

        super.onCreate(savedInstanceState);

        // init preference and callback
        String preferenceName = getPackageName() + "_preferences";
        sharedPreferences = getSharedPreferences(preferenceName, MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        if (ACTION_QUIT.equals(getIntent().getAction())) {
            Log.d(LOG_TAG, "intent.action => " + ACTION_QUIT);
            finish();
        } else {
//			requestWindowFeature(Window.FEATURE_LEFT_ICON);
            setContentView(R.layout.dashboard_activity);
//			getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ic_launcher_dot_r);

            viewModePreferenceKey = getString(R.string.dashboard_view_mode_key);
            viewModeGridValue = getString(R.string.dashboard_view_mode_grid_value);
            viewModeListValue = getString(R.string.dashboard_view_mode_list_value);
            viewModeDefaultValue = getString(R.string.dashboard_view_mode_default_value);

            gridView = (GridView) findViewById(R.id.dashboard_gridview);
            gridView.setOnItemClickListener(this);

            listView = (ListView) findViewById(R.id.dashboard_listview);
            listView.setOnItemClickListener(this);

            // init grid or list
            initLauncherView();
        }

        Log.v(LOG_TAG, "Bye");
    }

    @Override
    protected void onDestroy() {
        Log.v(LOG_TAG, "Hello");

        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();

        Log.v(LOG_TAG, "Bye");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.dashboard_menu, menu);
        menuInflater.inflate(R.menu.preference_menu, menu);
        menuInflater.inflate(R.menu.quit_menu, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem gridModeMenuItem = menu.findItem(R.id.grid_mode_menuitem);
        MenuItem listModeMenuItem = menu.findItem(R.id.list_mode_menuitem);

        String viewMode = sharedPreferences.getString(viewModePreferenceKey, viewModeDefaultValue);
        Log.d(LOG_TAG, "current viewMode => " + viewMode);

        if (viewModeGridValue.equals(viewMode)) {
            gridModeMenuItem.setVisible(false);
            listModeMenuItem.setVisible(true);
        } else if (viewModeListValue.equals(viewMode)) {
            gridModeMenuItem.setVisible(true);
            listModeMenuItem.setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = true;
        Log.v(LOG_TAG, "Hello item => " + item);

        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.grid_mode_menuitem:
            case R.id.list_mode_menuitem:
                String newMode = null;
                if (itemId == R.id.grid_mode_menuitem) {
                    newMode = viewModeGridValue;
                } else if (itemId == R.id.list_mode_menuitem) {
                    newMode = viewModeListValue;
                }

                if (newMode != null) {
                    Editor editor = sharedPreferences.edit();
                    editor.putString(viewModePreferenceKey, newMode);
                    editor.commit();
                }
                break;
            case R.id.preference_menuitem:
                Intent preferencesIntent = new Intent(this, PreferencesActivity.class);
                startActivity(preferencesIntent);
                break;
            case R.id.quit_menuitem:
                finish();
                break;
            default:
                result = super.onOptionsItemSelected(item);
        }

        Log.v(LOG_TAG, "Hello result => " + result);
        return result;
    }

    /**
     * android.widget.AdapterView.OnItemClickListener
     *
     * @param parentView
     * @param view
     * @param position
     * @param id
     */
    public void onItemClick(AdapterView<?> parentView, View view, int position, long id) {
        TextView classnameTextView = (TextView) view.findViewById(R.id.classname_textview);
        String classname = classnameTextView.getText().toString();
        try {
            Class<?> clazz = Class.forName(classname);
            Intent intent = new Intent(this, clazz);
            startActivity(intent);
        } catch (ClassNotFoundException ex) {
            Log.e(LOG_TAG, "Activity class not found.", ex);
        }
    }

    /**
     * SharedPreferences.OnSharedPreferenceChangeListener
     *
     * @param prefs
     * @param key
     */
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (viewModePreferenceKey.equals(key)) {
            initLauncherView();
        }
    }

    protected List<Map<String, String>> getListData() {
        if (listData == null) {
            listData = new ArrayList<Map<String, String>>();
            listData.add(new HashMap<String, String>() {

                {
                    put(MAP_KEY_ICON, String.valueOf(R.drawable.ic_menu_icon_list));
                    put(MAP_KEY_LABEL, getString(R.string.drawable_viewer));
                    put(MAP_KEY_TITLE, getString(R.string.drawable_viewer_title));
                    put(MAP_KEY_CLASSNAME, DrawableViewerActivity.class.getName());
                }
            });
            listData.add(new HashMap<String, String>() {

                {
                    put(MAP_KEY_LABEL, getString(R.string.color_viewer));
                    put(MAP_KEY_TITLE, getString(R.string.color_viewer_title));
                    put(MAP_KEY_ICON, String.valueOf(R.drawable.ic_menu_color_list));
                    put(MAP_KEY_CLASSNAME, ColorViewerActivity.class.getName());
                }
            });
            listData.add(new HashMap<String, String>() {

                {
                    put(MAP_KEY_LABEL, getString(R.string.string_viewer));
                    put(MAP_KEY_TITLE, getString(R.string.string_viewer_title));
                    put(MAP_KEY_ICON, String.valueOf(R.drawable.ic_menu_text_list));
                    put(MAP_KEY_CLASSNAME, StringViewerActivity.class.getName());
                }
            });
            listData.add(new HashMap<String, String>() {

                {
                    put(MAP_KEY_LABEL, getString(R.string.array_viewer));
                    put(MAP_KEY_TITLE, getString(R.string.array_viewer_title));
                    put(MAP_KEY_ICON, String.valueOf(R.drawable.ic_menu_list));
                    put(MAP_KEY_CLASSNAME, ArrayViewerActivity.class.getName());
                }
            });
            listData.add(new HashMap<String, String>() {

                {
                    put(MAP_KEY_LABEL, getString(R.string.dimension_viewer));
                    put(MAP_KEY_TITLE, getString(R.string.dimension_viewer_title));
                    put(MAP_KEY_ICON, String.valueOf(R.drawable.ic_menu_dimen_list));
                    put(MAP_KEY_CLASSNAME, DimensionViewerActivity.class.getName());
                }
            });
            listData.add(new HashMap<String, String>() {

                {
                    put(MAP_KEY_LABEL, getString(R.string.theme_viewer));
                    put(MAP_KEY_TITLE, getString(R.string.theme_viewer_title));
                    put(MAP_KEY_ICON, String.valueOf(R.drawable.ic_menu_list));
                    put(MAP_KEY_CLASSNAME, ThemeViewerActivity.class.getName());
                }
            });
            listData.add(new HashMap<String, String>() {

                {
                    put(MAP_KEY_LABEL, getString(R.string.list_item_viewer));
                    put(MAP_KEY_TITLE, getString(R.string.list_item_viewer_title));
                    put(MAP_KEY_ICON, String.valueOf(R.drawable.ic_menu_list));
                    put(MAP_KEY_CLASSNAME, ListItemViewerActivity.class.getName());
                }
            });
        }
        return listData;
    }

    private void initLauncherView() {
        String viewMode = sharedPreferences.getString(viewModePreferenceKey, viewModeDefaultValue);
        Log.i(LOG_TAG, "initLauncherView() viewMode => " + viewMode);

        if (viewModeGridValue.equals(viewMode)) {
            initGridView();
        } else if (viewModeListValue.equals(viewMode)) {
            initListView();
        }
    }

    private void initGridView() {
        SimpleAdapter adapter = new SimpleAdapter(this, getListData(),
                R.layout.dashboard_grid_item, MAPPING_FROM, MAPPING_TO);

        listView.setAdapter(null);
        listView.setVisibility(View.INVISIBLE);

        gridView.setAdapter(adapter);
        gridView.setVisibility(View.VISIBLE);
    }

    private void initListView() {
        SimpleAdapter adapter = new SimpleAdapter(this,
                getListData(), R.layout.dashboard_list_item, MAPPING_FROM, MAPPING_TO);

        gridView.setAdapter(null);
        gridView.setVisibility(View.INVISIBLE);

        listView.setAdapter(adapter);
        listView.setVisibility(View.VISIBLE);
    }
}
