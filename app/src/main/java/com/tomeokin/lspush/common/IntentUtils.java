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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.tomeokin.lspush.R;

import java.util.ArrayList;
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

    /**
     * @see <a href="http://imesong.com/2016/04/05/Android%E5%8F%8D%E7%BC%96%E8%AF%91%E5%AE%9E%E6%88%98%EF%BC%8D%E6%8F%AD%E7%A7%98%E7%8C%8E%E8%B1%B9%E8%AE%BE%E7%BD%AE%E9%BB%98%E8%AE%A4%E6%B5%8F%E8%A7%88%E5%99%A8%E5%AE%9E%E7%8E%B0%E9%80%BB%E8%BE%91">Android反编译－揭秘猎豹设置默认浏览器逻辑</a>
     */
    public static ArrayList<ResolveInfo> getDefaultResolveInfoList(Context context, String url) {
        final PackageManager packageManager = context.getPackageManager();
        final Uri uri = Uri.parse(url);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(uri);

        ArrayList<ResolveInfo> supportResolveInfoList = new ArrayList<>();
        try {
            List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, 0);
            if (resolveInfoList != null) {
                List<ComponentName> activities = new ArrayList<>();
                List<IntentFilter> intentFilters = new ArrayList<>();

                for (ResolveInfo item : resolveInfoList) {
                    packageManager.getPreferredActivities(intentFilters, activities, item.activityInfo.packageName);
                    for (IntentFilter filter : intentFilters) {
                        if (supportAccess(filter) && supportDataScheme(filter, uri.getScheme())) {
                            supportResolveInfoList.add(item);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return supportResolveInfoList;
    }

    private static boolean supportAccess(IntentFilter filter) {
        return filter.hasCategory(Intent.CATEGORY_BROWSABLE) || filter.hasCategory(Intent.CATEGORY_DEFAULT);
    }

    private static boolean supportDataScheme(IntentFilter filter, String scheme) {
        return filter.hasDataScheme(scheme);
    }
}
