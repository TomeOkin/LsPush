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
package com.tomeokin.nativestackblur;

import android.graphics.Bitmap;

import com.tomeokin.deviceinfo.DeviceInfo;

public class NativeBlurUtil {
    private static final int THREAD_COUNT = DeviceInfo.getInstance().getCpuCores();
    private static boolean ready = false;
    private static NativeBlurUtil sInstance;

    static {
        try {
            System.loadLibrary("stackblur");
            ready = true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static NativeBlurUtil getInstance() {
        if (sInstance == null) {
            sInstance = new NativeBlurUtil();
        }
        return sInstance;
    }

    public Bitmap blur(Bitmap bitmap, float scale, int radius) {
        bitmap = Bitmap.createScaledBitmap(bitmap, Math.round(bitmap.getWidth() * scale),
            Math.round(bitmap.getHeight() * scale), false);
        blur(bitmap, radius);
        return bitmap;
    }

    public void blur(Bitmap original, int radius) {
        blur(original, radius, false);
    }

    public Bitmap blur(Bitmap original, int radius, boolean copy) {
        if (!ready) {
            return null;
        }
        if (radius < 2 || radius > 254) {
            return original;
        }
        Bitmap bitmapOut = copy ? original.copy(Bitmap.Config.ARGB_8888, true) : original;
        functionToBlur(bitmapOut, radius, THREAD_COUNT);
        return bitmapOut;
    }

    private static native void functionToBlur(Bitmap bitmapOut, int radius, int threadCount);
}
