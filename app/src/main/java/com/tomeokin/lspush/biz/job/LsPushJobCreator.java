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
package com.tomeokin.lspush.biz.job;

import android.content.Context;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.tomeokin.lspush.biz.job.sync.SyncJob;

import timber.log.Timber;

public class LsPushJobCreator implements JobCreator {
    private Context mContext;

    public LsPushJobCreator(Context context) {
        mContext = context;
        Timber.i("add job creator");
    }

    @Override
    public Job create(String tag) {
        Timber.i("start job");
        if (SyncJob.TAG.equals(tag)) {
            return new SyncJob(mContext);
        }
        return null;
    }
}
