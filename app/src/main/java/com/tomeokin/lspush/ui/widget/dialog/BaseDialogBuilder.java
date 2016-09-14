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
public abstract class BaseDialogBuilder<T extends BaseDialogBuilder<T, F>, F extends BaseDialogFragment> {
    public static final String EXTRA_REQUEST_CODE = "dialog.request.code";
    public static final String EXTRA_ACTION_TYPE = "dialog.action.type";
    public static final String EXTRA_LIST_ITEM_CLICK_POSITION = "dialog.list.item.click.position";

    public static final String EXTRA_DIALOG_THEME = "dialog.theme";
    public static final String EXTRA_ENABLE_CANCEL_LISTENING = "dialog.enable.cancel.listening";
    public static final String EXTRA_ENABLE_DISMISS_LISTENING = "dialog.enable.dismiss.listening";
    public static final String EXTRA_ENABLE_ACTION_CLICK_LISTENING = "dialog.enable.action.click.listening";
    public static final String EXTRA_DIALOG_TITLE = "dialog.title";
    public static final String EXTRA_DIALOG_MESSAGE = "dialog.message";
    public static final String EXTRA_DIALOG_NEUTRAL_TEXT = "dialog.neutral.text";
    public static final String EXTRA_DIALOG_NEGATIVE_TEXT = "dialog.negative.text";
    public static final String EXTRA_DIALOG_POSITIVE_TEXT = "dialog.positive.text";
    public static final String EXTRA_DIALOG_CANCELABLE = "dialog.cancelable";
    public static final String EXTRA_DIALOG_CANCELED_ON_TOUCH_OUTSIDE = "dialog.canceled.on.touch.outside";
    public static final String EXTRA_DIALOG_LIST_ITEMS = "dialog.list.items";

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

    protected abstract T self();

    @NonNull protected Bundle prepareArguments(Bundle args) {
        return args;
    }

    public T setTag(String tag) {
        mTag = tag;
        return self();
    }

    public T setTargetFragment(Fragment fragment, int requestCode) {
        mRequestCode = requestCode;
        mTargetFragment = fragment;
        return self();
    }

    public T setRequestCode(int requestCode) {
        mArgs.putInt(EXTRA_REQUEST_CODE, requestCode);
        return self();
    }

    public T setTheme(@StyleRes int themeId) {
        mArgs.putInt(EXTRA_DIALOG_THEME, themeId);
        return self();
    }

    public T setCancelListeningEnable(boolean flag) {
        mArgs.putBoolean(EXTRA_ENABLE_CANCEL_LISTENING, flag);
        return self();
    }

    public T setDismissListeningEnable(boolean flag) {
        mArgs.putBoolean(EXTRA_ENABLE_DISMISS_LISTENING, flag);
        return self();
    }

    public T setActionClickListeningEnable(boolean flag) {
        mArgs.putBoolean(EXTRA_ENABLE_ACTION_CLICK_LISTENING, flag);
        return self();
    }

    public T setListItem(String[] items) {
        mArgs.putCharSequenceArray(EXTRA_DIALOG_LIST_ITEMS, items);
        return self();
    }

    public T setTitle(CharSequence title) {
        mArgs.putCharSequence(EXTRA_DIALOG_TITLE, title);
        return self();
    }

    public T setTitle(@StringRes int resId) {
        mArgs.putCharSequence(EXTRA_DIALOG_TITLE, mContext.getResources().getString(resId));
        return self();
    }

    public T setMessage(CharSequence message) {
        mArgs.putCharSequence(EXTRA_DIALOG_MESSAGE, message);
        return self();
    }

    public T setMessage(@StringRes int resId) {
        mArgs.putCharSequence(EXTRA_DIALOG_MESSAGE, mContext.getResources().getString(resId));
        return self();
    }

    public T setNeutralText(CharSequence neutralText) {
        mArgs.putCharSequence(EXTRA_DIALOG_NEUTRAL_TEXT, neutralText);
        return self();
    }

    public T setNeutralText(@StringRes int resId) {
        mArgs.putCharSequence(EXTRA_DIALOG_NEUTRAL_TEXT, mContext.getResources().getString(resId));
        return self();
    }

    public T setNegativeText(CharSequence negativeText) {
        mArgs.putCharSequence(EXTRA_DIALOG_NEGATIVE_TEXT, negativeText);
        return self();
    }

    public T setNegativeText(@StringRes int resId) {
        mArgs.putCharSequence(EXTRA_DIALOG_NEGATIVE_TEXT, mContext.getResources().getString(resId));
        return self();
    }

    public T setPositiveText(CharSequence positiveText) {
        mArgs.putCharSequence(EXTRA_DIALOG_POSITIVE_TEXT, positiveText);
        return self();
    }

    public T setPositiveText(@StringRes int resId) {
        mArgs.putCharSequence(EXTRA_DIALOG_POSITIVE_TEXT, mContext.getResources().getString(resId));
        return self();
    }

    public T setCancelable(boolean flag) {
        mArgs.putBoolean(EXTRA_DIALOG_CANCELABLE, flag);
        return self();
    }

    public T setCanceledOnTouchOutside(boolean flag) {
        mArgs.putBoolean(EXTRA_DIALOG_CANCELED_ON_TOUCH_OUTSIDE, flag);
        return self();
    }

    @SuppressWarnings("unchecked") private F create() {
        final Bundle args = prepareArguments(mArgs);
        final F fragment = (F) Fragment.instantiate(mContext, mClazz.getName(), args);

        if (mTargetFragment != null) {
            fragment.setTargetFragment(mTargetFragment, mRequestCode);
        }

        return fragment;
    }

    public F show() {
        F fragment = create();
        fragment.show(mFragmentManager, mTag);
        return fragment;
    }

    /**
     * Like show() but allows the commit to be executed after an activity's state is saved. This
     * is dangerous because the commit can be lost if the activity needs to later be restored from
     * its state, so this should only be used for cases where it is okay for the UI state to change
     * unexpectedly on the user.
     */
    public F showAllowingStateLoss() {
        F fragment = create();
        fragment.showAllowingStateLoss(mFragmentManager, mTag);
        return fragment;
    }
}
