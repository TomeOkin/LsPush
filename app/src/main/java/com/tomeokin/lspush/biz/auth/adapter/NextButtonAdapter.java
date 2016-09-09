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
import com.tomeokin.lspush.biz.base.BaseStateAdapter;
import com.tomeokin.lspush.biz.base.BaseStateCallback;

public final class NextButtonAdapter extends BaseStateAdapter {
    TextView mNextButton;
    private ProgressBar mProgressBar;
    private Context mContext;
    private String mNextText;

    public NextButtonAdapter(int requestId, BaseStateCallback callback, Context context, TextView nextButton,
        ProgressBar progressBar) {
        super(requestId, callback);
        mNextButton = nextButton;
        mProgressBar = progressBar;
        mContext = context;
        mNextText = mContext.getString(R.string.next);
    }

    public NextButtonAdapter(int requestId, BaseStateCallback callback, Context context, int state, TextView nextButton,
        ProgressBar progressBar) {
        super(requestId, callback, state);
        mNextButton = nextButton;
        mProgressBar = progressBar;
        mContext = context;
        mNextText = context.getString(R.string.next);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mNextButton = null;
        mProgressBar = null;
    }

    @Override
    public void onDestroy() {
        mContext = null;
    }

    public void setNextText(String nextText) {
        mNextText = nextText;
        sync();
    }

    @Override
    public void active() {
        super.active();
        mNextButton.setText(mNextText);
        mNextButton.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        mNextButton.setEnabled(true);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void waiting() {
        super.waiting();
        mNextButton.setText("");
        mNextButton.setTextColor(ContextCompat.getColor(mContext, R.color.white_20_transparent));
        mNextButton.setEnabled(false);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void disable() {
        super.disable();
        mNextButton.setText(mNextText);
        mNextButton.setTextColor(ContextCompat.getColor(mContext, R.color.white_20_transparent));
        mNextButton.setEnabled(false);
        mProgressBar.setVisibility(View.GONE);
    }
}
