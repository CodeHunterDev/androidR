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

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.routine_work.android_r.R;
import org.routine_work.utils.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sawai
 */
public class BackgroundColorListAdapterFactory
        implements SimpleAdapter.ViewBinder {

    private static final String LOG_TAG = "android.R";
    private static final String MAP_KEY_COLOR_NAME = "name";
    private static final String MAP_KEY_COLOR_VALUE = "color_value";
    private static final String[] MAPPING_FROM = new String[]
            {
                    MAP_KEY_COLOR_NAME, MAP_KEY_COLOR_VALUE, MAP_KEY_COLOR_VALUE,
            };
    private static final int[] MAPPING_TO = new int[]
            {
                    R.id.color_name_textview,
                    R.id.color_code_textview,
                    R.id.color_sample_view,
            };
    private String[] colorValues;
    private String[] colorNames;
    private List<Map<String, String>> colorListData;
    private Context context;

    public BackgroundColorListAdapterFactory(Context context) {
        this.context = context;
    }

    public ListAdapter createBackgroundColorListAdapter() {
        SimpleAdapter listAdapter;
        Log.v(LOG_TAG, "Hello");

        listAdapter = new SimpleAdapter(context, getColorListData(),
                R.layout.color_chooser_dialog_list_item, MAPPING_FROM, MAPPING_TO);
        listAdapter.setViewBinder(this);

        Log.v(LOG_TAG, "Bye");
        return listAdapter;
    }

    @Override
    public boolean setViewValue(View view, Object data, String dataText) {
        boolean result = false;

        if (view.getId() == R.id.color_sample_view) {
            int parsedColor = Color.parseColor(dataText);
            view.setBackgroundColor(parsedColor);
            result = true;
        } else if (view instanceof TextView) {
            TextView textView = (TextView) view;
            textView.setText(dataText);
            result = true;
        }

        return result;
    }

    public synchronized String[] getColorNames() {
        if (colorNames == null) {
            colorNames = context.getResources().getStringArray(R.array.drawable_background_color_entries);
        }
        return colorNames;
    }

    public synchronized String[] getColorValues() {
        if (colorValues == null) {
            colorValues = context.getResources().getStringArray(R.array.drawable_background_color_values);
        }
        return colorValues;
    }

    public int getColorCount() {
        return getColorValues().length;
    }

    public int getIndexByColorValue(String colorValue) {
        int result = -1;

        String[] values = getColorValues();
        for (int i = 0; i < values.length; i++) {
            if (colorValue.equals(values[i])) {
                result = i;
            }
        }

        return result;
    }

    public String getColorNameAt(int index) {
        String colorName = null;

        String[] names = getColorNames();
        if (index >= 0 && index < names.length) {
            colorName = names[index];
        }

        return colorName;
    }

    public String getColorValueAt(int index) {
        String colorValue = null;

        String[] values = getColorValues();
        if (index >= 0 && index < values.length) {
            colorValue = values[index];
        }

        return colorValue;
    }

    private synchronized List<Map<String, String>> getColorListData() {
        if (colorListData == null) {
            colorListData = new ArrayList<Map<String, String>>();
            String[] names = getColorNames();
            String[] values = getColorValues();
            for (int i = 0; i < names.length; i++) {

                Map<String, String> colorData = new HashMap<String, String>();
                colorData.put(MAP_KEY_COLOR_NAME, names[i]);
                colorData.put(MAP_KEY_COLOR_VALUE, values[i]);
                colorListData.add(colorData);
            }
        }

        return colorListData;
    }
}
