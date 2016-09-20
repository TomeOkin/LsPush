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
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.auth.adapter.NextButtonAdapter;
import com.tomeokin.lspush.biz.usercase.CheckCaptchaAction;
import com.tomeokin.lspush.biz.usercase.SendCaptchaAction;
import com.tomeokin.lspush.biz.base.BaseActionCallback;
import com.tomeokin.lspush.biz.base.BaseFragment;
import com.tomeokin.lspush.biz.base.BaseStateAdapter;
import com.tomeokin.lspush.biz.base.BaseStateCallback;
import com.tomeokin.lspush.biz.base.BaseTextWatcher;
import com.tomeokin.lspush.biz.common.UserScene;
import com.tomeokin.lspush.common.Navigator;
import com.tomeokin.lspush.common.SoftInputUtils;
import com.tomeokin.lspush.data.model.BaseResponse;
import com.tomeokin.lspush.data.model.CaptchaRequest;
import com.tomeokin.lspush.injection.component.AuthComponent;
import com.tomeokin.lspush.ui.widget.SearchEditText;
import com.tomeokin.lspush.ui.widget.dialog.SimpleDialogBuilder;

import javax.inject.Inject;

public class CaptchaConfirmationFragment extends BaseFragment implements BaseActionCallback, BaseStateCallback {
    public static final int NEXT_BUTTON_ID = 0;

    public static final String EXTRA_CAPTCHA_REQUEST = "extra.captcha.request";
    public static final String EXTRA_CAPTCHA_COUNTRY_CODE = "extra.captcha.country.code";

    public static final int DEFAULT_WAITING_TIME = 60_000;
    private long mWaitingTime = DEFAULT_WAITING_TIME;

    private CaptchaRequest mCaptchaRequest = null;
    private String mCountryCode;
    private long mLastSentTime;
    private SearchEditText mCaptchaField;
    private NextButtonAdapter mNextButtonAdapter;
    private TextWatcher mValidWatcher;

    @Inject SendCaptchaAction mSendCaptchaAction;
    @Inject CheckCaptchaAction mCheckCaptchaAction;

    public static Bundle prepareArgument(CaptchaRequest captchaRequest, String countryCode) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_CAPTCHA_REQUEST, captchaRequest);
        bundle.putString(EXTRA_CAPTCHA_COUNTRY_CODE, countryCode);
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments() != null && getArguments().containsKey(EXTRA_CAPTCHA_REQUEST)) {
            mCaptchaRequest = getArguments().getParcelable(EXTRA_CAPTCHA_REQUEST);
            mCountryCode = getArguments().getString(EXTRA_CAPTCHA_COUNTRY_CODE);
        }

        component(AuthComponent.class).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.auth_container, container, false);
        inflater.inflate(R.layout.fragment_captcha_confirmation, (ViewGroup) view.findViewById(R.id.content_container),
            true);

        view.findViewById(R.id.image_icon).setBackgroundResource(R.drawable.register_name);
        ((TextView) view.findViewById(R.id.field_title)).setText(R.string.enter_captcha_code);
        TextView fieldDetail = (TextView) view.findViewById(R.id.field_detail);
        fieldDetail.setText(R.string.resend_captcha_code);
        mLastSentTime = SystemClock.elapsedRealtime();
        fieldDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastSentTime <= mWaitingTime) {
                    SimpleDialogBuilder builder = new SimpleDialogBuilder(getContext(), getFragmentManager());
                    builder.setTitle(R.string.send_captcha_code)
                        .setMessage(getResources().getString(R.string.send_captcha_dialog_notice,
                            SystemClock.elapsedRealtime() - mLastSentTime))
                        .setNeutralText(R.string.ok)
                        .show();
                } else {
                    mLastSentTime = SystemClock.elapsedRealtime();
                    mSendCaptchaAction.sendCaptchaCode(mCaptchaRequest, mCountryCode);
                }
            }
        });
        mCaptchaField = (SearchEditText) view.findViewById(R.id.captcha_field);
        mCaptchaField.requestFocus();
        mCaptchaField.setFilters(new InputFilter[] {new InputFilter.LengthFilter(6)});
        mCaptchaField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT && isFieldValid()) {
                    checkCaptcha();
                    return true;
                }

                return false;
            }
        });
        mValidWatcher = new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (isFieldValid()) {
                    mNextButtonAdapter.active();
                } else {
                    mNextButtonAdapter.disable();
                }
            }
        };

        TextView nextButton = (TextView) view.findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCaptcha();
            }
        });
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.next_progress);
        mNextButtonAdapter = new NextButtonAdapter(NEXT_BUTTON_ID, this, getContext(), nextButton, progressBar);
        registerLifecycleListener(mNextButtonAdapter);

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
        mSendCaptchaAction.attach(this);
        mCheckCaptchaAction.attach(this);
        dispatchOnViewCreate(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(mCaptchaField.getText().toString())) {
            mCaptchaField.requestFocus();
            SoftInputUtils.showInput(mCaptchaField);
        } else {
            SoftInputUtils.hideInput(mCaptchaField);
        }
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mCaptchaField.addTextChangedListener(mValidWatcher);
        dispatchOnResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        SoftInputUtils.hideInput(mCaptchaField);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
        dispatchOnPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dispatchOnDestroyView();
        unregister(mNextButtonAdapter);
        mNextButtonAdapter = null;
        mCaptchaField.removeTextChangedListener(mValidWatcher);
        mValidWatcher = null;
        mCaptchaField.setOnEditorActionListener(null);
        mCaptchaField = null;
        mSendCaptchaAction.detach();
        mCheckCaptchaAction.detach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSendCaptchaAction = null;
        mCheckCaptchaAction = null;
    }

    public void checkCaptcha() {
        mNextButtonAdapter.waiting();
        mCheckCaptchaAction.checkCaptcha(mCaptchaRequest, mCaptchaField.getText().toString(), mCountryCode);
    }

    public boolean isFieldValid() {
        return mCaptchaField.getText().toString().trim().length() >= 4;
    }

    @Override
    public boolean isActive(BaseStateAdapter adapter, int requestId) {
        if (requestId == NEXT_BUTTON_ID) {
            return isFieldValid();
        }
        // other
        return false;
    }

    @Override
    public void onStateChange(BaseStateAdapter adapter, int requestId, int currentState) {
        if (requestId == NEXT_BUTTON_ID) {
            final boolean enable = currentState != NextButtonAdapter.WAITING;
            mCaptchaField.setEnabled(enable);
            mCaptchaField.setClearButtonEnabled(enable);
        }
    }

    @Override
    public void onActionFailure(int action, @Nullable BaseResponse response, String message) {
        if (action == UserScene.ACTION_SEND_CAPTCHA) {
            mWaitingTime = DEFAULT_WAITING_TIME / 2;
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        } else if (action == UserScene.ACTION_CHECK_CAPTCHA) {
            mNextButtonAdapter.syncRevokeWaiting();
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActionSuccess(int action, @Nullable BaseResponse response) {
        if (action == UserScene.ACTION_SEND_CAPTCHA) {
            mWaitingTime = DEFAULT_WAITING_TIME;
            Toast.makeText(getContext(), getResources().getString(R.string.receive_captcha_notice), Toast.LENGTH_SHORT)
                .show();
        } else if (action == UserScene.ACTION_CHECK_CAPTCHA) {
            mNextButtonAdapter.syncRevokeWaiting();
            Bundle bundle = RegisterFragment.prepareArgument(mCaptchaRequest, mCaptchaField.getText().toString());
            Navigator.moveTo(this, RegisterFragment.class, bundle);
        }
    }
}
