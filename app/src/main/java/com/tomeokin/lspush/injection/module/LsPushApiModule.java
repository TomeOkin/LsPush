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
import android.util.Log;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tomeokin.lspush.BuildConfig;
import com.tomeokin.lspush.biz.common.UserScene;
import com.tomeokin.lspush.util.NetworkUtils;
import com.tomeokin.lspush.data.model.Image;
import com.tomeokin.lspush.data.remote.LsPushService;
import com.tomeokin.lspush.data.support.DateTypeConverter;
import com.tomeokin.lspush.data.support.GsonStrategy;
import com.tomeokin.lspush.data.support.ImageTypeConverter;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

@Module
public class LsPushApiModule {
    private static final String API_URL;
    private static final String CACHE_CONTROL = "Cache-Control";
    private static final String CACHE_DIR = "http-cache-lspush";

    static {
        API_URL = BuildConfig.LSPUSH_SERVER_URL;
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(final Application application) {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(provideHttpLoggingInterceptor());
            builder.addNetworkInterceptor(new StethoInterceptor());
        }

        builder.connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(provideOfflineCacheInterceptor())
            .addNetworkInterceptor(provideCacheInterceptor())
            .cache(provideCache(application));

        return builder.build();
    }

    private Cache provideCache(final Application app) {
        Cache cache = null;
        try {
            cache = new Cache(new File(app.getCacheDir(), CACHE_DIR), 10 * 1024 * 1024); // 10 MB
        } catch (Exception e) {
            Timber.e(e, "Could not create Cache!");
        }
        return cache;
    }

    private HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                // for better see, change to normal
                Log.d(UserScene.TAG_NETWORK, message);
            }
        });
        loggingInterceptor.setLevel(
            BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        return loggingInterceptor;
    }

    public Interceptor provideCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());

                // re-write response header to force use of cache
                CacheControl cacheControl = new CacheControl.Builder().maxAge(0, TimeUnit.SECONDS)
                    .onlyIfCached()
                    .maxStale(0, TimeUnit.SECONDS) // 清除离线时的配置
                    .build();

                return response.newBuilder().header(CACHE_CONTROL, cacheControl.toString()).build();
            }
        };
    }

    public Interceptor provideOfflineCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                if (NetworkUtils.isOffline()) {
                    Timber.w("no network available");
                    CacheControl cacheControl = new CacheControl.Builder().maxStale(7, TimeUnit.DAYS).build();

                    request = request.newBuilder().cacheControl(cacheControl).build();
                }

                return chain.proceed(request);
            }
        };
    }

    @Provides
    @Singleton
    public Gson provideGson() {
        // provide a base gson for image converter,
        // it is a little hack
        // (gson perhaps should provide a @GsonString or @GsonStringAdapter for bean detail transparent of server).
        Gson gson = new Gson();
        return new GsonBuilder().setExclusionStrategies(new GsonStrategy())
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .registerTypeAdapter(Image.class, new ImageTypeConverter(gson))
            .registerTypeAdapter(Date.class, new DateTypeConverter())
            .create();
    }

    @Provides
    @Singleton
    public LsPushService provideLsPushService(Gson gson, OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

        return retrofit.create(LsPushService.class);
    }
}
