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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.auth.CaptchaView;
import com.tomeokin.lspush.biz.base.BaseTextWatcher;
import com.tomeokin.lspush.biz.base.LifecycleListener;
import com.tomeokin.lspush.common.SoftInputUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class EmailFieldViewHolder extends LifecycleListener {
    public final AutoCompleteTextView mEmailField;
    private final TextWatcher mEmptyWatcher;
    private final TextWatcher mValidWatcher;
    public final ImageView mClearButton;

    private final TextView mNextButton;
    private final CaptchaView mCaptchaView;

    private final Context mContext;
    private final NextButtonAdapter mNextButtonAdapter;

    public EmailFieldViewHolder(Context context, AutoCompleteTextView emailField, ImageView clearButton,
        TextView nextButton, CaptchaView captchaView, NextButtonAdapter emailFieldStateAdapter) {
        mEmailField = emailField;
        mEmptyWatcher = new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                mClearButton.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }
        };
        mValidWatcher = new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (mCaptchaView.isFieldValid()) {
                    mNextButtonAdapter.active();
                } else {
                    mNextButtonAdapter.disable();
                }
            }
        };

        mClearButton = clearButton;
        mNextButton = nextButton;
        mCaptchaView = captchaView;
        mContext = context;
        mNextButtonAdapter = emailFieldStateAdapter;
    }

    @Override
    public void onCreateView(View view) {
        List<String> emails = mCaptchaView.getHistoryUserEmails();
        String[] emailHint = mContext.getResources().getStringArray(R.array.email_postfix);
        List<String> emailHintList = Arrays.asList(emailHint);
        if (emails != null && !emails.isEmpty()) {
            if (mEmailField.length() == 0) {
                mEmailField.setText(emails.get(0));
            }
            mEmailField.setAdapter(new AutoCompleteEmailAdapter(mContext, emails, emailHintList));
            mEmailField.dismissDropDown();
        } else {
            mEmailField.setAdapter(
                new AutoCompleteEmailAdapter(mContext, Collections.<String>emptyList(), emailHintList));
        }

        mEmailField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT && mCaptchaView.isFieldValid()) {
                    onSendCaptcha();
                    return true;
                }
                return false;
            }
        });

        mClearButton.setVisibility(mEmailField.length() > 0 ? View.VISIBLE : View.GONE);
        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmailField.setText("");
                mEmailField.requestFocus();
            }
        });
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendCaptcha();
            }
        });
    }

    public void onSendCaptcha() {
        mNextButtonAdapter.waiting();
        mCaptchaView.sendCaptcha();
    }

    @Override
    public void onResume() {
        mEmailField.addTextChangedListener(mEmptyWatcher);
        mEmailField.addTextChangedListener(mValidWatcher);
        if (mCaptchaView.isFieldValid()) {
            mEmailField.dismissDropDown();
        }
    }

    @Override
    public void onPause() {
        SoftInputUtils.hideInput(mEmailField);
        mEmailField.removeTextChangedListener(mEmptyWatcher);
        mEmailField.removeTextChangedListener(mValidWatcher);
    }
}
