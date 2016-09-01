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

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.base.LifecycleListener;

public final class NextButtonAdapter extends LifecycleListener {
    public static final int ACTIVE = 0;
    public static final int DISABLE = 1;
    public static final int WAITING = 2;

    private final int mRequestId;
    private int mState = DISABLE;

    private NextButtonCallback mCallback;

    TextView mNextButton;
    private ProgressBar mProgressBar;
    private Context mContext;

    private String mNextText;

    public NextButtonAdapter(int requestId, NextButtonCallback callback, TextView nextButton,
        ProgressBar progressBar) {
        mRequestId = requestId;
        mCallback = callback;
        mNextButton = nextButton;
        mProgressBar = progressBar;
        mContext = callback.getContext();
        mNextText = mContext.getString(R.string.next);
    }

    @Override public void onResume() {
        sync();
    }

    @Override public void onDestroyView() {
        mCallback = null;
        mNextButton = null;
        mProgressBar = null;
    }

    @Override public void onDestroy() {
        super.onDestroy();
        mContext = null;
    }

    public void setNextText(String nextText) {
        mNextText = nextText;
        sync();
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
        } else {
            disable();
        }
    }

    public void active() {
        mState = ACTIVE;
        mNextButton.setText(mNextText);
        mNextButton.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        mNextButton.setEnabled(true);
        mProgressBar.setVisibility(View.GONE);
    }

    public void waiting() {
        mState = WAITING;
        mNextButton.setText("");
        mNextButton.setTextColor(ContextCompat.getColor(mContext, R.color.white_20_transparent));
        mNextButton.setEnabled(false);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void disable() {
        mState = DISABLE;
        mNextButton.setText(mNextText);
        mNextButton.setTextColor(ContextCompat.getColor(mContext, R.color.white_20_transparent));
        mNextButton.setEnabled(false);
        mProgressBar.setVisibility(View.GONE);
    }
}
