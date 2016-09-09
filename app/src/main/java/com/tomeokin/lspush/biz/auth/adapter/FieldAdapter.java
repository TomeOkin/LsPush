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
import com.tomeokin.lspush.biz.base.BaseStateAdapter;
import com.tomeokin.lspush.biz.base.BaseStateCallback;

public class FieldAdapter extends BaseStateAdapter {
    private ImageView mValidButton;
    private ProgressBar mProgressBar;

    public FieldAdapter(int requestId, BaseStateCallback callback, ImageView validButton, ProgressBar progressBar) {
        super(requestId, callback);
        mValidButton = validButton;
        mProgressBar = progressBar;
    }

    public FieldAdapter(int requestId, BaseStateCallback callback, int state, ImageView validButton,
        ProgressBar progressBar) {
        super(requestId, callback, state);
        mValidButton = validButton;
        mProgressBar = progressBar;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        mValidButton = null;
        mProgressBar = null;
    }

    @Override
    public void active() {
        super.active();
        mValidButton.setBackgroundResource(R.drawable.validation_positive);
        mValidButton.setVisibility(View.VISIBLE);
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void waiting() {
        super.waiting();
        mValidButton.setVisibility(View.GONE);
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void disable() {
        super.disable();
        mValidButton.setBackgroundResource(R.drawable.validation_negative);
        mValidButton.setVisibility(View.VISIBLE);
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void info() {
        super.info();
        mValidButton.setBackgroundResource(R.drawable.info);
        mValidButton.setVisibility(View.VISIBLE);
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
