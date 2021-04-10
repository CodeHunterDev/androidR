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

/**
 * @author sawai
 */
public interface ListItemExampleConstants {

    String EXTRA_LIST_ITEM_LAYOUT_ID = "list_item_layout_id_key";
    String EXTRA_LIST_ITEM_LAYOUT_NAME = "list_item_layout_name_key";
    String KEY_NAME = "name";
    String KEY_EMAIL = "email";
    String KEY_ICON = "icon";
    String[] USER_MAPPING_FROM =
            {
                    KEY_NAME,
                    KEY_EMAIL,
                    KEY_ICON,
            };
    int[] USER_MAPPING_TO =
            {
                    android.R.id.text1,
                    android.R.id.text2,
                    android.R.id.icon,
            };
    String[] GROUP_MAPPING_FROM =
            {
                    KEY_NAME,
            };
    int[] GROUP_MAPPING_TO =
            {
                    android.R.id.text1,
            };
}
