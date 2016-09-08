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
package com.tomeokin.lspush.biz.auth;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.auth.adapter.BaseStateAdapter;
import com.tomeokin.lspush.biz.auth.adapter.BaseStateCallback;
import com.tomeokin.lspush.biz.auth.adapter.CaptchaViewHolder;
import com.tomeokin.lspush.biz.auth.adapter.EmailFieldViewHolder;
import com.tomeokin.lspush.biz.auth.adapter.FieldSwitchAdapter;
import com.tomeokin.lspush.biz.auth.adapter.NextButtonAdapter;
import com.tomeokin.lspush.biz.auth.adapter.PhoneFieldViewHolder;
import com.tomeokin.lspush.biz.auth.listener.OnCountryCodeSelectedListener;
import com.tomeokin.lspush.biz.base.BaseFragment;
import com.tomeokin.lspush.common.CountryCodeUtils;
import com.tomeokin.lspush.common.Navigator;
import com.tomeokin.lspush.common.SMSCaptchaUtils;
import com.tomeokin.lspush.common.ValidateUtils;
import com.tomeokin.lspush.data.model.CaptchaRequest;
import com.tomeokin.lspush.data.model.CountryCodeData;
import com.tomeokin.lspush.injection.component.AuthComponent;
import com.tomeokin.lspush.ui.widget.SearchEditText;

import java.util.List;

import javax.inject.Inject;

import cn.smssdk.EventHandler;

