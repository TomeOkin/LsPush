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
package com.tomeokin.lspush.ui.widget.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * focus on the interaction functions among dialog. Not request to use.
 */
public class BaseDialogBuilder<T extends BaseDialogFragment> {
    public static final String EXTRA_REQUEST_CODE = "dialog.request.code";
    public static final String EXTRA_ACTION_TYPE = "dialog.action.type";
    public static final String EXTRA_LIST_ITEM_CLICK_POSITION = "dialog.list.item.click.position";

    public static final String EXTRA_DIALOG_THEME = "dialog.theme";
    public static final String EXTRA_ENABLE_CANCEL_LISTENING = "dialog.enable.cancel.listening";
    public static final String EXTRA_ENABLE_DISMISS_LISTENING = "dialog.enable.dismiss.listening";
    public static final String EXTRA_ENABLE_ACTION_CLICK_LISTENING = "dialog.enable.action.click.listening";
    public static final String EXTRA_ENABLE_LIST_ITEM_CLICK_LISTENING = "dialog.enable.list.item.click.listening";
    public static final String EXTRA_DIALOG_TITLE = "dialog.title";
    public static final String EXTRA_DIALOG_MESSAGE = "dialog.message";
    public static final String EXTRA_DIALOG_NEUTRAL_TEXT = "dialog.neutral.text";
    public static final String EXTRA_DIALOG_NEGATIVE_TEXT = "dialog.negative.text";
    public static final String EXTRA_DIALOG_POSITIVE_TEXT = "dialog.positive.text";
    public static final String EXTRA_DIALOG_CANCELABLE = "dialog.cancelable";
    public static final String EXTRA_DIALOG_CANCELED_ON_TOUCH_OUTSIDE = "dialog.canceled.on.touch.outside";

    protected final Context mContext;
    protected final FragmentManager mFragmentManager;
    protected final Class<? extends BaseDialogFragment> mClazz;
    private Fragment mTargetFragment;
    private int mRequestCode;
    private String mTag;
    protected Bundle mArgs;

    public BaseDialogBuilder(Context context, FragmentManager fragmentManager,
        Class<? extends BaseDialogFragment> clazz) {
        mArgs = new Bundle();
        mContext = context;
        mFragmentManager = fragmentManager;
        mClazz = clazz;
    }

    @NonNull protected Bundle prepareArguments(Bundle args) {
        return args;
    }

    public BaseDialogBuilder setTag(String tag) {
        mTag = tag;
        return this;
    }

    public BaseDialogBuilder setTargetFragment(Fragment fragment, int requestCode) {
        mRequestCode = requestCode;
        mTargetFragment = fragment;
        return this;
    }

    public BaseDialogBuilder setRequestCode(int requestCode) {
        mArgs.putInt(EXTRA_REQUEST_CODE, requestCode);
        return this;
    }

    public BaseDialogBuilder setTheme(@StyleRes int themeId) {
        mArgs.putInt(EXTRA_DIALOG_THEME, themeId);
        return this;
    }

    public BaseDialogBuilder setCancelListeningEnable(boolean flag) {
        mArgs.putBoolean(EXTRA_ENABLE_CANCEL_LISTENING, flag);
        return this;
    }

    public BaseDialogBuilder setDismissListeningEnable(boolean flag) {
        mArgs.putBoolean(EXTRA_ENABLE_DISMISS_LISTENING, flag);
        return this;
    }

    public BaseDialogBuilder setActionClickListeningEnable(boolean flag) {
        mArgs.putBoolean(EXTRA_ENABLE_ACTION_CLICK_LISTENING, flag);
        return this;
    }

    public BaseDialogBuilder setEnableListItemSelectListening(boolean flag) {
        mArgs.putBoolean(EXTRA_ENABLE_LIST_ITEM_CLICK_LISTENING, flag);
        return this;
    }

    public BaseDialogBuilder setTitle(CharSequence title) {
        mArgs.putCharSequence(EXTRA_DIALOG_TITLE, title);
        return this;
    }

    public BaseDialogBuilder setTitle(@StringRes int resId) {
        mArgs.putCharSequence(EXTRA_DIALOG_TITLE, mContext.getResources().getString(resId));
        return this;
    }

    public BaseDialogBuilder setMessage(CharSequence message) {
        mArgs.putCharSequence(EXTRA_DIALOG_MESSAGE, message);
        return this;
    }

    public BaseDialogBuilder setMessage(@StringRes int resId) {
        mArgs.putCharSequence(EXTRA_DIALOG_MESSAGE, mContext.getResources().getString(resId));
        return this;
    }

    public BaseDialogBuilder setNeutralText(CharSequence neutralText) {
        mArgs.putCharSequence(EXTRA_DIALOG_NEUTRAL_TEXT, neutralText);
        return this;
    }

    public BaseDialogBuilder setNeutralText(@StringRes int resId) {
        mArgs.putCharSequence(EXTRA_DIALOG_NEUTRAL_TEXT, mContext.getResources().getString(resId));
        return this;
    }

    public BaseDialogBuilder setNegativeText(CharSequence negativeText) {
        mArgs.putCharSequence(EXTRA_DIALOG_NEGATIVE_TEXT, negativeText);
        return this;
    }

    public BaseDialogBuilder setNegativeText(@StringRes int resId) {
        mArgs.putCharSequence(EXTRA_DIALOG_NEGATIVE_TEXT, mContext.getResources().getString(resId));
        return this;
    }

    public BaseDialogBuilder setPositiveText(CharSequence positiveText) {
        mArgs.putCharSequence(EXTRA_DIALOG_POSITIVE_TEXT, positiveText);
        return this;
    }

    public BaseDialogBuilder setPositiveText(@StringRes int resId) {
        mArgs.putCharSequence(EXTRA_DIALOG_POSITIVE_TEXT, mContext.getResources().getString(resId));
        return this;
    }

    public BaseDialogBuilder setCancelable(boolean flag) {
        mArgs.putBoolean(EXTRA_DIALOG_CANCELABLE, flag);
        return this;
    }

    public BaseDialogBuilder setCanceledOnTouchOutside(boolean flag) {
        mArgs.putBoolean(EXTRA_DIALOG_CANCELED_ON_TOUCH_OUTSIDE, flag);
        return this;
    }

    @SuppressWarnings("unchecked") private T create() {
        final Bundle args = prepareArguments(mArgs);
        final T fragment = (T) Fragment.instantiate(mContext, mClazz.getName(), args);

        if (mTargetFragment != null) {
            fragment.setTargetFragment(mTargetFragment, mRequestCode);
        }

        return fragment;
    }

    public T show() {
        T fragment = create();
        fragment.show(mFragmentManager, mTag);
        return fragment;
    }

    /**
     * Like show() but allows the commit to be executed after an activity's state is saved. This
     * is dangerous because the commit can be lost if the activity needs to later be restored from
     * its state, so this should only be used for cases where it is okay for the UI state to change
     * unexpectedly on the user.
     */
    public T showAllowingStateLoss() {
        T fragment = create();
        fragment.showAllowingStateLoss(mFragmentManager, mTag);
        return fragment;
    }
}
