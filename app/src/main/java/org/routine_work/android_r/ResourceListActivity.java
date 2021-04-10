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

import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.routine_work.utils.Log;
import org.routine_work.utils.SystemResourceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ResourceListActivity is a super class of *ViewerActivity.
 */
public abstract class ResourceListActivity extends ListActivity
        implements TextView.OnEditorActionListener, View.OnClickListener {

    protected static final String API_BASE_URI = "http://developer.android.com/reference/android/";
    private static final String LOG_TAG = "android.R";
    private final List<Cursor> searchWordCursorList = new ArrayList<Cursor>();
    private SearchWordDBHelper searchWordDBHelper;
    private SQLiteDatabase searchWordDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resource_list_activity);

        initializeSearchViews();
        initializeListData();
        initializeSearchWordAutoComplete();
    }

    @Override
    protected void onDestroy() {
        finalizeListData();
        finalizeSearchWordAutoComplete();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.resource_viewer_menu, menu);
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
            case R.id.open_reference_menuitem:
                Uri referenceUri = getApiReferenceUri();
                if (referenceUri != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, referenceUri);
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(this, R.string.browser_not_found, Toast.LENGTH_LONG).show();
                    }
                }
                break;
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean result = true;

        if (event.getKeyCode() == KeyEvent.KEYCODE_SEARCH
                && event.getAction() == KeyEvent.ACTION_UP) {
            EditText searchWordEditText = (EditText) findViewById(R.id.search_word_textview);
            searchWordEditText.requestFocus();
            openIMEWindow(searchWordEditText);
        } else {
            result = super.dispatchKeyEvent(event);
        }

        return result;

    }

    /**
     * TextView.OnEditorActionListener#onEditorAction()
     *
     * @param textView
     * @param actionId
     * @param ke
     * @return
     */
    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent ke) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            // close ime window
            closeIMEWindow(textView);
            updateListData();
            updateSearchWordDB();
        }
        return true;
    }

    /**
     * View.OnClickListener#onClick(View view)
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        int viewID = view.getId();
        if (viewID == R.id.search_go_button) {
            closeIMEWindow(view);
            updateListData();
            updateSearchWordDB();
        }
    }

    protected Uri getApiReferenceUri() {
        return Uri.parse(API_BASE_URI + "R." + getResourceType() + ".html");
    }

    protected abstract String getResourceType();

    protected abstract void initializeListData();

    protected abstract void finalizeListData();

    protected abstract void updateListData();

    protected void initializeSearchViews() {
        EditText searchWordEditText = (EditText) findViewById(R.id.search_word_textview);
        searchWordEditText.setOnEditorActionListener(this);

        Button searchGoButton = (Button) findViewById(R.id.search_go_button);
        searchGoButton.setOnClickListener(this);
    }

    protected void initializeSearchWordAutoComplete() {
        Log.v(LOG_TAG, "Hello");
        searchWordDBHelper = new SearchWordDBHelper(this);
        searchWordDB = searchWordDBHelper.getWritableDatabase();

        final String[] from =
                {
                        SearchWordDBConstants.Columns.SEARCH_WORD,
                };
        final int[] to =
                {
                        android.R.id.text1,
                };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_dropdown_item_1line, null, from, to);

        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence value) {
                Cursor cursor = searchWordDBHelper.selectSearchWords(searchWordDB,
                        getResourceType(), value);
                synchronized (searchWordCursorList) {
                    searchWordCursorList.add(cursor);
                }
                return cursor;
            }
        });
        adapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            public CharSequence convertToString(Cursor cursor) {
                int index = cursor.getColumnIndex(SearchWordDBConstants.Columns.SEARCH_WORD);
                String searchWord = cursor.getString(index);
                return searchWord;
            }
        });

        AutoCompleteTextView searchWordAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.search_word_textview);
        searchWordAutoCompleteTextView.setAdapter(adapter);

        Log.v(LOG_TAG, "Bye");
    }

    protected void finalizeSearchWordAutoComplete() {
        Log.v(LOG_TAG, "Hello");

        synchronized (searchWordCursorList) {
            for (Cursor cursor : searchWordCursorList) {
                cursor.close();
            }
            searchWordCursorList.clear();
        }
        if (searchWordDB != null) {
            searchWordDB.close();
            searchWordDB = null;
        }

        Log.v(LOG_TAG, "Bye");
    }

    private void updateSearchWordDB() {
        AutoCompleteTextView searchWordAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.search_word_textview);
        String searchWord = searchWordAutoCompleteTextView.getText().toString().trim();
        if (searchWord.length() > 1) {
            searchWordDBHelper.insertOrUpdateSearchWord(searchWordDB, getResourceType(), searchWord);
        }
    }

    private void openIMEWindow(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    private void closeIMEWindow(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
