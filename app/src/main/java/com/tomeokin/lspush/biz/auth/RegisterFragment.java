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

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CheckableImageButton;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.auth.adapter.FieldAdapter;
import com.tomeokin.lspush.biz.auth.adapter.FilterCallback;
import com.tomeokin.lspush.biz.auth.adapter.NextButtonAdapter;
import com.tomeokin.lspush.biz.auth.adapter.PasswordFilter;
import com.tomeokin.lspush.biz.auth.adapter.UserIdFilter;
import com.tomeokin.lspush.biz.auth.usercase.CheckUIDAction;
import com.tomeokin.lspush.biz.auth.usercase.RegisterAction;
import com.tomeokin.lspush.biz.auth.usercase.UpdateLocalUserInfoAction;
import com.tomeokin.lspush.biz.auth.usercase.UploadAvatarAction;
import com.tomeokin.lspush.biz.base.BaseActionCallback;
import com.tomeokin.lspush.biz.base.BaseFragment;
import com.tomeokin.lspush.biz.base.BaseStateAdapter;
import com.tomeokin.lspush.biz.base.BaseStateCallback;
import com.tomeokin.lspush.biz.base.BaseTextWatcher;
import com.tomeokin.lspush.biz.common.UserScene;
import com.tomeokin.lspush.biz.main.MainActivity;
import com.tomeokin.lspush.biz.model.UserInfoModel;
import com.tomeokin.lspush.common.FileNameUtils;
import com.tomeokin.lspush.common.ImageIntentUtils;
import com.tomeokin.lspush.common.MimeTypeUtils;
import com.tomeokin.lspush.common.Navigator;
import com.tomeokin.lspush.common.SoftInputUtils;
import com.tomeokin.lspush.common.StringUtils;
import com.tomeokin.lspush.data.model.AccessResponse;
import com.tomeokin.lspush.data.model.BaseResponse;
import com.tomeokin.lspush.data.model.CaptchaRequest;
import com.tomeokin.lspush.data.model.RegisterData;
import com.tomeokin.lspush.data.model.UploadResponse;
import com.tomeokin.lspush.data.model.User;
import com.tomeokin.lspush.injection.component.AuthComponent;
import com.tomeokin.lspush.ui.glide.CircleTransform;
import com.tomeokin.lspush.ui.widget.NotificationBar;
import com.tomeokin.lspush.ui.widget.dialog.BaseDialogFragment;
import com.tomeokin.lspush.ui.widget.dialog.OnListItemClickListener;
import com.tomeokin.lspush.ui.widget.dialog.SimpleDialogBuilder;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

