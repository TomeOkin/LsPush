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
import com.tomeokin.lspush.biz.auth.adapter.NextButtonAdapter;
import com.tomeokin.lspush.biz.auth.filter.AccountFilter;
import com.tomeokin.lspush.biz.auth.filter.FilterCallback;
import com.tomeokin.lspush.biz.auth.filter.PasswordFilter;
import com.tomeokin.lspush.biz.base.BaseActionCallback;
import com.tomeokin.lspush.biz.base.BaseFragment;
import com.tomeokin.lspush.biz.base.BaseStateAdapter;
import com.tomeokin.lspush.biz.base.BaseStateCallback;
import com.tomeokin.lspush.biz.base.BaseTextWatcher;
import com.tomeokin.lspush.biz.model.UserInfoModel;
import com.tomeokin.lspush.common.Navigator;
import com.tomeokin.lspush.common.SoftInputUtils;
import com.tomeokin.lspush.data.model.BaseResponse;
import com.tomeokin.lspush.injection.component.AuthComponent;
import com.tomeokin.lspush.ui.widget.NotificationBar;
import com.tomeokin.lspush.ui.widget.SearchEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class LoginFragment extends BaseFragment implements BaseActionCallback, BaseStateCallback, FilterCallback {
    public static final int NEXT_BUTTON_ID = 0;
    public static final int ACCOUNT_FILTER_ID = 1;
    public static final int PWD_FILTER_ID = 2;

    private Unbinder mUnBinder;
    @BindView(R.id.image_icon) ImageView mUserAvatar;
    @BindView(R.id.account_field) SearchEditText mAccountField;
    @BindView(R.id.password_layout) View mPasswordLayout;
    @BindView(R.id.password_field) EditText mPasswordField;
    @BindView(R.id.notification_bar) NotificationBar mNotificationBar;

    private NextButtonAdapter mNextButtonAdapter;
    private TextWatcher mValidWatcher;

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
        View view = inflater.inflate(R.layout.auth_container, container, false);
        inflater.inflate(R.layout.fragment_login, (ViewGroup) view.findViewById(R.id.content_container), true);
        mUnBinder = ButterKnife.bind(this, view);

        mValidWatcher = new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                mNextButtonAdapter.sync();
            }
        };

        mUserAvatar.setBackgroundResource(R.drawable.auth_avatar);

        // region: Account Field
        mAccountField.setFilters(
            new InputFilter[] { new AccountFilter(ACCOUNT_FILTER_ID, this), new InputFilter.LengthFilter(30) });
        mAccountField.requestFocus();
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
        final CheckableImageButton toggleButton = (CheckableImageButton) view.findViewById(R.id.userPwd_toggle_button);
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
        TextView nextButton = (TextView) view.findViewById(R.id.next_button);
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
        return mAccountField.getText().length() >= 3
            && UserInfoModel.isValidPassword(mPasswordField.getText());
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mAccountField.addTextChangedListener(mValidWatcher);
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
        dispatchOnDestroyView();
        mPasswordField.setOnEditorActionListener(null);
        mAccountField.removeTextChangedListener(mValidWatcher);
        mPasswordField.removeTextChangedListener(mValidWatcher);
        mValidWatcher = null;
        if (mUnBinder != null) {
            mUnBinder.unbind();
            mUnBinder = null;
        }
    }

    private void login() {

    }

    @Override
    public void onInvalidCharacter(int requestId, char c) {
        if (requestId == ACCOUNT_FILTER_ID || requestId == PWD_FILTER_ID) {
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
            mAccountField.setEnabled(enable);
            mPasswordLayout.setEnabled(enable);
        }
    }

    @Override
    public void onActionFailure(int action, @Nullable BaseResponse response, String message) {

    }

    @Override
    public void onActionSuccess(int action, @Nullable BaseResponse response) {

    }
}
