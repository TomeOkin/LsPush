/*
 * Copyright 2016 TomeOkin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tomeokin.lspush;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;

import timber.log.Timber;

public class CrashReportingTree extends Timber.Tree {
    private static final int MAX_LOG_LENGTH = 4000;
    private final Context context;

    public CrashReportingTree(final Context context) {
        this.context = context;
        CrashReport.initCrashReport(context);
    }

    @Override
    protected boolean isLoggable(int priority) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
            return false;
        }

        // only warn、error、wtf
        return true;
    }

    /**
     * I use @SuppressLint("LogTagMismatch") because AS is making a mistake with the inspection,
     * where there is Timber.isLoggable(priority) but not the Android's Log.isLoggable(tag,
     * priority).
     */
    @SuppressLint("LogTagMismatch")
    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if (isLoggable(priority)) {
            if (priority == Log.ASSERT) {
                // need to create tag first in bugly web console
                // CrashReport.setUserSceneTag(context, yourUserSceneTagId);
                CrashReport.postCatchedException(t);  // bugly 会将这个 throwable 上报
                return;
            }

            if (message.length() < MAX_LOG_LENGTH) {
                Log.println(priority, tag, message);
                return;
            }

            for (int i = 0, length = message.length(); i < length; i++) {
                int newline = message.indexOf("\n", i);
                newline = newline != -1 ? newline : length;
                do {
                    int end = Math.min(newline, i + MAX_LOG_LENGTH);
                    String part = message.substring(i, end);
                    Log.println(priority, tag, part);
                    i = end;
                } while (i < newline);
            }
        }
    }
}
