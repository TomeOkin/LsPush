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

import com.tomeokin.lspush.common.NetworkUtils;
import com.tomeokin.lspush.common.SMSCaptchaUtils;
import com.tomeokin.lspush.data.crypt.Crypto;
import com.tomeokin.lspush.data.local.LsPushConfig;
import com.tomeokin.lspush.injection.component.AppComponent;
import com.tomeokin.lspush.injection.component.DaggerAppComponent;
import com.tomeokin.lspush.injection.module.AppModule;

import timber.log.Timber;

public class LsPushApplication extends Application {
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        LsPushConfig.init(this);
        Crypto.init(LsPushConfig.getPublicKey());
        NetworkUtils.init(this);
        SMSCaptchaUtils.init(this, LsPushConfig.getMobSMSId(), LsPushConfig.getMobSMSKey());
        initAppComponent();
        initLogger(this);
    }

    private void initAppComponent() {
        appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    }

    private void initLogger(final Context context) {
        Timber.plant(new CrashReportingTree(context));
    }

    public AppComponent appComponent() {
        return appComponent;
    }
}
