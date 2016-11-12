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
package com.tomeokin.lspush.util;

import android.content.Context;

import java.io.File;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class FileNameUtils {
    private FileNameUtils() { }

    private static final AtomicInteger sIndex = new AtomicInteger(0);

    public static File getJPEGFile(Context context) {
        // don't use getCacheDir(), otherwise camera will fail to put data into it.
        // when use getCacheDir(), need to grant a write and read permission to uri
        return new File(context.getExternalCacheDir(), "temp_" + getFilename() + ".jpg");
    }

    private static String getFilename() {
        Date date = new Date();
        CharSequence charSequence = android.text.format.DateFormat.format("yyyyMMddkkmmss", date);
        return charSequence + String.format(Locale.ENGLISH, "%02d", sIndex.getAndIncrement());
    }
}
