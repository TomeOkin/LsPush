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
package com.tomeokin.lspush.biz.job.sync;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.evernote.android.job.JobManager;
import com.tomeokin.lspush.LsPushApplication;
import com.tomeokin.lspush.biz.usercase.auth.LoginAction;
import com.tomeokin.lspush.biz.usercase.sync.RefreshTokenAction;
import com.tomeokin.lspush.biz.usercase.user.LocalUserInfoAction;
import com.tomeokin.lspush.common.NetworkUtils;
import com.tomeokin.lspush.data.model.AccessResponse;
import com.tomeokin.lspush.data.model.LoginData;
import com.tomeokin.lspush.injection.component.AppComponent;
import com.tomeokin.lspush.injection.component.DaggerSyncComponent;
import com.tomeokin.lspush.injection.component.SyncComponent;
import com.tomeokin.lspush.injection.module.AuthModule;
import com.tomeokin.lspush.injection.module.ServiceModule;
import com.tomeokin.lspush.injection.module.SyncModule;

import org.threeten.bp.Duration;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.temporal.ChronoUnit;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class SyncService extends Service {
    private static final long EXPIRE_TIME = 24 * 60; // minutes
    private static final long ALARM_TIME = EXPIRE_TIME / 5;
    private static final long SHOULD_REFRESH = EXPIRE_TIME / 5 * 3;
    private static final long TIME_UNIT_MINUTE = 60 * 1000;
    private static final long NEXT_JOB_START = EXPIRE_TIME / 5 * 4 * TIME_UNIT_MINUTE;
    private static final long NEXT_JOB_FLEX = AlarmManager.INTERVAL_HALF_HOUR;
    private static final Duration SHOULD_EXPIRE_DURATION = Duration.of(SHOULD_REFRESH, ChronoUnit.MINUTES);

    private static final int UPDATE_BY_PASSWORD = 0;
    private static final int UPDATE_BY_REFRESH_DATA = 1;
    private static final int UPDATE_BY_REFRESH_TOKEN = 2;

    private static final String EXTRA_JOB_ID = "extra.sync.job.id";
    private SyncComponent mComponent;
    private int mJobId = -1;

    @Inject LocalUserInfoAction mLocalUserInfoAction;
    @Inject Lazy<RefreshTokenAction> mRefreshTokenAction;
    @Inject Lazy<LoginAction> mLoginAction;
    @Inject JobManager mJobManager;

    public static Intent newIntent(Context context, int jobId) {
        Intent intent = new Intent(context, SyncService.class);
        intent.putExtra(EXTRA_JOB_ID, jobId);
        return intent;
    }

    public static void start(Context context) {
        final Intent intent = new Intent(context, SyncService.class);
        final PendingIntent pi = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
        if (pi != null) { // service is active
            return;
        }

        context.startService(intent);
    }

    protected AppComponent getAppComponent() {
        return LsPushApplication.get(this).appComponent();
    }

    public SyncComponent component() {
        if (mComponent == null) {
            mComponent = DaggerSyncComponent.builder()
                .appComponent(getAppComponent())
                .serviceModule(new ServiceModule(this))
                .syncModule(new SyncModule())
                .authModule(new AuthModule())
                .build();
        }
        return mComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        component().inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mJobId = intent == null ? -1 : intent.getIntExtra(EXTRA_JOB_ID, -1);
        Timber.v("onBind extra_job_id is %d", mJobId);
        return new SyncBinder();
    }

    public void sync(@Nullable final Callback callback) {
        NetworkUtils.init(this);
        if (NetworkUtils.isOffline()) {
            if (callback != null) {
                callback.onFailure(null);
            }
            return;
        }

        mLocalUserInfoAction.getAccessResponseObservable()
            .subscribeOn(Schedulers.io())
            .concatMap(new Func1<AccessResponse, Observable<TokenInfo>>() {
                @Override
                public Observable<TokenInfo> call(AccessResponse accessResponse) {
                    return refreshObservable(accessResponse, callback);
                }
            })
            .concatMap(new Func1<TokenInfo, Observable<AccessResponse>>() {
                @Override
                public Observable<AccessResponse> call(TokenInfo tokenInfo) {
                    AccessResponse old = tokenInfo.old;
                    if (tokenInfo.update == UPDATE_BY_PASSWORD) {
                        return updateByPasswordObservable(old);
                    } else if (tokenInfo.update == UPDATE_BY_REFRESH_DATA) {
                        return updateRefreshTokenObservable(old);
                    } else if (tokenInfo.update == UPDATE_BY_REFRESH_TOKEN) {
                        return updateExpireTokenObservable(old);
                    }
                    return null;
                }
            })
            .concatMap(new Func1<AccessResponse, Observable<AccessResponse>>() {
                @Override
                public Observable<AccessResponse> call(AccessResponse accessResponse) {
                    return mLocalUserInfoAction.updateAccessResponseObservable(accessResponse);
                }
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<AccessResponse>() {
                @Override
                public void onCompleted() {
                    Timber.v("onCompleted");
                    if (callback != null) {
                        callback.onSuccess();
                    }
                    stopSelf();
                }

                @Override
                public void onError(Throwable e) {
                    Timber.v("onError");
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                    stopSelf();
                }

                @Override
                public void onNext(AccessResponse accessResponse) {
                    Timber.v("onNext");
                    LocalDateTime now = LocalDateTime.now();
                    Instant instantExpire = Instant.ofEpochSecond(accessResponse.getExpireTime());
                    LocalDateTime targetExpire = LocalDateTime.ofInstant(instantExpire, ZoneId.systemDefault());

                    Duration duration = Duration.between(now, targetExpire);
                    scheduleJob(duration);
                    if (callback != null) {
                        callback.onSuccess();
                    }
                    stopSelf();
                }
            });
    }

    private Observable<TokenInfo> refreshObservable(final AccessResponse old, final Callback callback) {
        return Observable.create(new Observable.OnSubscribe<TokenInfo>() {
            @Override
            public void call(Subscriber<? super TokenInfo> subscriber) {
                if (old == null) { // not need refresh
                    Timber.i("");
                    subscriber.onCompleted();
                    return;
                }

                LocalDateTime now = LocalDateTime.now();
                Instant instantExpire = Instant.ofEpochSecond(old.getExpireTime());
                LocalDateTime targetExpire = LocalDateTime.ofInstant(instantExpire, ZoneId.systemDefault());

                Duration duration = Duration.between(now, targetExpire);
                Duration diff = duration.minus(SHOULD_EXPIRE_DURATION);
                if (!diff.isNegative()) { // no need refresh
                    scheduleJob(duration);
                    Timber.i("No Need refresh, active");
                    subscriber.onCompleted();
                    return;
                }

                if (now.isAfter(targetExpire)) {
                    if (shouldUpdateByPassword(now, old.getRefreshTime())) {
                        // need refresh by password
                        Timber.v("need refresh by password");
                        subscriber.onNext(new TokenInfo(old, UPDATE_BY_PASSWORD));
                    } else {
                        subscriber.onNext(new TokenInfo(old, UPDATE_BY_REFRESH_DATA));
                        // need refresh by refreshData
                        Timber.v("need refresh by refreshData");
                    }
                } else {
                    subscriber.onNext(new TokenInfo(old, UPDATE_BY_REFRESH_TOKEN));
                    // need refresh by refresh token
                    Timber.v("need refresh by refresh token");
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).doOnCompleted(new Action0() {
            @Override
            public void call() {
                callback.onSuccess();
            }
        }).asObservable();
    }

    private void scheduleJob(Duration duration) {
        if (duration.toMinutes() >= SHOULD_REFRESH) {
            SyncJob.start(mJobManager, NEXT_JOB_START, NEXT_JOB_START + NEXT_JOB_FLEX);
        } else {
            Timber.w("call scheduleJob only when deadline is full");
        }
    }

    private boolean shouldUpdateByPassword(LocalDateTime now, long refreshTime) {
        Instant instantRefresh = Instant.ofEpochSecond(refreshTime);
        LocalDateTime targetRefresh = LocalDateTime.ofInstant(instantRefresh, ZoneId.systemDefault());
        return now.isAfter(targetRefresh);
    }

    private Observable<AccessResponse> updateExpireTokenObservable(final AccessResponse old) {
        return mRefreshTokenAction.get()
            .refreshExpireToken(old.getRefreshToken())
            .subscribeOn(Schedulers.io())
            .map(new Func1<AccessResponse, AccessResponse>() {
                @Override
                public AccessResponse call(AccessResponse accessResponse) {
                    old.setExpireTime(accessResponse.getExpireTime());
                    old.setExpireToken(accessResponse.getExpireToken());
                    return old;
                }
            })
            .asObservable();
    }

    private Observable<AccessResponse> updateRefreshTokenObservable(final AccessResponse old) {
        return mRefreshTokenAction.get()
            .refreshRefreshToken(old)
            .subscribeOn(Schedulers.io())
            .map(new Func1<AccessResponse, AccessResponse>() {
                @Override
                public AccessResponse call(AccessResponse accessResponse) {
                    old.setExpireTime(accessResponse.getExpireTime());
                    old.setExpireToken(accessResponse.getExpireToken());
                    old.setRefreshTime(accessResponse.getRefreshTime());
                    old.setRefreshToken(accessResponse.getRefreshToken());
                    return old;
                }
            })
            .asObservable();
    }

    private Observable<AccessResponse> updateByPasswordObservable(final AccessResponse old) {
        LoginData loginData = new LoginData();
        loginData.setUid(old.getUser().getUid());
        loginData.setPassword(old.getUser().getPassword());
        return mLoginAction.get()
            .loginObservable(loginData)
            .subscribeOn(Schedulers.io())
            .map(new Func1<AccessResponse, AccessResponse>() {
                @Override
                public AccessResponse call(AccessResponse accessResponse) {
                    old.setExpireTime(accessResponse.getExpireTime());
                    old.setExpireToken(accessResponse.getExpireToken());
                    old.setRefreshTime(accessResponse.getRefreshTime());
                    old.setRefreshToken(accessResponse.getRefreshToken());
                    return accessResponse;
                }
            })
            .asObservable();
    }

    private class TokenInfo {
        public AccessResponse old;
        public int update;

        public TokenInfo(AccessResponse old, int update) {
            this.old = old;
            this.update = update;
        }
    }

    public interface Callback {
        void onSuccess();

        void onFailure(Throwable t);
    }

    public class SyncBinder extends Binder {
        public SyncService getService() {
            return SyncService.this;
        }
    }
}