public class RegisterFragment extends BaseFragment
    implements BaseActionCallback, FilterCallback, BaseStateCallback, OnListItemClickListener,
    EasyPermissions.PermissionCallbacks {
    public static final int NEXT_BUTTON_ID = 0;
    public static final int UID_FILTER_ID = 1;
    public static final int PWD_FILTER_ID = 2;
    public static final int UID_ADAPTER_ID = 3;
    public static final int USER_NAME_ADAPTER_ID = 4;

    private static final int REQUEST_PERMISSION_PICK_IMAGE = 101;
    private static final int REQUEST_PERMISSION_TAKE_PHOTO = 102;

    private static final int REQUEST_SELECT_IMAGE_SOURCE = 0;
    private static final int REQUEST_PICK_IMAGE = 1;
    private static final int REQUEST_TAKE_PHOTO = 2;

    private static String[] SELECT_IMAGE_SOURCE;

    public static final String EXTRA_CAPTCHA_REQUEST = "extra.captcha.request";
    public static final String EXTRA_CAPTCHA_AUTH_CODE = "extra.captcha.auth.code";

    private CaptchaRequest mCaptchaRequest = null;
    private String mAuthCode;
    private String mUserAvatarImage = null;
    private RegisterData mRegisterData;

    private ImageView mUserAvatar;
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

    private File mTakePhotoFile = null;
    private File mUserAvatarFile = null;
    private BaseDialogFragment mBaseDialogFragment;

    //@Inject RegisterPresenter mPresenter;
    @Inject CheckUIDAction mCheckUIDAction;
    @Inject UploadAvatarAction mUploadAvatarAction;
    @Inject RegisterAction mRegisterAction;
    @Inject UpdateLocalUserInfoAction mUpdateLocalUserInfoAction;

    public static Bundle prepareArgument(CaptchaRequest captchaRequest, String authCode) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_CAPTCHA_REQUEST, captchaRequest);
        bundle.putString(EXTRA_CAPTCHA_AUTH_CODE, authCode);
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments() != null && getArguments().containsKey(EXTRA_CAPTCHA_REQUEST)) {
            mCaptchaRequest = getArguments().getParcelable(EXTRA_CAPTCHA_REQUEST);
            mAuthCode = getArguments().getString(EXTRA_CAPTCHA_AUTH_CODE);
        }

        // we need to confirm the order, so don't use array resource
        SELECT_IMAGE_SOURCE = new String[] {
            getString(R.string.select_from_gallery), // position = 0
            getString(R.string.select_from_camera) // position = 1
        };

        component(AuthComponent.class).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.auth_container, container, false);
        inflater.inflate(R.layout.fragment_register, (ViewGroup) view.findViewById(R.id.content_container), true);
        mUserAvatar = (ImageView) view.findViewById(R.id.image_icon);
        mUserAvatar.setBackgroundResource(R.drawable.register_name);
        mUserAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaseDialogFragment =
                    new SimpleDialogBuilder(RegisterFragment.this).setTitle(getString(R.string.select_image_source))
                        .setListItem(SELECT_IMAGE_SOURCE)
                        .setTargetFragment(RegisterFragment.this, REQUEST_SELECT_IMAGE_SOURCE)
                        .show();
            }
        });

        mValidWatcher = new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s == mUserIdField.getText()) {
                    // 如果用户修改了文本，此时如果处于加载状态，取消该状态，并进行状态同步，
                    // 同时取消已进行的网络请求
                    if (mUIDAdapter.getState() == FieldAdapter.WAITING) {
                        mCheckUIDAction.cancel(UserScene.ACTION_CHECK_UID);
                    } else if (mUIDAdapter.getState() == FieldAdapter.INFO) {
                        mUIDAdapter.active();
                    }
                    mUIDAdapter.syncRevokeWaiting();
                } else if (s == mUserNameField.getText()) {
                    mUserNameAdapter.sync();
                }
                mNextButtonAdapter.sync();
            }
        };
        mFocusChangeValidChecker = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v.getId() == R.id.userId_field) {
                    if (!hasFocus
                        && mUIDAdapter.getState() != FieldAdapter.WAITING
                        && mUIDAdapter.getState() != FieldAdapter.INFO
                        && isValidUserId()) {
                        mUIDAdapter.waiting();
                        mCheckUIDAction.checkUIDExist(mUserIdField.getText().toString());
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
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT && isFieldValid()) {
                    register();
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

        TextView nextButton = (TextView) view.findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
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
    public void onItemClick(DialogInterface dialog, int requestCode, AdapterView<?> parent, View view, int position,
        long id) {
        if (requestCode == REQUEST_SELECT_IMAGE_SOURCE) {
            if (position == 0) {
                pickImageTask();
            } else if (position == 1) {
                takePhotoTask();
            }
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_PICK_IMAGE)
    private void pickImageTask() {
        if (EasyPermissions.hasPermissions(getContext(), ImageIntentUtils.PERMISSION_PICK_IMAGE)) {
            Intent intent = ImageIntentUtils.createSelectJPEGIntent();
            startActivityForResult(Intent.createChooser(intent, getText(R.string.pick_image)), REQUEST_PICK_IMAGE);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.pick_image_rationale),
                REQUEST_PERMISSION_PICK_IMAGE, needPermissions(ImageIntentUtils.PERMISSION_PICK_IMAGE));
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_TAKE_PHOTO)
    private void takePhotoTask() {
        if (EasyPermissions.hasPermissions(getContext(), ImageIntentUtils.PERMISSION_TAKE_PHOTO)) {
            mTakePhotoFile = FileNameUtils.getJPEGFile(getContext());
            Intent intent = ImageIntentUtils.createTakePhotoIntent(mTakePhotoFile);
            boolean canTakePhoto = intent.resolveActivity(getContext().getPackageManager()) != null;
            if (!canTakePhoto) {
                mNotificationBar.showTemporaryInverse(getString(R.string.no_suitable_camera));
                return;
            }

            // TODO: 2016/9/14 some camera don't support setting face with intent, later may change to custom control version.
            startActivityForResult(intent, REQUEST_TAKE_PHOTO);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.take_photo_rationale),
                REQUEST_PERMISSION_TAKE_PHOTO, needPermissions(ImageIntentUtils.PERMISSION_TAKE_PHOTO));
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Timber.i("permission granted %d", requestCode);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Timber.i("permission denied %d", requestCode);

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this, getString(R.string.rationale_ask_again)).setTitle(
                getString(R.string.settings_dialog_title))
                .setPositiveButton(getString(R.string.setting))
                .setNegativeButton(getString(R.string.dialog_cancel), null /* click listener */)
                .build()
                .show();
        }
    }

    // 根据 uri 裁剪图片
    private void cropImage(@NonNull Uri input) {
        Timber.i("Uri.toString: %s", input.toString());
        if (!MimeTypeUtils.isImage(getContext(), input)) {
            mNotificationBar.showTemporaryInverse(getString(R.string.not_image_file));
            return;
        }

        mUserAvatarFile = FileNameUtils.getJPEGFile(getContext());
        CropImage.activity(input)
            .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
            .setOutputUri(Uri.fromFile(mUserAvatarFile))
            .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
            .setMinCropResultSize(450, 450)
            .setRequestedSize(512, 512)
            .setMaxCropResultSize(768, 768)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(getContext(), this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mUserIdField.addTextChangedListener(mValidWatcher);
        mUserIdField.requestFocus();
        mUserNameField.addTextChangedListener(mValidWatcher);
        mPasswordField.addTextChangedListener(mValidWatcher);
        dispatchOnResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK && requestCode != CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            Timber.i("request-code-failure %d, result: %d", requestCode, resultCode);
            return;
        }
        if (requestCode == REQUEST_PICK_IMAGE) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
            }

            if (uri != null && uri.toString().length() != 0) {
                cropImage(uri);
            } else {
                mNotificationBar.showTemporaryInverse(getString(R.string.could_not_access_image));
            }
        } else if (requestCode == REQUEST_TAKE_PHOTO) {
            if (mTakePhotoFile != null && mTakePhotoFile.exists()) {
                Uri uri = Uri.fromFile(mTakePhotoFile);
                cropImage(uri);
            } else {
                mNotificationBar.showTemporaryInverse(getString(R.string.could_not_access_image));
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                if (mUserAvatarFile == null || !mUserAvatarFile.exists()) {
                    mUserAvatarFile = new File(result.getUri().toString());
                }
                if (mUserAvatarFile == null) {
                    mNotificationBar.showTemporaryInverse(getString(R.string.could_not_access_image));
                    return;
                }

                Timber.i("crop-image result uri: %s", mUserAvatarFile.getAbsolutePath());
                Glide.with(getContext()).load(mUserAvatarFile).diskCacheStrategy(DiskCacheStrategy.ALL)
                    // when using transform or bitmapTransform, don't use fitCenter() or centerCrop()
                    .transform(new CircleTransform(getContext())).override(450, 450).into(mUserAvatar);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                mNotificationBar.showTemporaryInverse(getString(R.string.could_not_access_image));
                Timber.i(result.getError(), "crop image failure");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCheckUIDAction.attach(this);
        mUploadAvatarAction.attach(this);
        mRegisterAction.attach(this);
        mUpdateLocalUserInfoAction.attach(this);
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
        mCheckUIDAction.detach();
        mCheckUIDAction = null;
        mUploadAvatarAction.detach();
        mUploadAvatarAction = null;
        mRegisterAction.detach();
        mRegisterAction = null;
        mUpdateLocalUserInfoAction.detach();
        mUpdateLocalUserInfoAction = null;

        if (mBaseDialogFragment != null) {
            mBaseDialogFragment.dismiss();
            mBaseDialogFragment = null;
        }
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
        mUserAvatar = null;
        unregister(mUIDAdapter);
        unregister(mUserNameAdapter);
        unregister(mNextButtonAdapter);
        mUIDAdapter = null;
        mUserNameAdapter = null;
        mNextButtonAdapter = null;
        dispatchOnDestroyView();
    }

    public void register() {
        mNextButtonAdapter.waiting();
        if (mUserAvatarFile != null && TextUtils.isEmpty(mUserAvatarImage)) {
            mUploadAvatarAction.upload(mUserAvatarFile);
            return;
        }

        if (mRegisterData == null) {
            mRegisterData = new RegisterData();
        }
        mRegisterData.setCaptchaRequest(mCaptchaRequest);
        mRegisterData.setAuthCode(mAuthCode);
        mRegisterData.setUserId(mUserIdField.getText().toString());
        mRegisterData.setNickname(mUserNameField.getText().toString());
        mRegisterData.setPassword(mPasswordField.getText().toString());
        mRegisterData.setUserAvatar(mUserAvatarImage);
        mRegisterAction.register(mRegisterData);
    }

    public boolean isValidUserId() {
        return mUserIdField.getText().toString().trim().length() >= UserInfoModel.USER_ID_MIN_LENGTH
            && mUIDAdapter.getState() != FieldAdapter.INFO;
    }

    public boolean isValidUserName() {
        return mUserNameField.getText().toString().trim().length() >= UserInfoModel.USER_NAME_MIN_LENGTH;
    }

    public boolean isValidPassword() {
        return mPasswordField.getText().toString().trim().length() >= UserInfoModel.USER_PASSWORD_MIN_LENGTH
            && quickFallPasswordStrength(mPasswordField.getText());
    }

    public boolean quickFallPasswordStrength(CharSequence password) {
        int result = StringUtils.indexDigest(password) >= 0 ? 1 : 0;
        result += StringUtils.indexLowerLetter(password) >= 0 ? 1 : 0;
        if (result == 2) {
            return true;
        }
        result += StringUtils.indexUpperLetter(password) >= 0 ? 1 : 0;
        if (result >= 2) {
            return true;
        }
        result += StringUtils.indexSpecial(UserInfoModel.PASSWORD_SPECIAL_SORT, password);
        return result >= 2;
    }

    public boolean isFieldValid() {
        return isValidUserId() && isValidUserName() && isValidPassword();
    }

    @Override
    public void onInvalidCharacter(int requestId, char c) {
        if (requestId == UID_FILTER_ID || requestId == PWD_FILTER_ID) {
            mNotificationBar.showTemporaryInverse(getString(R.string.not_support_character, c));
        }
    }

    @Override
    public boolean isActive(BaseStateAdapter adapter, int requestId) {
        if (requestId == UID_ADAPTER_ID) {
            return isValidUserId();
        } else if (requestId == USER_NAME_ADAPTER_ID) {
            return isValidUserName();
        } else if (requestId == NEXT_BUTTON_ID) {
            return isFieldValid();
        }
        return false;
    }

    @Override
    public void onStateChange(BaseStateAdapter adapter, int requestId, int currentState) {
        if (requestId == NEXT_BUTTON_ID) {
            final boolean enable = currentState != NextButtonAdapter.WAITING;
            mUserAvatar.setEnabled(enable);
            mUserIdFieldLayout.setEnabled(enable);
            mUserNameFieldLayout.setEnabled(enable);
            mPasswordFieldLayout.setEnabled(enable);
        }
    }

    @Override
    public void onActionFailure(int action, @Nullable BaseResponse response, String message) {
        if (action == UserScene.ACTION_CHECK_UID) {
            if (response != null && response.getResultCode() == 10) {
                mUIDAdapter.info();
                mNotificationBar.showTemporaryInverse(getString(R.string.uid_not_unique));
            } else {
                mNotificationBar.showTemporaryInverse(message);
            }
        } else if (action == UserScene.ACTION_REGISTER) {
            mNextButtonAdapter.syncRevokeWaiting();
            mNotificationBar.showTemporaryInverse(message);
        } else if (action == UserScene.ACTION_UPLOAD) {
            mNextButtonAdapter.syncRevokeWaiting();
            mNotificationBar.showTemporaryInverse(message);
        }
    }

    @Override
    public void onActionSuccess(int action, @Nullable BaseResponse response) {
        if (action == UserScene.ACTION_CHECK_UID) {
            mUIDAdapter.active();
        } else if (action == UserScene.ACTION_REGISTER) {
            AccessResponse res = (AccessResponse) response;
            if (res != null) {
                User user = new User();
                user.setUid(res.getUserId());
                user.setNickname(mRegisterData.getNickname());
                if (mRegisterData.getCaptchaRequest().getSendObject().contains("@")) {
                    user.setEmail(mRegisterData.getCaptchaRequest().getSendObject());
                } else {
                    user.setPhone(mRegisterData.getCaptchaRequest().getSendObject());
                    user.setRegion(mRegisterData.getCaptchaRequest().getRegion());
                }
                user.setImage(mRegisterData.getUserAvatar());
                user.setPassword(mRegisterData.getPassword());
                mUpdateLocalUserInfoAction.updateUserInfo(res, user);
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
            Timber.i("register success");
            mNextButtonAdapter.syncRevokeWaiting();
        } else if (action == UserScene.ACTION_UPLOAD) {
            UploadResponse res = (UploadResponse) response;
            if (res != null) {
                mUserAvatarImage = res.getFilename();
                register();
            } else {
                Timber.w("unexpected problem");
                mNextButtonAdapter.syncRevokeWaiting();
            }
        }
    }
}
