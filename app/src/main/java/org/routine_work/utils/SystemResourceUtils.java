/*
 * The MIT License
 *
 * Copyright 2012 Masahiko, SAWAI <masahiko.sawai@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.routine_work.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Masahiko, SAWAI <masahiko.sawai@gmail.com>
 */
public class SystemResourceUtils {

    private static final Map<String, Integer> RESOURCE_ID_MAP = new HashMap<String, Integer>();
    private static final String LOG_TAG = "simple-battery-logger";

    public static synchronized boolean exist(String name) {
        boolean result = false;

        if (RESOURCE_ID_MAP.containsKey(name)) {
            result = true;
        } else {
            int resourceId = getResourceId(name);
            if (resourceId != -1) {
                result = true;
            }
        }

        return result;
    }

    public static synchronized int getResourceId(String name) {
        int id = -1;

        try {
            if (RESOURCE_ID_MAP.containsKey(name)) {
                id = RESOURCE_ID_MAP.get(name);
            } else {
                int lastPeriodIndex = name.lastIndexOf(".");
                if (lastPeriodIndex != -1) {
                    String className = name.substring(0, lastPeriodIndex);
                    String fieldName = name.substring(lastPeriodIndex + 1);
                    Log.d(LOG_TAG, "className => " + className);
                    Log.d(LOG_TAG, "fieldName => " + fieldName);
                    Class clazz = Class.forName(className);
                    Log.d(LOG_TAG, "clazz => " + clazz);
                    Field field = clazz.getDeclaredField(fieldName);
                    System.out.println("field => " + field);
                    Log.d(LOG_TAG, "field => " + field);
                    if ((field != null) && (field.getType() == int.class)) {
                        Log.d(LOG_TAG, "field.type => " + field.getType());
                        id = field.getInt(null);
                        RESOURCE_ID_MAP.put(name, id);
                        Log.d(LOG_TAG, "Add to RESOURCE_ID_MAP. key => " + name + ", value => " + id);
                    }
                }
            }
        } catch (Exception e) {
        }

        return id;
    }
}
