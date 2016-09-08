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
import android.support.annotation.Nullable;
import android.support.design.widget.CheckableImageButton;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.auth.adapter.FieldAdapter;
import com.tomeokin.lspush.biz.auth.adapter.FieldCallback;
import com.tomeokin.lspush.biz.auth.adapter.FilterCallback;
import com.tomeokin.lspush.biz.auth.adapter.NextButtonAdapter;
import com.tomeokin.lspush.biz.auth.adapter.NextButtonCallback;
import com.tomeokin.lspush.biz.auth.adapter.PasswordFilter;
import com.tomeokin.lspush.biz.auth.adapter.UserIdFilter;
import com.tomeokin.lspush.biz.base.BaseFragment;
import com.tomeokin.lspush.biz.base.BaseTextWatcher;
import com.tomeokin.lspush.biz.model.UserInfoModel;
import com.tomeokin.lspush.common.Navigator;
import com.tomeokin.lspush.common.SoftInputUtils;
import com.tomeokin.lspush.data.model.CaptchaRequest;
import com.tomeokin.lspush.data.model.RegisterData;
import com.tomeokin.lspush.injection.component.AuthComponent;
import com.tomeokin.lspush.ui.widget.NotificationBar;

import javax.inject.Inject;

public class RegisterFragment extends BaseFragment
    implements NextButtonCallback, RegisterView, FilterCallback, FieldCallback {
    public static final int NEXT_BUTTON_ID = 0;
    public static final int UID_FILTER_ID = 1;
    public static final int PWD_FILTER_ID = 2;
    public static final int UID_ADAPTER_ID = 3;
    public static final int USER_NAME_ADAPTER_ID = 4;

    public static final String EXTRA_CAPTCHA_REQUEST = "extra.captcha.request";
    public static final String EXTRA_CAPTCHA_AUTH_CODE = "extra.captcha.auth.code";

    private CaptchaRequest mCaptchaRequest = null;
    private String mAuthCode;

    private NotificationBar mNotificationBar;
    private View mUserIdFieldLayout;
    private EditText mUserIdField;
    private View mUserNameFieldLayout;
    private EditText mUserNameField;
    private View mPasswordFieldLayout;
    private EditText mPasswordField;
    private NextButtonAdapter mNextButtonAdapter;
    private TextWatcher mValidWatcher;
    private View.OnFocusChangeListener mFocusChangeValidChecker;
    private FieldAdapter mUIDAdapter;
    private FieldAdapter mUserNameAdapter;

    @Inject RegisterPresenter mPresenter;

    public static Bundle prepareArgument(CaptchaRequest captchaRequest, String authCode) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_CAPTCHA_REQUEST, captchaRequest);
        bundle.putString(EXTRA_CAPTCHA_AUTH_CODE, authCode);
        return bundle;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments() != null && getArguments().containsKey(EXTRA_CAPTCHA_REQUEST)) {
            mCaptchaRequest = getArguments().getParcelable(EXTRA_CAPTCHA_REQUEST);
            mAuthCode = getArguments().getString(EXTRA_CAPTCHA_AUTH_CODE);
        }

        component(AuthComponent.class).inject(this);
        dispatchOnCreate(savedInstanceState);
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.auth_container, container, false);
        inflater.inflate(R.layout.fragment_register, (ViewGroup) view.findViewById(R.id.content_container), true);
        view.findViewById(R.id.image_icon).setBackgroundResource(R.drawable.register_name);

        mValidWatcher = new BaseTextWatcher() {
            @Override public void afterTextChanged(Editable s) {
                if (s == mUserIdField.getText()) {
                    // 如果用户修改了文本，此时如果处于加载状态，取消该状态，并进行状态同步，
                    // 同时取消已进行的网络请求
                    if (mUIDAdapter.getState() == FieldAdapter.WAITING) {
                        mUIDAdapter.active();
                        // TODO: 2016/9/1 取消未完成的 UID 检查(取消已进行的网络请求)
                    } else if (mUIDAdapter.getState() == FieldAdapter.INFO) {
                        mUIDAdapter.active();
                    }
                    mUIDAdapter.sync();
                } else if (s == mUserNameField.getText()) {
                    mUserNameAdapter.sync();
                }
                mNextButtonAdapter.sync();
            }
        };
        mFocusChangeValidChecker = new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if (v.getId() == R.id.userId_field) {
                    if (!hasFocus
                        && mUIDAdapter.getState() != FieldAdapter.WAITING
                        && mUIDAdapter.getState() != FieldAdapter.INFO
                        && isValidUserId()) {
                        mUIDAdapter.waiting();
                        mPresenter.checkUIDExist(mUserIdField.getText().toString());
                    }
                }
            }
        };

        mNotificationBar = (NotificationBar) view.findViewById(R.id.notification_bar);

        mUserIdFieldLayout = view.findViewById(R.id.userId_layout);
        mUserIdField = (EditText) view.findViewById(R.id.userId_field);
        ImageView uidValidButton = (ImageView) view.findViewById(R.id.userId_validation_button);
        ProgressBar uidProgressBar = (ProgressBar) view.findViewById(R.id.userId_waiting_progress);
        mUserIdField.setFilters(new InputFilter[] {
            new UserIdFilter(UID_FILTER_ID, this), new InputFilter.LengthFilter(UserInfoModel.USER_ID_MAX_LENGTH)
        });
        mUserIdField.setOnFocusChangeListener(mFocusChangeValidChecker);
        mUIDAdapter = new FieldAdapter(UID_ADAPTER_ID, this, uidValidButton, uidProgressBar);
        registerLifecycleListener(mUIDAdapter);

        mUserNameFieldLayout = view.findViewById(R.id.userName_layout);
        mUserNameField = (EditText) view.findViewById(R.id.userName_field);
        ImageView userNameValidButton = (ImageView) view.findViewById(R.id.userName_validation_button);
        mUserNameField.setFilters(new InputFilter[] {new InputFilter.LengthFilter(UserInfoModel.USER_NAME_MAX_LENGTH)});
        mUserNameAdapter = new FieldAdapter(USER_NAME_ADAPTER_ID, this, userNameValidButton, null);
        registerLifecycleListener(mUserNameAdapter);

        mPasswordFieldLayout = view.findViewById(R.id.userPwd_layout);
        mPasswordField = (EditText) view.findViewById(R.id.userPwd_field);
        mPasswordField.setFilters(new InputFilter[] {
            new PasswordFilter(PWD_FILTER_ID, this),
            new InputFilter.LengthFilter(UserInfoModel.USER_PASSWORD_MAX_LENGTH)
        });
        mPasswordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT && isFieldValid()) {
                    register();
                    return true;
                }
                return false;
            }
        });
        final CheckableImageButton toggleButton = (CheckableImageButton) view.findViewById(R.id.userPwd_toggle_button);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                final int selection = mPasswordField.getSelectionEnd();
                boolean passwordToggledVisible;
                if (mPasswordField.getTransformationMethod() instanceof PasswordTransformationMethod) {
                    mPasswordField.setTransformationMethod(null);
                    passwordToggledVisible = true;
                } else {
                    mPasswordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordToggledVisible = false;
                }
                toggleButton.setChecked(passwordToggledVisible);
                mPasswordField.setSelection(selection);
            }
        });

        TextView nextButton = (TextView) view.findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                register();
            }
        });
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.next_progress);
        mNextButtonAdapter = new NextButtonAdapter(NEXT_BUTTON_ID, this, nextButton, progressBar);
        registerLifecycleListener(mNextButtonAdapter);

        TextView loginButton = (TextView) view.findViewById(R.id.login_button);
        loginButton.setText(getString(R.string.already_have_an_account_log_in));
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Navigator.moveTo(getContext(), getFragmentManager(), LoginFragment.class, null);
            }
        });

        dispatchOnCreateView(view);
        return view;
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.attachView(this);
    }

    @Override public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mUserIdField.addTextChangedListener(mValidWatcher);
        mUserIdField.requestFocus();
        mUserNameField.addTextChangedListener(mValidWatcher);
        mPasswordField.addTextChangedListener(mValidWatcher);
        dispatchOnResume();
    }

    @Override public void onPause() {
        super.onPause();
        SoftInputUtils.hideInput(mPasswordField);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
        dispatchOnPause();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        mPresenter.detachView();
        mUserIdFieldLayout = null;
        mUserNameFieldLayout = null;
        mPasswordFieldLayout = null;
        mUserIdField.removeTextChangedListener(mValidWatcher);
        mUserNameField.removeTextChangedListener(mValidWatcher);
        mPasswordField.removeTextChangedListener(mValidWatcher);
        mUserIdField.setOnFocusChangeListener(null);
        mUserNameField.setOnFocusChangeListener(null);
        mPasswordField.setOnFocusChangeListener(null);
        mFocusChangeValidChecker = null;
        mPasswordField.setOnEditorActionListener(null);
        mValidWatcher = null;
        mUserIdField = null;
        mUserNameField = null;
        mPasswordField = null;
        unregister(mUIDAdapter);
        unregister(mUserNameAdapter);
        unregister(mNextButtonAdapter);
        mUIDAdapter = null;
        mUserNameAdapter = null;
        mNextButtonAdapter = null;
        dispatchOnDestroyView();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        mPresenter = null;
        dispatchOnDestroy();
    }

    public void register() {
        mNextButtonAdapter.waiting();
        RegisterData data = new RegisterData();
        data.setCaptchaRequest(mCaptchaRequest);
        data.setAuthCode(mAuthCode);
        data.setUserId(mUserIdField.getText().toString());
        data.setNickname(mUserNameField.getText().toString());
        data.setPassword(mPasswordField.getText().toString());
        mPresenter.register(data);
    }

    public boolean isValidUserId() {
        return mUserIdField.getText().toString().trim().length() >= UserInfoModel.USER_ID_MIN_LENGTH
            && mUIDAdapter.getState() != FieldAdapter.INFO;
    }

    public boolean isValidUserName() {
        return mUserNameField.getText().toString().trim().length() >= UserInfoModel.USER_NAME_MIN_LENGTH;
    }

    public boolean isValidPassword() {
        return mPasswordField.getText().toString().trim().length() >= UserInfoModel.USER_PASSWORD_MIN_LENGTH;
    }

    public boolean isFieldValid() {
        return isValidUserId() && isValidUserName() && isValidPassword();
    }

    @Override public int checkState(NextButtonAdapter adapter, int requestId, int currentState) {
        if (requestId == NEXT_BUTTON_ID) {
            if (currentState == NextButtonAdapter.WAITING) {
                return currentState;
            } else if (isFieldValid()) {
                return NextButtonAdapter.ACTIVE;
            } else {
                return NextButtonAdapter.DISABLE;
            }
        }

        return -1;
    }

    @Override public void onStateChange(NextButtonAdapter adapter, int requestId, int currentState) {
        if (requestId == NEXT_BUTTON_ID) {
            if (currentState == NextButtonAdapter.WAITING) {
                mUserIdFieldLayout.setEnabled(false);
                mUserNameFieldLayout.setEnabled(false);
                mPasswordFieldLayout.setEnabled(false);
            } else {
                mUserIdFieldLayout.setEnabled(true);
                mUserNameFieldLayout.setEnabled(true);
                mPasswordFieldLayout.setEnabled(true);
            }
        }
    }

    @Override public void onInvalidCharacter(int requestId, char c) {
        if (requestId == UID_FILTER_ID || requestId == PWD_FILTER_ID) {
            mNotificationBar.showTemporaryInverse(getString(R.string.not_support_character, c));
        }
    }

    @Override public int checkState(FieldAdapter adapter, int requestId, int currentState) {
        if (requestId == UID_ADAPTER_ID) {
            if (currentState == FieldAdapter.WAITING || currentState == FieldAdapter.INFO) {
                return currentState;
            } else if (isValidUserId()) {
                return FieldAdapter.ACTIVE;
            } else {
                return FieldAdapter.DISABLE;
            }
        } else if (requestId == USER_NAME_ADAPTER_ID) {
            if (currentState == FieldAdapter.WAITING || currentState == FieldAdapter.INFO) {
                return currentState;
            } else if (isValidUserName()) {
                return FieldAdapter.ACTIVE;
            } else {
                return FieldAdapter.DISABLE;
            }
        }
        return -1;
    }

    @Override public void onStateChange(FieldAdapter adapter, int requestId, int currentState) {

    }

    @Override public void onCheckUIDSuccess() {
        mUIDAdapter.active();
    }

    @Override public void onCheckUIDFailure(String message) {
        mUIDAdapter.info();
        // TODO: 2016/9/1 提示语句
        mNotificationBar.showTemporaryInverse(getString(R.string.uid_not_unique));
    }
}
