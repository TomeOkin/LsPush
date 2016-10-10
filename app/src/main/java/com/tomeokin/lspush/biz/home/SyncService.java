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
package com.tomeokin.lspush.biz.home;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.tomeokin.lspush.LsPushApplication;
import com.tomeokin.lspush.biz.usercase.user.LocalUserInfoAction;
import com.tomeokin.lspush.common.NetworkUtils;
import com.tomeokin.lspush.data.model.AccessResponse;
import com.tomeokin.lspush.injection.component.AppComponent;
import com.tomeokin.lspush.injection.component.DaggerSyncComponent;
import com.tomeokin.lspush.injection.component.SyncComponent;
import com.tomeokin.lspush.injection.module.SyncModule;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import javax.inject.Inject;

public class SyncService extends IntentService {
    private static final String TAG = "SyncService";
    private static final long SYNC_INTERVAL = AlarmManager.INTERVAL_HOUR * 3;
    private SyncComponent mComponent;
    @Inject LocalUserInfoAction mLocalUserInfoAction;

    public static Intent newIntent(Context context) {
        return new Intent(context, SyncService.class);
    }

    public static boolean isServiceActive(Context context) {
        final Intent i = newIntent(context);
        final PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    // TODO: 2016/10/10 change AlarmManager with android-job
    public static void startAlarmService(Context context) {
        if (isServiceActive(context)) {
            return;
        }

        final Intent i = newIntent(context);
        final PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), SYNC_INTERVAL,
            pi);
    }

    public static void stopAlarmService(Context context) {
        final Intent i = newIntent(context);
        final PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pi);
        pi.cancel();
    }

    protected AppComponent getAppComponent() {
        return ((LsPushApplication) getApplication()).appComponent();
    }

    public SyncComponent component() {
        if (mComponent == null) {
            mComponent = DaggerSyncComponent.builder()
                .appComponent(getAppComponent())
                .syncModule(new SyncModule())
                .build();
        }
        return mComponent;
    }

    public SyncService() {
        super(TAG);
        component().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        NetworkUtils.init(this);
        if (NetworkUtils.isOffline()) {
            return;
        }

        AccessResponse accessResponse = mLocalUserInfoAction.getAccessResponseSync();
        if (accessResponse == null) { // 用户未登录，无法进行同步，直接退出
            stopAlarmService(this);
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        Instant instantExpire = Instant.ofEpochSecond(accessResponse.getExpireTime());
        LocalDateTime targetExpire = LocalDateTime.ofInstant(instantExpire, ZoneId.systemDefault());
        if (now.isAfter(targetExpire)) {
            Instant instantRefresh = Instant.ofEpochSecond(accessResponse.getExpireTime());
            LocalDateTime targetRefresh = LocalDateTime.ofInstant(instantRefresh, ZoneId.systemDefault());
            if (now.isAfter(targetRefresh)) {
                // need refresh by password
            } else {
                // need refresh by refresh token
            }
        }
    }
}
