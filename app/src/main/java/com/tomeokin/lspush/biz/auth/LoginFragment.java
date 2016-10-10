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

import android.content.Intent;
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
import com.tomeokin.lspush.biz.auth.adapter.NextButtonAdapter;
import com.tomeokin.lspush.biz.auth.filter.FilterCallback;
import com.tomeokin.lspush.biz.auth.filter.PasswordFilter;
import com.tomeokin.lspush.biz.auth.filter.UserIdFilter;
import com.tomeokin.lspush.biz.base.support.BaseActionCallback;
import com.tomeokin.lspush.biz.base.BaseFragment;
import com.tomeokin.lspush.biz.base.support.BaseStateAdapter;
import com.tomeokin.lspush.biz.base.support.BaseStateCallback;
import com.tomeokin.lspush.ui.widget.listener.BaseTextWatcher;
import com.tomeokin.lspush.biz.common.UserScene;
import com.tomeokin.lspush.biz.home.HomeActivity;
import com.tomeokin.lspush.biz.model.UserInfoModel;
import com.tomeokin.lspush.biz.usercase.auth.LoginAction;
import com.tomeokin.lspush.biz.usercase.user.LocalUserInfoAction;
import com.tomeokin.lspush.common.Navigator;
import com.tomeokin.lspush.common.SoftInputUtils;
import com.tomeokin.lspush.data.model.AccessResponse;
import com.tomeokin.lspush.data.model.BaseResponse;
import com.tomeokin.lspush.data.model.LoginData;
import com.tomeokin.lspush.data.model.User;
import com.tomeokin.lspush.injection.component.AuthComponent;
import com.tomeokin.lspush.ui.widget.NotificationBar;
import com.tomeokin.lspush.ui.widget.SearchEditText;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

public class LoginFragment extends BaseFragment implements BaseActionCallback, BaseStateCallback, FilterCallback {
    public static final int NEXT_BUTTON_ID = 0;
    public static final int UID_FILTER_ID = 1;
    public static final int PWD_FILTER_ID = 2;

    private Unbinder mUnBinder;
    @BindView(R.id.image_icon) ImageView mUserAvatar;
    @BindView(R.id.account_field) SearchEditText mUidField;
    @BindView(R.id.password_layout) View mPasswordLayout;
    @BindView(R.id.password_field) EditText mPasswordField;
    @BindView(R.id.notification_bar) NotificationBar mNotificationBar;

    private NextButtonAdapter mNextButtonAdapter;
    private TextWatcher mValidWatcher;

    @Inject LoginAction mLoginAction;
    @Inject LocalUserInfoAction mLocalUserInfoAction;
    private LoginData mLoginData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        component(AuthComponent.class).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_auth_container, container, false);
        inflater.inflate(R.layout.fragment_login, (ViewGroup) view.findViewById(R.id.content_container), true);
        mUnBinder = ButterKnife.bind(this, view);

        mValidWatcher = new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                mNextButtonAdapter.sync();
            }
        };

        mUserAvatar.setBackgroundResource(R.drawable.auth_avatar);

        // region: UID Field
        mUidField.setFilters(new InputFilter[] {
            new UserIdFilter(UID_FILTER_ID, this), new InputFilter.LengthFilter(UserInfoModel.USER_ID_MAX_LENGTH)
        });
        mUidField.requestFocus();
        // endregion

        // region: Password Field
        mPasswordField.setFilters(new InputFilter[] {
            new PasswordFilter(PWD_FILTER_ID, this),
            new InputFilter.LengthFilter(UserInfoModel.USER_PASSWORD_MAX_LENGTH)
        });
        mPasswordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT && isFieldValid()) {
                    login();
                    return true;
                }
                return false;
            }
        });
        final CheckableImageButton toggleButton = ButterKnife.findById(view, R.id.userPwd_toggle_button);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 保存 EditText 焦点
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
        // endregion

        // region: NextButton
        TextView nextButton = ButterKnife.findById(view, R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        ProgressBar progressBar = ButterKnife.findById(view, R.id.next_progress);
        mNextButtonAdapter = new NextButtonAdapter(NEXT_BUTTON_ID, this, getContext(), nextButton, progressBar);
        registerLifecycleListener(mNextButtonAdapter);
        // endregion

        // region: Bottom Register Link
        TextView loginButton = ButterKnife.findById(view, R.id.reg_login_button);
        loginButton.setText(getString(R.string.don_not_have_an_account_register));
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigator.moveTo(getContext(), getFragmentManager(), CaptchaFragment.class, null);
            }
        });
        // endregion

        dispatchOnCreateView(view);
        return view;
    }

    private boolean isFieldValid() {
        return UserInfoModel.isValidUid(mUidField.getText()) && UserInfoModel.isValidPassword(mPasswordField.getText());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLoginAction.attach(this);
        mLocalUserInfoAction.attach(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mUidField.addTextChangedListener(mValidWatcher);
        mPasswordField.addTextChangedListener(mValidWatcher);
        dispatchOnResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        SoftInputUtils.hideInput(mPasswordField);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
        dispatchOnPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mLoginAction.detach();
        mLocalUserInfoAction.detach();

        dispatchOnDestroyView();
        mPasswordField.setOnEditorActionListener(null);
        mUidField.removeTextChangedListener(mValidWatcher);
        mPasswordField.removeTextChangedListener(mValidWatcher);
        mValidWatcher = null;
        if (mUnBinder != null) {
            mUnBinder.unbind();
            mUnBinder = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLoginAction = null;
        mLocalUserInfoAction = null;
    }

    private void login() {
        mNextButtonAdapter.waiting();
        if (mLoginData == null) {
            mLoginData = new LoginData();
        }
        mLoginData.setUid(mUidField.getText().toString());
        mLoginData.setPassword(mPasswordField.getText().toString());
        mLoginAction.login(mLoginData);
    }

    @Override
    public void onInvalidCharacter(int requestId, char c) {
        if (requestId == UID_FILTER_ID || requestId == PWD_FILTER_ID) {
            mNotificationBar.showTemporaryInverse(getString(R.string.not_support_character, c));
        }
    }

    @Override
    public boolean isActive(BaseStateAdapter adapter, int requestId) {
        if (requestId == NEXT_BUTTON_ID) {
            return isFieldValid();
        }
        // else
        return false;
    }

    @Override
    public void onStateChange(BaseStateAdapter adapter, int requestId, int currentState) {
        if (requestId == NEXT_BUTTON_ID) {
            final boolean enable = currentState != NextButtonAdapter.WAITING;
            mUidField.setEnabled(enable);
            mPasswordLayout.setEnabled(enable);
        }
    }

    @Override
    public void onActionFailure(int action, @Nullable BaseResponse response, String message) {
        if (action == UserScene.ACTION_LOGIN) {
            mNextButtonAdapter.syncRevokeWaiting();
            mNotificationBar.showTemporaryInverse(message);
        }
    }

    @Override
    public void onActionSuccess(int action, @Nullable BaseResponse response) {
        if (action == UserScene.ACTION_LOGIN) {
            AccessResponse res = (AccessResponse) response;
            if (res != null) {
                User user = res.getUser();
                user.setPassword(mLoginData.getPassword());
                mLocalUserInfoAction.userLogin(res, res.getUser());
                Intent intent = new Intent(getContext(), HomeActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
            Timber.i("register success");
            mNextButtonAdapter.syncRevokeWaiting();
        }
    }
}
