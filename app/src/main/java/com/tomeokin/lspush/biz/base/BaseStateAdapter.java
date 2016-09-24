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

import android.support.annotation.CallSuper;

public abstract class BaseStateAdapter extends LifecycleListener {
    public static final int ACTIVE = 0;
    public static final int DISABLE = 1;
    public static final int WAITING = 2;
    public static final int INFO = 3;

    protected boolean mNotice = true;
    protected int mState = DISABLE;
    protected final int mRequestId;
    protected BaseStateCallback mCallback;

    public BaseStateAdapter(int requestId, BaseStateCallback callback) {
        mRequestId = requestId;
        mCallback = callback;
    }

    public BaseStateAdapter(int requestId, BaseStateCallback callback, int state) {
        mRequestId = requestId;
        mCallback = callback;
        mState = state;
    }

    @CallSuper
    @Override
    public void onResume() {
        sync();
    }

    @CallSuper
    @Override
    public void onDestroyView() {
        mCallback = null;
    }

    @CallSuper
    public void sync() {
        if (mState == WAITING || mState == INFO) {
            sync(mState);
        } else {
            final boolean isActive = mCallback.isActive(this, mRequestId);
            sync(isActive ? ACTIVE : DISABLE);
        }
    }

    /**
     * this method only call when you need to break waiting state conveniently.
     */
    @CallSuper
    public void syncRevokeWaiting() {
        if (mState == INFO) {
            sync(mState);
        } else {
            final boolean isActive = mCallback.isActive(this, mRequestId);
            sync(isActive ? ACTIVE : DISABLE);
        }
    }

    @CallSuper
    public void sync(int state) {
        // disable notice when sync
        mNotice = false;
        if (state == ACTIVE) {
            active();
        } else if (state == WAITING) {
            waiting();
        } else if (state == INFO) {
            info();
        } else {
            disable();
        }
        mNotice = true;
    }

    @CallSuper
    public void active() {
        updateState(ACTIVE, mNotice);
    }

    @CallSuper
    public void waiting() {
        updateState(WAITING, mNotice);
    }

    @CallSuper
    public void disable() {
        updateState(DISABLE, mNotice);
    }

    @CallSuper
    public void info() {
        updateState(INFO, mNotice);
    }

    @CallSuper
    public int getState() {
        return mState;
    }

    protected void updateState(int state, boolean callback) {
        if (mState != state) {
            mState = state;
            if (callback) {
                mCallback.onStateChange(this, mRequestId, mState);
            }
        }
    }
}
