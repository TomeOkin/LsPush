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
package com.tomeokin.lspush.data.remote.util;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络检测工具类
 */
public class NetworkUtils {
    public static boolean isNetworkAvailable(Application app) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        }

        NetworkInfo active = connectivityManager.getActiveNetworkInfo();
        return connectivityManager.getBackgroundDataSetting()
                && active != null
                && active.isConnected();
    }

    public static boolean isWifi(Application app) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        }

        NetworkInfo active = connectivityManager.getActiveNetworkInfo();
        return active.getType() == ConnectivityManager.TYPE_WIFI;
    }
}
