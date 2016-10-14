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
import com.tomeokin.lspush.biz.usercase.RefreshTokenAction;
import com.tomeokin.lspush.biz.usercase.auth.LoginAction;
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
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class SyncService extends Service {
    private static final long EXPIRE_TIME = 24; // hours
    private static final long ALARM_TIME = EXPIRE_TIME / 5;
    private static final long SHOULD_REFRESH = EXPIRE_TIME / 5 * 3;
    private static final Duration SHOULD_EXPIRE_DURATION = Duration.of(SHOULD_REFRESH, ChronoUnit.HOURS);

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
        Timber.d("onBind extra_job_id is %d", mJobId);
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

        Timber.d("getAccessResponseObservable start");
        mLocalUserInfoAction.getAccessResponseObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError(new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    Timber.d("doOnError");
                    if (callback != null) {
                        callback.onFailure(throwable);
                    }
                }
            })
            .observeOn(Schedulers.io())
            .doOnNext(new Action1<AccessResponse>() {
                @Override
                public void call(AccessResponse accessResponse) {
                    Timber.d("doOnNext");
                    if (accessResponse != null) {
                        refresh(accessResponse, callback);
                    } else {
                        if (callback != null) {
                            callback.onFailure(null);
                        }
                    }
                }
            })
            .subscribe();
        Timber.d("getAccessResponseObservable end");
    }

    private void refresh(final AccessResponse old, final Callback callback) {
        Timber.d("refresh function");
        LocalDateTime now = LocalDateTime.now();
        Instant instantExpire = Instant.ofEpochSecond(old.getExpireTime());
        LocalDateTime targetExpire = LocalDateTime.ofInstant(instantExpire, ZoneId.systemDefault());

        Duration duration = Duration.between(now, targetExpire);
        Duration diff = duration.minus(SHOULD_EXPIRE_DURATION);
        if (!diff.isNegative()) { // no need refresh
            // 检查定时器或者调整定时器
            scheduleJob(callback);
        } else {
            if (now.isAfter(targetExpire)) {
                if (shouldUpdateByPassword(now, old.getRefreshTime())) {
                    // need refresh by password
                    Timber.d("need refresh by password");
                    updateByPassword(old, callback);
                } else {
                    // need refresh by refreshData
                    Timber.d("need refresh by refreshData");
                    updateRefreshToken(old, callback);
                }
            } else {
                // need refresh by refresh token
                Timber.d("need refresh by refresh token");
                updateExpireToken(old, callback);
            }
        }
    }

    private boolean shouldUpdateByPassword(LocalDateTime now, long refreshTime) {
        Instant instantRefresh = Instant.ofEpochSecond(refreshTime);
        LocalDateTime targetRefresh = LocalDateTime.ofInstant(instantRefresh, ZoneId.systemDefault());
        return now.isAfter(targetRefresh);
    }

    private void updateExpireToken(final AccessResponse old, final Callback callback) {
        mRefreshTokenAction.get()
            .refreshExpireToken(old.getRefreshToken())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError(new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    Timber.d("doOnError");
                    callback.onFailure(throwable);
                }
            })
            .observeOn(Schedulers.io())
            .doOnNext(new Action1<AccessResponse>() {
                @Override
                public void call(AccessResponse accessResponse) {
                    Timber.d("doOnNext");
                    old.setExpireTime(accessResponse.getExpireTime());
                    old.setExpireToken(accessResponse.getExpireToken());
                    updateAccessResponse(old, callback);
                }
            })
            .subscribe();
    }

    private void updateRefreshToken(final AccessResponse old, final Callback callback) {
        mRefreshTokenAction.get()
            .refreshRefreshToken(old)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError(new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    Timber.d("doOnError");
                    callback.onFailure(throwable);
                }
            })
            .observeOn(Schedulers.io())
            .doOnNext(new Action1<AccessResponse>() {
                @Override
                public void call(AccessResponse accessResponse) {
                    Timber.d("doOnNext");
                    old.setExpireTime(accessResponse.getExpireTime());
                    old.setExpireToken(accessResponse.getExpireToken());
                    old.setRefreshTime(accessResponse.getRefreshTime());
                    old.setRefreshToken(accessResponse.getRefreshToken());
                    updateAccessResponse(old, callback);
                }
            })
            .subscribe();
    }

    public void updateByPassword(final AccessResponse old, final Callback callback) {
        LoginData loginData = new LoginData();
        loginData.setUid(old.getUser().getUid());
        loginData.setPassword(old.getUser().getPassword());
        mLoginAction.get()
            .loginObservable(loginData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError(new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    Timber.d("doOnError");
                    callback.onFailure(throwable);
                }
            })
            .observeOn(Schedulers.io())
            .doOnNext(new Action1<AccessResponse>() {
                @Override
                public void call(AccessResponse accessResponse) {
                    Timber.d("doOnNext");
                    old.setExpireTime(accessResponse.getExpireTime());
                    old.setExpireToken(accessResponse.getExpireToken());
                    old.setRefreshTime(accessResponse.getRefreshTime());
                    old.setRefreshToken(accessResponse.getRefreshToken());
                    updateAccessResponse(old, callback);
                }
            })
            .subscribe();
    }

    private void updateAccessResponse(final AccessResponse accessResponse, final Callback callback) {
        mLocalUserInfoAction.updateAccessResponseObservable(accessResponse)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError(new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    Timber.d("doOnError");
                    callback.onFailure(throwable);
                }
            })
            .observeOn(Schedulers.io())
            .doOnNext(new Action1<AccessResponse>() {
                @Override
                public void call(AccessResponse accessResponse) {
                    Timber.d("doOnNext");
                    // 检查定时器或者调整定时器
                    scheduleJob(callback);
                }
            })
            .subscribe();
    }

    private void scheduleJob(final Callback callback) {
        Observable.create(new Observable.OnSubscribe<AccessResponse>() {
            @Override
            public void call(Subscriber<? super AccessResponse> subscriber) {
                if (mJobId == -1) { // 不是由 JobManager 唤起
                    long interval = ALARM_TIME * AlarmManager.INTERVAL_HOUR;
                    long flex = 2 * AlarmManager.INTERVAL_FIFTEEN_MINUTES;
                    SyncJob.start(mJobManager, interval, flex);
                }
                stopSelf();
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnCompleted(new Action0() {
            @Override
            public void call() {
                callback.onSuccess();
            }
        }).subscribe();
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
