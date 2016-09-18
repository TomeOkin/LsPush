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

import com.tomeokin.lspush.biz.auth.CaptchaView;
import com.tomeokin.lspush.biz.base.LifecycleListener;

public final class CaptchaViewHolder extends LifecycleListener {
    private final CaptchaView mCaptchaView;
    public final View mEmailTab;
    public final View mPhoneTab;
    private boolean mShowingEmailTab;
    private final FieldSwitchAdapter mEmailFieldSwitchAdapter;
    private final FieldSwitchAdapter mPhoneFieldSwitchAdapter;

    public CaptchaViewHolder(CaptchaView captchaView, View emailTab, View phoneTab, boolean showingEmailTab,
        FieldSwitchAdapter emailFieldSwitchAdapter, FieldSwitchAdapter phoneFieldSwitchAdapter) {
        mCaptchaView = captchaView;
        mEmailTab = emailTab;
        mPhoneTab = phoneTab;
        mShowingEmailTab = showingEmailTab;
        mEmailFieldSwitchAdapter = emailFieldSwitchAdapter;
        mPhoneFieldSwitchAdapter = phoneFieldSwitchAdapter;
    }

    @Override
    public void onCreateView(View view) {
        mEmailTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchShowEmail(true);
            }
        });
        mPhoneTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchShowEmail(false);
            }
        });
        switchShowEmail(mShowingEmailTab);
    }

    public final void switchShowEmail(boolean flag) {
        mShowingEmailTab = flag;
        mCaptchaView.onTabSelectUpdate(mShowingEmailTab);
        mEmailFieldSwitchAdapter.toggle(flag);
        mPhoneFieldSwitchAdapter.toggle(!flag);
    }
}
