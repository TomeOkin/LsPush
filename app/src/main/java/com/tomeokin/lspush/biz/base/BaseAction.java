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
package com.tomeokin.lspush.biz.base;

import android.content.res.Resources;
import android.support.annotation.CallSuper;

import retrofit2.Call;

public class BaseAction {
    protected final Resources mResource;
    protected BaseActionCallback mCallback;

    public BaseAction(Resources resources) {
        mResource = resources;
    }

    @CallSuper
    public void attach(BaseActionCallback callback) {
        mCallback = callback;
    }

    @CallSuper
    public void detach() {
        mCallback = null;
    }

    protected void checkAndCancel(Call<?> call) {
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }
    }

    public void cancel(int action) {}
}
