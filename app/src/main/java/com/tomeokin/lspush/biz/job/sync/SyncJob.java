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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import java.util.Set;
import java.util.concurrent.CountDownLatch;

import timber.log.Timber;

public class SyncJob extends Job {
    public static final String TAG = "SyncJob";
    private Context mContext;

    public SyncJob(Context context) {
        mContext = context.getApplicationContext();
    }

    ///**
    // * @param jobManager only want to emphasize that it depend on jobManager
    // */
    //@SuppressWarnings("UnusedParameters")
    //public static int start(JobManager jobManager, long interval, long flex) {
    //    Timber.v("call start sync job");
    //    return new JobRequest.Builder(TAG).setPeriodic(interval, flex)
    //        .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
    //        .build()
    //        .schedule();
    //}

    /**
     * @param jobManager only want to emphasize that it depend on jobManager
     * @param start ms, the execution window start
     * @param end ms, the execution window end
     */
    public static int start(JobManager jobManager, long start, long end) {
        Set<JobRequest> jobRequests = jobManager.getAllJobRequestsForTag(TAG);
        if (jobRequests != null && jobRequests.size() != 0) {
            Timber.v("Already has a %s apply.", TAG);
            return 0;
        }
        Timber.v("Apply a new %s job", TAG);
        return new JobRequest.Builder(TAG).setExecutionWindow(start, end)
            .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
            .build()
            .schedule();
    }

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ServiceConnection serviceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                Timber.v("sync service connected");

                SyncService.SyncBinder binder = (SyncService.SyncBinder) service;
                binder.getService().sync(new SyncService.Callback() {
                    @Override
                    public void onSuccess() {
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        countDownLatch.countDown();
                    }
                });
            }

            public void onServiceDisconnected(ComponentName className) {
                Timber.v("sync service disconnected");
                countDownLatch.countDown();
            }
        };

        SyncService.start(mContext);
        final Intent intent = SyncService.newIntent(mContext, params.getId());
        mContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        try {
            countDownLatch.await();
        } catch (InterruptedException ignored) {
        }
        mContext.unbindService(serviceConnection);
        Timber.v("job existed");
        return Result.SUCCESS;
    }
}
