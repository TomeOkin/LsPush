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
package com.tomeokin.lspush.injection.module;

import android.app.Application;
import android.content.Context;

import com.evernote.android.job.JobManager;
import com.google.gson.Gson;
import com.tomeokin.lspush.biz.home.CollectionHolder;
import com.tomeokin.lspush.biz.job.LsPushJobCreator;
import com.tomeokin.lspush.biz.usercase.user.LsPushUserState;
import com.tomeokin.lspush.injection.qualifier.AppContext;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private final Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application application() {
        return application;
    }

    @Provides
    @Singleton
    @AppContext
    Context provideAppContext() {
        return application;
    }

    @Provides
    @Singleton
    LsPushUserState provideLsPushUserState(Gson gson) {
        return new LsPushUserState(gson);
    }

    @Provides
    @Singleton
    JobManager provideJobManager(@AppContext Context context) {
        JobManager.create(context).addJobCreator(new LsPushJobCreator(context));
        return JobManager.instance();
    }

    @Provides
    @Singleton
    CollectionHolder provideCollectionHolder() {
        return new CollectionHolder();
    }
}
