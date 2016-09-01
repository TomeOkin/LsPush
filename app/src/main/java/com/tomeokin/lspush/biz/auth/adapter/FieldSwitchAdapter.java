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
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.common.SoftInputUtils;

public final class FieldSwitchAdapter {
    private final TextView mTextField;
    private final View mTabContent;
    private final View mTabSelection;
    private final FrameLayout mNextLayout;
    private final TextView mTabText;
    private final View mTab;
    private final NextButtonAdapter mNextButtonAdapter;

    public FieldSwitchAdapter(View tabContent, View tabSelection, FrameLayout nextLayout, TextView textField,
        TextView tabText, View tab, NextButtonAdapter adapter) {
        mTabContent = tabContent;
        mTabSelection = tabSelection;
        mNextLayout = nextLayout;
        mTextField = textField;
        mTabText = tabText;
        mTab = tab;
        mNextButtonAdapter = adapter;
    }

    public final void toggle(boolean show) {
        Context context = mTabText.getContext();
        mTabContent.setVisibility(show ? View.VISIBLE : View.GONE);
        mTabSelection.setBackgroundResource(show ? R.color.white : R.color.white_20_transparent);
        mTabText.setTextColor(ContextCompat.getColor(context, show ? R.color.white : R.color.white_50_transparent));
        mNextLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        mTab.setEnabled(!show);
        if (show) {
            if (TextUtils.isEmpty(mTextField.getText().toString())) {
                mTextField.requestFocus();
                SoftInputUtils.showInput(mTextField);
                return;
            } else {
                SoftInputUtils.hideInput(mTextField);
            }

            mNextButtonAdapter.sync();
        }
    }
}
