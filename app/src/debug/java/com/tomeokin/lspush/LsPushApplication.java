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

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.LogInterceptor;
import com.orhanobut.logger.Logger;
import com.squareup.leakcanary.LeakCanary;
import com.tomeokin.lspush.biz.common.UserScene;
import com.tomeokin.lspush.common.NetworkUtils;
import com.tomeokin.lspush.config.LsPushConfig;
import com.tomeokin.lspush.data.crypt.BeeCrypto;
import com.tomeokin.lspush.data.crypt.BeeEncryption;
import com.tomeokin.lspush.injection.component.AppComponent;
import com.tomeokin.lspush.injection.component.DaggerAppComponent;
import com.tomeokin.lspush.injection.module.AppModule;

import timber.log.Timber;

public class LsPushApplication extends Application {
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }

        // https://github.com/square/leakcanary/issues/322
        // https://medium.com/@amitshekhar/android-memory-leaks-inputmethodmanager-solved-a6f2fe1d1348#.i060weodr
        LeakCanary.install(this);
        initializeStetho(this);
        initLogger();
        AndroidThreeTen.init(this);
    }

    public LsPushApplication get(Context context) {
        return (LsPushApplication) context.getApplicationContext();
    }

    public AppComponent appComponent() {
        if (appComponent == null) {
            BeeCrypto.init(this);
            LsPushConfig.init(this);
            //initHawk(this);
            NetworkUtils.init(this);
            appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
        }
        return appComponent;
    }

    private void initLogger() {
        Logger.init(UserScene.TAG_APP).methodOffset(5).methodCount(1);
        Timber.plant(new Timber.DebugTree() {
            @Override
            protected void log(int priority, String tag, String message, Throwable t) {
                Logger.log(priority, tag, message, t);
            }
        });
    }

    private void initHawk(final Context context) {
        Hawk.init(context).setEncryption(new BeeEncryption(context)).setLogInterceptor(new LogInterceptor() {
            @Override
            public void onLog(String message) {
                Log.d("Hawk", message);
            }
        }).build();
    }

    private void initializeStetho(final Context context) {
        /**
         * seeing http://www.littlerobots.nl/blog/stetho-for-android-debug-builds-only/
         * or https://github.com/facebook/stetho/blob/master/stetho-sample/src/debug/java/com/facebook/stetho/sample/SampleDebugApplication.java
         */
        Stetho.initializeWithDefaults(context);
    }
}
