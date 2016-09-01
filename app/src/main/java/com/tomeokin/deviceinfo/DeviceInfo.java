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
package com.tomeokin.deviceinfo;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import java.io.File;
import java.io.FileFilter;

/**
 * see https://github.com/facebook/device-year-class/blob/master/yearclass/src/main/java/com/facebook/device/yearclass/DeviceInfo.java
 */
public class DeviceInfo {
    private static DeviceInfo sInstance;
    private int cpuCores = -1;

    public static DeviceInfo getInstance() {
        if (sInstance == null) {
            sInstance = new DeviceInfo();
        }
        return sInstance;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN) public static long getTotalMemory(Context context) {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryInfo(memoryInfo);
        return memoryInfo.totalMem;
    }

    private static final FileFilter CPU_FILTER = new FileFilter() {
        @Override public boolean accept(File pathname) {
            // pathname.getName().matches("cpu[0-9]+");
            String path = pathname.getName();
            return path.startsWith("cpu") && Character.isDigit(path.charAt(3));
        }
    };

    public final int getNumberOfCPUCores() {
        try {
            this.cpuCores = new File("/sys/devices/system/cpu/").listFiles(CPU_FILTER).length;
            if (this.cpuCores == 0) {
                this.cpuCores = -1;
            }
        } catch (Exception localException) {
            this.cpuCores = -1;
        }
        return this.cpuCores;
    }

    public final int getCpuCores() {
        if (cpuCores <= 0) {
            cpuCores = getNumberOfCPUCores();
        }

        if (cpuCores == -1) {
            cpuCores = Math.max(Runtime.getRuntime().availableProcessors(), 1);
        }
        return cpuCores;
    }
}
