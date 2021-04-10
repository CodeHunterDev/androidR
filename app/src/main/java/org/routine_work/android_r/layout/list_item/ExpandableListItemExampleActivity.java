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
package org.routine_work.android_r.layout.list_item;

import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;

import org.routine_work.android_r.DashboardActivity;
import org.routine_work.android_r.PreferencesActivity;
import org.routine_work.android_r.R;
import org.routine_work.utils.SystemResourceUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpandableListItemExampleActivity extends ExpandableListActivity
        implements ListItemExampleConstants {

    private static final int DEFAULT_LIST_ITEM_LAYOUT_ID = android.R.layout.simple_expandable_list_item_2;
    private static final String DEFAULT_LIST_ITEM_LAYOUT_NAME = "simple_expandable_list_item_2";
    private List<Map<String, String>> userList;
    private List<Map<String, String>> expandableUserGroup;
    private List<List<Map<String, String>>> expandableUserList;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_expandable_list_activity);

        // get name and id from Intent
        int listItemLayoutID = getIntent().getExtras().getInt(EXTRA_LIST_ITEM_LAYOUT_ID, DEFAULT_LIST_ITEM_LAYOUT_ID);
        String listItemLayoutName = getIntent().getExtras().getString(EXTRA_LIST_ITEM_LAYOUT_NAME);
        if (listItemLayoutName == null || listItemLayoutName.length() == 0) {
            listItemLayoutName = DEFAULT_LIST_ITEM_LAYOUT_NAME;
        }

        setTitle(listItemLayoutName);

        // initialize list adapter
        SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(this,
                getExpandableGroupList(),
                listItemLayoutID,
                GROUP_MAPPING_FROM,
                GROUP_MAPPING_TO,
                getExpandableUserList(),
                listItemLayoutID,
                USER_MAPPING_FROM,
                USER_MAPPING_TO);
        setListAdapter(adapter);

        // initialize ListView
        ListView listView = (ListView) findViewById(android.R.id.list);
        if (listItemLayoutID == android.R.layout.simple_list_item_single_choice) {
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        } else {
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
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

    private List<Map<String, String>> getUserList() {
        if (userList == null) {
            // initialize userList
            userList = new ArrayList<Map<String, String>>();
            String[] userNames = getResources().getStringArray(R.array.names);
            for (String name : userNames) {
                Map<String, String> user = new HashMap<String, String>();
                user.put(KEY_NAME, name);
                user.put(KEY_EMAIL, name.toLowerCase() + "@example.com");
                user.put(KEY_ICON, String.valueOf(android.R.drawable.sym_def_app_icon));
                userList.add(user);
            }
        }
        return userList;
    }

    private List<List<Map<String, String>>> getExpandableUserList() {
        if (expandableUserList == null) {
            expandableUserList = new ArrayList<List<Map<String, String>>>();
            List<Map<String, String>> list = getUserList();
            for (Map<String, String> user : list) {
                List<Map<String, String>> childList = new ArrayList<Map<String, String>>();
                childList.add(user);
                expandableUserList.add(childList);
            }
        }
        return expandableUserList;
    }

    private List<Map<String, String>> getExpandableGroupList() {
        if (expandableUserGroup == null) {
            expandableUserGroup = new ArrayList<Map<String, String>>();
            for (Map<String, String> user : getUserList()) {
                String userName = user.get(KEY_NAME);
                String firstChar = userName.substring(0, 1);

                Map<String, String> group = new HashMap<String, String>();
                group.put(KEY_NAME, firstChar);
                expandableUserGroup.add(group);
            }
        }
        return expandableUserGroup;
    }
}
