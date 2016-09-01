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
package com.tomeokin.lspush.biz.auth.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.base.LifecycleListener;

public class FieldAdapter extends LifecycleListener {
    public static final int ACTIVE = 0;
    public static final int DISABLE = 1;
    public static final int WAITING = 2;
    public static final int INFO = 3;

    private final int mRequestId;
    private FieldCallback mCallback;
    private int mState = DISABLE;

    private ImageView mValidButton;
    private ProgressBar mProgressBar;

    public FieldAdapter(int requestId, FieldCallback callback, ImageView validButton, ProgressBar progressBar) {
        mRequestId = requestId;
        mCallback = callback;
        mValidButton = validButton;
        mProgressBar = progressBar;
    }

    @Override public void onResume() {
        sync();
    }

    @Override public void onDestroyView() {
        mCallback = null;
        mValidButton = null;
        mProgressBar = null;
    }

    @Override public void onDestroy() {
        super.onDestroy();
    }

    public void sync() {
        sync(mCallback.checkState(this, mRequestId, mState));
    }

    public void sync(int state) {
        if (mState != state) {
            mState = state;
            mCallback.onStateChange(this, mRequestId, mState);
        }

        if (state == ACTIVE) {
            active();
        } else if (state == WAITING) {
            waiting();
        } else if (state == INFO) {
            info();
        } else {
            disable();
        }
    }

    public void active() {
        mState = ACTIVE;
        mValidButton.setBackgroundResource(R.drawable.validation_positive);
        mValidButton.setVisibility(View.VISIBLE);
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    public void waiting() {
        mState = WAITING;
        mValidButton.setVisibility(View.GONE);
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    public void disable() {
        mState = DISABLE;
        mValidButton.setBackgroundResource(R.drawable.validation_negative);
        mValidButton.setVisibility(View.VISIBLE);
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    public void info() {
        mState = INFO;
        mValidButton.setBackgroundResource(R.drawable.info);
        mValidButton.setVisibility(View.VISIBLE);
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    public int getState() {
        return mState;
    }
}
