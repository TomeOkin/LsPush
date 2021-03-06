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
package com.tomeokin.lspush.biz.base.support;

import android.content.res.Resources;

import retrofit2.Call;
import rx.Subscription;

public class BaseAction extends CommonAction<BaseActionCallback> {
    protected final Resources mResource;

    public BaseAction(Resources resources) {
        mResource = resources;
    }

    protected void checkAndCancel(Call<?> call) {
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }
    }

    protected void checkAndUnsubscribe(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    public void cancel(int action) {}
}
