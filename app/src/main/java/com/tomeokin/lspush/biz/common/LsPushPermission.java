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
package com.tomeokin.lspush.biz.common;

import android.content.Context;
import android.content.pm.PackageManager;

public class LsPushPermission {
    public static final String LSPUSH_APP_CALLING = "com.tomeokin.lspush.permission.LSPUSH_APP_CALLING";

    /**
     * 通过权限（签名级别保护）检查判断调用方是否来自应用内部。
     * note: use {@link Context#checkCallingOrSelfPermission(String)} instead of {@link
     * Context#checkCallingPermission(String)}, because {@link Context#checkCallingPermission(String)} will always fail
     * if you are not using IPC.
     */
    public static boolean checkLsPushPermission(Context context) {
        int check = context.checkCallingOrSelfPermission(LSPUSH_APP_CALLING);
        if (check == PackageManager.PERMISSION_DENIED) {
            return false;
        }

        // may add other check in this
        return true;
    }
}
