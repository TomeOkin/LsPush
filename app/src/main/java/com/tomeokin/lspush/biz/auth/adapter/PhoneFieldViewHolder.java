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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.tomeokin.lspush.biz.auth.CaptchaView;
import com.tomeokin.lspush.biz.auth.CountryCodePickerDialog;
import com.tomeokin.lspush.ui.widget.listener.TextWatcherAdapter;
import com.tomeokin.lspush.biz.base.lifecycle.LifecycleListener;
import com.tomeokin.lspush.util.SoftInputUtils;
import com.tomeokin.lspush.data.model.CountryCodeData;
import com.tomeokin.lspush.ui.widget.SearchEditText;

public final class PhoneFieldViewHolder extends LifecycleListener {
    public final SearchEditText mPhoneField;
    private final TextWatcher mValidWatcher;
    private PhoneNumberFormattingTextWatcher mFormatWatcher;

    private final Fragment mFragment;
    private final FragmentManager mFragmentManager;
    public final TextView mCountryCodePicker;
    private final TextView mNextButton;
    private final CaptchaView mCaptchaView;
    private final CountryCodeData mCountryCodeData;
    private CountryCodePickerDialog mCountryCodePickerDialog = null;

    private final NextButtonAdapter mNextButtonAdapter;

    public PhoneFieldViewHolder(Fragment fragment, FragmentManager fragmentManager, SearchEditText phoneField,
        TextView countryCodePicker, TextView nextButton, CaptchaView captchaView, CountryCodeData countryCodeData,
        NextButtonAdapter phoneFieldStateAdapter) {
        mPhoneField = phoneField;
        mValidWatcher = new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                if (mCaptchaView.isFieldValid()) {
                    mNextButtonAdapter.active();
                } else {
                    mNextButtonAdapter.disable();
                }
            }
        };

        mFragment = fragment;
        mFragmentManager = fragmentManager;
        mCountryCodePicker = countryCodePicker;
        mNextButton = nextButton;
        mCaptchaView = captchaView;
        mCountryCodeData = countryCodeData;
        mNextButtonAdapter = phoneFieldStateAdapter;
    }

    public void updateCountryCode(CountryCodeData countryCodeData) {
        mCountryCodePicker.setText(countryCodeData.formatSimple());
        if (!mCountryCodeData.equals(countryCodeData)) {
            updatePhoneNumberFormatting(countryCodeData);
        }
    }

    private void updatePhoneNumberFormatting(CountryCodeData countryCodeData) {
        if (mFormatWatcher != null) {
            mPhoneField.removeTextChangedListener(mFormatWatcher);
        }
        mFormatWatcher = new PhoneNumberFormattingTextWatcher(countryCodeData.country);
        mPhoneField.addTextChangedListener(mFormatWatcher);
    }

    @Override
    public void onCreateView(View view) {
        mCountryCodePicker.setText(mCountryCodeData.formatSimple());
        mCountryCodePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountryCodePickerDialog = new CountryCodePickerDialog();
                mCountryCodePickerDialog.setTargetFragment(mFragment, 0);
                mCountryCodePickerDialog.show(mFragmentManager, null);
            }
        });

        updatePhoneNumberFormatting(mCountryCodeData);
        mPhoneField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT && mCaptchaView.isFieldValid()) {
                    onSendCaptcha();
                    return true;
                }
                return false;
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
        super.onResume();
        mPhoneField.addTextChangedListener(mValidWatcher);
    }

    @Override
    public void onPause() {
        if (mCountryCodePickerDialog != null) {
            mCountryCodePickerDialog.dismiss();
        }
        SoftInputUtils.hideInput(mPhoneField);
        mPhoneField.removeTextChangedListener(mValidWatcher);
    }
}
