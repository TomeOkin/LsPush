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
import android.net.Uri;

public class UriUtils {
    public static final String ANDROID_RESOURCE = "android.resource://";
    public static final String FORWARD_SLASH = "/";

    private UriUtils() { }

    public static Uri resourceIdToUri(Context context, int resourceId) {
        return Uri.parse(ANDROID_RESOURCE + context.getPackageName() + FORWARD_SLASH + resourceId);
    }

    public static boolean isEmpty(Uri uri) {
        return uri == null || Uri.EMPTY.equals(uri);
    }
}
