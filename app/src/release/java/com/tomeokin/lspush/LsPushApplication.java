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

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.orhanobut.hawk.Hawk;
import com.tomeokin.lspush.util.NetworkUtils;
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
        initLogger(this);
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

    private void initHawk(final Context context) {
        Hawk.init(context).setEncryption(new BeeEncryption(context)).build();
    }

    private void initLogger(final Context context) {
        Timber.plant(new CrashReportingTree(context));
    }
}
