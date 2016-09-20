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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {
    private static NetworkUtils sInstance;
    private static ConnectivityManager mConnectivityManager;

    private NetworkUtils(Context context) {
        mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static void init(Context context) {
        if (sInstance == null) {
            sInstance = new NetworkUtils(context.getApplicationContext());
        }
    }

    public NetworkUtils(ConnectivityManager connectivityManager) {
        mConnectivityManager = connectivityManager;
    }

    public static boolean connectingNetwork() {
        if (mConnectivityManager == null) {
            return false;
        }
        final NetworkInfo active = mConnectivityManager.getActiveNetworkInfo();
        return active != null && active.isConnected();
    }

    public static boolean connectingWifi() {
        return mConnectivityManager != null
            && mConnectivityManager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
    }
}