public class CaptchaFragment extends BaseFragment
    implements CaptchaView, BaseStateCallback, OnCountryCodeSelectedListener {
    public static final int EMAIL_NEXT_ID = 0;
    public static final int PHONE_NEXT_ID = 1;

    private CaptchaRequest mCaptchaRequest = null;
    private CountryCodeData mCountryCodeData;
    private boolean mShowEmail;
    private boolean mShowingEmailTab;
    private AutoCompleteTextView mEmailField;
    private SearchEditText mPhoneField;
    private NextButtonAdapter mEmailNextButtonAdapter;
    private EmailFieldViewHolder mEmailFieldViewHolder;
    private NextButtonAdapter mPhoneNextButtonAdapter;
    private PhoneFieldViewHolder mPhoneFieldViewHolder;
    private CaptchaViewHolder mViewHolder;

    private EventHandler mEventHandler;
    private Handler mHandler;

    @Inject CaptchaPresenter mCaptchaPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        component(AuthComponent.class).inject(this);

        mCountryCodeData = CountryCodeUtils.getDefault(getContext());
        mShowEmail = true;
        mShowingEmailTab = true;

        dispatchOnCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.auth_container, container, false);
        inflater.inflate(R.layout.fragment_captcha, (ViewGroup) view.findViewById(R.id.content_container), true);
        ImageView imageIcon = (ImageView) view.findViewById(R.id.image_icon);
        imageIcon.setBackgroundResource(R.drawable.register_name);

        ViewGroup emailTab, phoneTab;
        if (mShowEmail) {
            emailTab = (ViewGroup) view.findViewById(R.id.left_tab);
            phoneTab = (ViewGroup) view.findViewById(R.id.right_tab);
        } else {
            emailTab = (ViewGroup) view.findViewById(R.id.right_tab);
            phoneTab = (ViewGroup) view.findViewById(R.id.left_tab);
        }

        // 配置 email tab
        TextView emailTabText = (TextView) emailTab.findViewById(R.id.tab_text);
        emailTabText.setText(R.string.email);
        View emailTabSelection = emailTab.findViewById(R.id.tab_selection);

        View emailTabContent = ((ViewStub) view.findViewById(R.id.email_field_stub)).inflate();
        mEmailField = (AutoCompleteTextView) emailTabContent.findViewById(R.id.email_field);
        ImageView clearButton = (ImageView) emailTabContent.findViewById(R.id.clear_button);

        FrameLayout emailNextLayout = (FrameLayout) view.findViewById(R.id.next_button_1);
        TextView emailNextButton = (TextView) emailNextLayout.findViewById(R.id.next_button);
        ProgressBar emailProgressBar = (ProgressBar) emailNextLayout.findViewById(R.id.next_progress);

        mEmailNextButtonAdapter =
            new NextButtonAdapter(EMAIL_NEXT_ID, this, getContext(), emailNextButton, emailProgressBar);
        mEmailFieldViewHolder =
            new EmailFieldViewHolder(mEmailField, clearButton, emailNextButton, this, mEmailNextButtonAdapter);
        registerLifecycleListener(mEmailNextButtonAdapter);
        registerLifecycleListener(mEmailFieldViewHolder);

        // 配置 phone tab
        TextView phoneTabText = (TextView) phoneTab.findViewById(R.id.tab_text);
        phoneTabText.setText(R.string.phone);
        View phoneTabSelection = phoneTab.findViewById(R.id.tab_selection);

        View phoneTabContent = ((ViewStub) view.findViewById(R.id.phone_field_stub)).inflate();
        TextView countryCodePicker = (TextView) phoneTabContent.findViewById(R.id.country_code_picker);
        mPhoneField = (SearchEditText) phoneTabContent.findViewById(R.id.phone_field);

        FrameLayout phoneNextLayout = (FrameLayout) view.findViewById(R.id.next_button_2);
        TextView phoneNextButton = (TextView) phoneNextLayout.findViewById(R.id.next_button);
        ProgressBar phoneProgressBar = (ProgressBar) phoneNextLayout.findViewById(R.id.next_progress);

        mPhoneNextButtonAdapter =
            new NextButtonAdapter(PHONE_NEXT_ID, this, getContext(), phoneNextButton, phoneProgressBar);
        mPhoneFieldViewHolder =
            new PhoneFieldViewHolder(mPhoneField, countryCodePicker, phoneNextButton, this, mCountryCodeData,
                mPhoneNextButtonAdapter);
        registerLifecycleListener(mPhoneNextButtonAdapter);
        registerLifecycleListener(mPhoneFieldViewHolder);

        FieldSwitchAdapter emailSwitchAdapter =
            new FieldSwitchAdapter(emailTabContent, emailTabSelection, emailNextLayout, mEmailField, emailTabText,
                emailTab, mEmailNextButtonAdapter);
        FieldSwitchAdapter phoneSwitchAdapter =
            new FieldSwitchAdapter(phoneTabContent, phoneTabSelection, phoneNextLayout, mPhoneField, phoneTabText,
                phoneTab, mPhoneNextButtonAdapter);
        mViewHolder =
            new CaptchaViewHolder(this, emailTab, phoneTab, mShowingEmailTab, emailSwitchAdapter, phoneSwitchAdapter);
        registerLifecycleListener(mViewHolder);

        TextView loginButton = (TextView) view.findViewById(R.id.login_button);
        loginButton.setText(getString(R.string.already_have_an_account_log_in));
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigator.moveTo(getContext(), getFragmentManager(), LoginFragment.class, null);
            }
        });

        dispatchOnCreateView(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCaptchaPresenter.attachView(this);
        mHandler = new Handler();
        mEventHandler = new SMSCaptchaUtils.CustomEventHandler(mHandler, mCaptchaPresenter);
        SMSCaptchaUtils.registerEventHandler(mEventHandler);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dispatchOnResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
        dispatchOnPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCaptchaPresenter.detachView();
        mEmailField = null;
        mPhoneField = null;
        unregister(mEmailNextButtonAdapter);
        unregister(mEmailFieldViewHolder);
        unregister(mPhoneNextButtonAdapter);
        unregister(mPhoneFieldViewHolder);
        unregister(mViewHolder);
        mEmailNextButtonAdapter = null;
        mEmailFieldViewHolder = null;
        mPhoneNextButtonAdapter = null;
        mPhoneFieldViewHolder = null;
        mViewHolder = null;
        SMSCaptchaUtils.unregisterEventHandler(mEventHandler);
        mEventHandler = null;
        mHandler = null;
        dispatchOnDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCaptchaPresenter = null;
        mCountryCodeData = null;
        mCaptchaRequest = null;
        dispatchOnDestroy();
    }

    @Override
    public CaptchaFragment self() {
        return this;
    }

    @Override
    public boolean isFieldValid() {
        if (mShowingEmailTab) {
            return ValidateUtils.isEmailValid(mEmailField.getText().toString());
        } else {
            return ValidateUtils.isPhoneValid(mPhoneField.getText().toString(), mCountryCodeData.country);
        }
    }

    @Override
    public List<String> getHistoryUserEmails() {
        return mCaptchaPresenter.getHistoryUserEmails();
    }

    @Override
    public void sendCaptcha() {
        if (mCaptchaRequest == null) {
            mCaptchaRequest = new CaptchaRequest();
        }
        if (mShowingEmailTab) {
            mCaptchaRequest.setSendObject(mEmailField.getText().toString());
            mCaptchaRequest.setRegion("");
        } else {
            String phone = PhoneNumberUtils.stripSeparators(mPhoneField.getText().toString());
            mCaptchaRequest.setSendObject(phone);
            mCaptchaRequest.setRegion(mCountryCodeData.country);
        }
        mCaptchaPresenter.sendCaptchaCode(mCaptchaRequest, mCountryCodeData.countryCode);
    }

    @Override
    public void onTabSelectUpdate(boolean showingEmailTab) {
        mShowingEmailTab = showingEmailTab;
    }

    @Override
    public void onCountryCodeSelected(CountryCodeData countryCodeData) {
        mCountryCodeData = countryCodeData;
        mPhoneFieldViewHolder.updateCountryCode(countryCodeData);
    }

    @Override
    public void moveToCaptchaVerify() {
        syncNextButton();
        if (mCaptchaRequest != null) {
            Bundle bundle = CaptchaConfirmationFragment.prepareArgument(mCaptchaRequest, mCountryCodeData.countryCode);
            Navigator.moveTo(this, CaptchaConfirmationFragment.class, bundle);
        }
    }

    @Override
    public void onSentCaptchaCodeFailure(String message) {
        syncNextButton();
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void syncNextButton() {
        if (mShowingEmailTab) {
            mEmailNextButtonAdapter.sync();
        } else {
            mPhoneNextButtonAdapter.sync();
        }
    }

    @Override
    public boolean isActive(BaseStateAdapter adapter, int requestId) {
        if (requestId == EMAIL_NEXT_ID) {
            return ValidateUtils.isEmailValid(mEmailField.getText().toString());
        } else if (requestId == PHONE_NEXT_ID) {
            return ValidateUtils.isPhoneValid(mPhoneField.getText().toString(), mCountryCodeData.country);
        }
        return false;
    }

    @Override
    public void onStateChange(BaseStateAdapter adapter, int requestId, int currentState) {
        if (requestId == EMAIL_NEXT_ID) {
            final boolean enable = currentState != NextButtonAdapter.WAITING;
            mViewHolder.mEmailTab.setEnabled(enable);
            mViewHolder.mPhoneTab.setEnabled(enable);
            mEmailFieldViewHolder.mEmailField.setEnabled(enable);
            mEmailFieldViewHolder.mClearButton.setEnabled(enable);
            if (enable) {
                mEmailFieldViewHolder.mClearButton.setVisibility(
                    mEmailField.getText().length() != 0 ? View.VISIBLE : View.INVISIBLE);
            } else {
                mEmailFieldViewHolder.mClearButton.setVisibility(View.INVISIBLE);
            }
        } else if (requestId == PHONE_NEXT_ID) {
            final boolean enable = currentState != NextButtonAdapter.WAITING;
            mViewHolder.mEmailTab.setEnabled(enable);
            mViewHolder.mPhoneTab.setEnabled(enable);
            mPhoneFieldViewHolder.mCountryCodePicker.setEnabled(enable);
            mPhoneField.setEnabled(enable);
            mPhoneField.setClearButtonEnabled(enable);
        }
    }

    //@Override
    //public void onActivityResult(int requestCode, int resultCode, Intent data) {
    //    if (resultCode != Activity.RESULT_OK) {
    //        return;
    //    }
    //
    //    if (requestCode == Navigator.REQUEST_CODE) {
    //        Bundle bundle = data.getExtras();
    //        if (bundle != null) {
    //            Timber.i("just a test %s", bundle.getString("hello_captcha"));
    //        }
    //    } else {
    //        super.onActivityResult(requestCode, resultCode, data);
    //    }
    //}
}
