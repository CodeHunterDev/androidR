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

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import org.routine_work.android_r.R;
import org.routine_work.utils.Log;

/**
 * @author sawai
 */
public class BackgroundColorDialogPreference extends DialogPreference {

    private static final String LOG_TAG = "android.R";
    private BackgroundColorListAdapterFactory backgroundColorListAdapterFactory;
    private int checkedColorIndex = -1;

    public BackgroundColorDialogPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        backgroundColorListAdapterFactory = new BackgroundColorListAdapterFactory(context);
    }

    public BackgroundColorDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        backgroundColorListAdapterFactory = new BackgroundColorListAdapterFactory(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = super.onCreateView(parent);

        updateSummary();

        return view;
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        Log.v(LOG_TAG, "Hello");

        super.onPrepareDialogBuilder(builder);
        ListAdapter listAdapter = backgroundColorListAdapterFactory.createBackgroundColorListAdapter();

        String bgColorDefaultValue = getContext().getString(R.string.drawable_background_color_default_value);
        String colorValue = getPersistedString(bgColorDefaultValue);
        Log.d(LOG_TAG, "colorValue => " + colorValue);
        int colorIndex = backgroundColorListAdapterFactory.getIndexByColorValue(colorValue);
        Log.d(LOG_TAG, "colorIndex => " + colorIndex);
        if (colorIndex >= 0 && colorIndex < backgroundColorListAdapterFactory.getColorCount()) {
            checkedColorIndex = colorIndex;
            String colorName = backgroundColorListAdapterFactory.getColorNameAt(checkedColorIndex);
            Log.d(LOG_TAG, "colorName => " + colorName);
            if (colorName != null) {
                setSummary(colorName);
            }
        }

        builder.setSingleChoiceItems(listAdapter, checkedColorIndex, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int index) {
                Log.v(LOG_TAG, "Hello");
                Log.d(LOG_TAG, "index => " + index);

                if (index >= 0 && index < backgroundColorListAdapterFactory.getColorCount()) {
                    checkedColorIndex = index;
                }

                Log.d(LOG_TAG, "checkedColorIndex => " + checkedColorIndex);
                Log.v(LOG_TAG, "Bye");
            }
        });

        Log.v(LOG_TAG, "Bye");
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        Log.v(LOG_TAG, "Hello");

        Log.d(LOG_TAG, "positiveResult => " + positiveResult);
        if (positiveResult) {
            String colorValue = backgroundColorListAdapterFactory.getColorValueAt(checkedColorIndex);
            Log.d(LOG_TAG, "checked color value => " + colorValue);
            // save to preferences
            if (callChangeListener(colorValue)) {
                Log.d(LOG_TAG, "callChangeListener() is true.");
                persistString(colorValue);
                updateSummary();
            }
        }
        super.onDialogClosed(positiveResult);

        Log.v(LOG_TAG, "Bye");
    }

    private void updateSummary() {
        String bgColorDefaultValue = getContext().getString(R.string.drawable_background_color_default_value);
        String bgColorValue = getPersistedString(bgColorDefaultValue);
        int index = backgroundColorListAdapterFactory.getIndexByColorValue(bgColorValue);
        String bgColorName = backgroundColorListAdapterFactory.getColorNameAt(index);
        Log.d(LOG_TAG, "bgColorName => " + bgColorName);
        if (bgColorName != null) {
            setSummary(bgColorName);
        }
    }
}
