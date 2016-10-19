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
package com.tomeokin.lspush.common;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.tomeokin.lspush.R;

import java.util.List;

public class IntentUtils {
    public static void openInBrowser(Context context, String url) {
        final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        PackageManager packageManager = context.getPackageManager();
        if (packageManager.resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
            context.startActivity(browserIntent);
        } else {
            List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(browserIntent, 0);
            if (resolveInfoList != null && resolveInfoList.size() > 1) {
                context.startActivity(Intent.createChooser(browserIntent, context.getString(R.string.select_browser)));
            } else {
                context.startActivity(browserIntent);
            }
        }
    }
}
