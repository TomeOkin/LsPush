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

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.tomeokin.lspush.R;

public class BaseDialogFragment extends DialogFragment
    implements DialogInterface.OnClickListener, AdapterView.OnItemClickListener {
    protected static final String KEY_HAS_CANCEL_LISTENER = "has_cancel_listener";
    protected static final String KEY_HAS_DISMISS_LISTENER = "has_dismiss_listener";
    protected static final String KEY_HAS_ACTION_CLICK_LISTENER = "has_action_click_listener";
    protected static final String KEY_HAS_LIST_CLICK_LISTENER = "has_list_item_click_listener";
    protected boolean hasCancelListener = false;
    protected boolean hasDismissListener = false;
    protected boolean hasActionClickListener = false;
    protected boolean hasListItemClickListener = false;

    public static final int DEFAULT_REQUEST_CODE = 124;
    protected int requestCode;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        init(savedInstanceState);
        int theme = resolveTheme();
        Builder builder = new Builder(getContext(), theme);
        builder = config(builder);
        builder = applyArgsSetting(builder);
        return builder.create();
    }

    private void init(Bundle bundle) {
        if (bundle != null) {
            hasCancelListener = bundle.getBoolean(KEY_HAS_CANCEL_LISTENER, hasCancelListener);
            hasDismissListener = bundle.getBoolean(KEY_HAS_DISMISS_LISTENER, hasDismissListener);
            hasActionClickListener = bundle.getBoolean(KEY_HAS_ACTION_CLICK_LISTENER, hasActionClickListener);
            hasListItemClickListener = bundle.getBoolean(KEY_HAS_LIST_CLICK_LISTENER, hasListItemClickListener);
        }
    }

    @NonNull
    protected Builder config(@NonNull Builder builder) {
        return builder;
    }

    protected Builder applyArgsSetting(Builder builder) {
        Bundle args = getArguments();
        if (args != null) {
            CharSequence field = args.getCharSequence(BaseDialogBuilder.EXTRA_DIALOG_TITLE, null);
            if (field != null) {
                builder.setTitle(field);
            }
            field = args.getCharSequence(BaseDialogBuilder.EXTRA_DIALOG_MESSAGE, null);
            if (field != null) {
                builder.setTitle(field);
            }

            // it also contain builder setting
            hasCancelListener = args.getBoolean(BaseDialogBuilder.EXTRA_ENABLE_CANCEL_LISTENING, hasCancelListener);
            hasDismissListener = args.getBoolean(BaseDialogBuilder.EXTRA_ENABLE_DISMISS_LISTENING, hasDismissListener);
            hasActionClickListener =
                args.getBoolean(BaseDialogBuilder.EXTRA_ENABLE_ACTION_CLICK_LISTENING, hasActionClickListener);

            DialogInterface.OnClickListener listener = hasActionClickListener ? this : null;
            field = args.getCharSequence(BaseDialogBuilder.EXTRA_DIALOG_NEUTRAL_TEXT, null);
            if (field != null) {
                builder.addNeutralButton(field, listener);
            }
            field = args.getCharSequence(BaseDialogBuilder.EXTRA_DIALOG_NEGATIVE_TEXT, null);
            if (field != null) {
                builder.addNegativeButton(field, listener);
            }
            field = args.getCharSequence(BaseDialogBuilder.EXTRA_DIALOG_POSITIVE_TEXT, null);
            if (field != null) {
                builder.addPositiveButton(field, listener);
            }

            CharSequence[] items = args.getCharSequenceArray(BaseDialogBuilder.EXTRA_DIALOG_LIST_ITEMS);
            if (items != null) {
                builder.setItems(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, items), this);
            }

            boolean flag = args.getBoolean(BaseDialogBuilder.EXTRA_DIALOG_CANCELABLE, true);
            builder.setCancelable(flag);
            flag = args.getBoolean(BaseDialogBuilder.EXTRA_DIALOG_CANCELED_ON_TOUCH_OUTSIDE, true);
            builder.setCanceledOnTouchOutside(flag);
        }
        return builder;
    }

    @StyleRes
    protected int resolveTheme() {
        int theme = 0;
        Bundle args = getArguments();
        if (args != null) {
            theme = args.getInt(BaseDialogBuilder.EXTRA_DIALOG_THEME, theme);
        }
        if (theme == 0) {
            theme = getTheme();
        }
        if (theme != 0) {
            return theme;
        } else {
            return R.style.Widget_LsPush_Dialog;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        requestCode = resolveRequestCode();
    }

    protected int resolveRequestCode() {
        int requestCode = DEFAULT_REQUEST_CODE;
        if (getTargetFragment() != null) {
            requestCode = getTargetRequestCode();
        } else if (getArguments() != null) {
            requestCode = getArguments().getInt(BaseDialogBuilder.EXTRA_REQUEST_CODE, requestCode);
        }
        return requestCode;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (hasCancelListener) {
            OnDialogCancelListener listener = getDialogListener(OnDialogCancelListener.class);
            if (listener != null) {
                listener.onDialogCancel(dialog, requestCode);
            } else {
                setResult(requestCode, Activity.RESULT_CANCELED, null);
            }
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (hasDismissListener) {
            OnDialogDismissListener listener = getDialogListener(OnDialogDismissListener.class);
            if (listener != null) {
                listener.onDialogDismiss(dialog, requestCode);
            } else {
                setResult(requestCode, Activity.RESULT_OK, null);
            }
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (hasActionClickListener) {
            OnActionClickListener listener = getDialogListener(OnActionClickListener.class);
            if (listener != null) {
                listener.onDialogActionClick(dialog, requestCode, which);
            } else {
                Intent data = new Intent();
                data.putExtra(BaseDialogBuilder.EXTRA_ACTION_TYPE, which);
                int resultCode =
                    which == DialogInterface.BUTTON_NEGATIVE ? Activity.RESULT_CANCELED : Activity.RESULT_OK;
                setResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (hasListItemClickListener) {
            OnListItemClickListener listener = getDialogListener(OnListItemClickListener.class);
            if (listener != null) {
                listener.onItemClick(getDialog(), requestCode, parent, view, position, id);
            } else {
                Intent data = new Intent();
                data.putExtra(BaseDialogBuilder.EXTRA_LIST_ITEM_CLICK_POSITION, position);
                setResult(requestCode, Activity.RESULT_OK, data);
            }

            ListView listView = (ListView) parent;
            if (listView.getChoiceMode() == AbsListView.CHOICE_MODE_NONE) {
                getDialog().dismiss();
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> T getDialogListener(Class<T> listenerInterface) {
        final Fragment targetFragment = getTargetFragment();
        if (targetFragment != null && listenerInterface.isAssignableFrom(targetFragment.getClass())) {
            return (T) targetFragment;
        } else if (getActivity() != null && listenerInterface.isAssignableFrom(getActivity().getClass())) {
            return (T) getActivity();
        }
        return null;
    }

    protected void setResult(int requestCode, int resultCode, Intent data) {
        final Fragment targetFragment = getTargetFragment();
        if (targetFragment != null) {
            targetFragment.onActivityResult(requestCode, resultCode, data);
        } else if (getActivity() != null) {
            if (data == null) {
                data = new Intent();
            }

            data.putExtra(BaseDialogBuilder.EXTRA_REQUEST_CODE, requestCode);
            getActivity().setResult(resultCode, data);
        }
    }

    public void showAllowingStateLoss(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_HAS_CANCEL_LISTENER, hasCancelListener);
        outState.putBoolean(KEY_HAS_DISMISS_LISTENER, hasDismissListener);
        outState.putBoolean(KEY_HAS_ACTION_CLICK_LISTENER, hasActionClickListener);
        outState.putBoolean(KEY_HAS_LIST_CLICK_LISTENER, hasListItemClickListener);
    }

    /**
     * provide it to config the default layout
     */
    protected class Builder {
        private final Context mContext;
        private final Dialog mDialog;
        private final FrameLayout mDialogTitleContainer;
        private final TextView mDialogTitle;
        private final View mDialogHeaderDivider;
        private final ScrollView mMessageScrollView;
        private final TextView mMessage;
        private final FrameLayout mCustomViewHolder;
        private final ListView mListView;
        private final FrameLayout mDialogFooterContainer;
        private final TextView mBlueButton;
        private final ViewStub mDialogTwoButtonStub;
        private final ViewStub mDialogThreeButtonStub;
        private CharSequence mNeutralText;
        private CharSequence mNegativeText;
        private CharSequence mPositiveText;
        private DialogInterface.OnClickListener mNeutralButtonListener;
        private DialogInterface.OnClickListener mNegativeButtonListener;
        private DialogInterface.OnClickListener mPositiveButtonListener;
        private boolean mCancelable = true;
        private boolean mCanceledOnTouchOutside = true;

        Builder(Context context, @StyleRes int theme) {
            mContext = context;
            mDialog = new Dialog(context, theme);
            mDialog.setContentView(R.layout.fragment_base_dialog);
            mDialogTitleContainer = (FrameLayout) mDialog.findViewById(R.id.dialog_titleContainer);
            mDialogTitle = (TextView) mDialog.findViewById(R.id.dialog_title);
            mDialogHeaderDivider = mDialog.findViewById(R.id.dialog_headerDivider);
            mMessageScrollView = (ScrollView) mDialog.findViewById(R.id.message_ScrollView);
            mMessage = (TextView) mDialog.findViewById(R.id.dialog_message);
            mCustomViewHolder = (FrameLayout) mDialog.findViewById(R.id.customViewHolder);
            mListView = (ListView) mDialog.findViewById(android.R.id.list);
            mDialogFooterContainer = (FrameLayout) mDialog.findViewById(R.id.dialog_footerContainer);
            mBlueButton = (TextView) mDialog.findViewById(R.id.button_blue);
            mDialogTwoButtonStub = (ViewStub) mDialog.findViewById(R.id.dialog_two_button_stub);
            mDialogThreeButtonStub = (ViewStub) mDialog.findViewById(R.id.dialog_three_button_stub);
        }

        public Builder setTitle(@StringRes int resId) {
            mDialogTitle.setText(resId);
            mDialogTitleContainer.setVisibility(View.VISIBLE);
            return this;
        }

        public Builder setTitle(CharSequence title) {
            mDialogTitle.setText(title);
            mDialogTitleContainer.setVisibility(View.VISIBLE);
            return this;
        }

        public TextView getTitleView() {
            return mDialogTitle;
        }

        public Builder setTitleView(View titleView) {
            mDialogTitleContainer.removeViewAt(0);
            mDialogTitleContainer.addView(titleView, 0);
            return this;
        }

        public Builder setMessage(@StringRes int resId) {
            mMessage.setText(resId);
            mMessageScrollView.setVisibility(View.VISIBLE);
            mDialogHeaderDivider.setVisibility(View.GONE);
            return this;
        }

        public Builder setMessage(CharSequence message) {
            mMessage.setText(message);
            mMessageScrollView.setVisibility(View.VISIBLE);
            mDialogHeaderDivider.setVisibility(View.GONE);
            return this;
        }

        public TextView getMessageView() {
            return mMessage;
        }

        public Builder addCustomMessageView(View view) {
            mCustomViewHolder.removeAllViews();
            mCustomViewHolder.addView(view);
            mCustomViewHolder.setVisibility(View.VISIBLE);
            return this;
        }

        public FrameLayout getCustomViewHolder() {
            return mCustomViewHolder;
        }

        public Builder setItems(ListAdapter listAdapter, final AdapterView.OnItemClickListener listener) {
            hasListItemClickListener = listener != null;
            mListView.setAdapter(listAdapter);
            mListView.setOnItemClickListener(listener);
            mListView.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
            mListView.setVisibility(View.VISIBLE);
            return this;
        }

        public Builder setItems(ListAdapter listAdapter, AdapterView.OnItemClickListener listener, int choiceMode) {
            hasListItemClickListener = listener != null;
            mListView.setAdapter(listAdapter);
            mListView.setOnItemClickListener(listener);
            mListView.setChoiceMode(choiceMode);
            mListView.setVisibility(View.VISIBLE);
            return this;
        }

        public ListView getListView() {
            return mListView;
        }

        public Builder addNeutralButton(@StringRes int resId, DialogInterface.OnClickListener listener) {
            mNeutralText = mContext.getResources().getText(resId);
            mNeutralButtonListener = listener;
            return this;
        }

        public Builder addNeutralButton(CharSequence message, DialogInterface.OnClickListener listener) {
            mNeutralText = message;
            mNeutralButtonListener = listener;
            return this;
        }

        public Builder addNegativeButton(@StringRes int resId, DialogInterface.OnClickListener listener) {
            mNegativeText = mContext.getResources().getText(resId);
            mNegativeButtonListener = listener;
            return this;
        }

        public Builder addNegativeButton(CharSequence message, DialogInterface.OnClickListener listener) {
            mNegativeText = message;
            mNegativeButtonListener = listener;
            return this;
        }

        public Builder addPositiveButton(@StringRes int resId, DialogInterface.OnClickListener listener) {
            mPositiveText = mContext.getResources().getText(resId);
            mPositiveButtonListener = listener;
            return this;
        }

        public Builder addPositiveButton(CharSequence message, DialogInterface.OnClickListener listener) {
            mPositiveText = message;
            mPositiveButtonListener = listener;
            return this;
        }

        public Builder setActionView(View view) {
            mDialogFooterContainer.removeViews(1, mDialogFooterContainer.getChildCount() - 1);
            mDialogFooterContainer.addView(view, 1);
            mDialogFooterContainer.setVisibility(View.VISIBLE);
            return this;
        }

        private void setOnClickListener(TextView textView, CharSequence text,
            final DialogInterface.OnClickListener listener, final int which) {
            hasActionClickListener = true;
            textView.setText(text);
            if (listener != null) {
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        listener.onClick(mDialog, which);
                    }
                });
            }
        }

        private void configActionView() {
            if (!TextUtils.isEmpty(mNeutralText)) {
                mDialogFooterContainer.setVisibility(View.VISIBLE);

                // three button version
                if (!TextUtils.isEmpty(mPositiveText) || !TextUtils.isEmpty(mNegativeText)) {
                    View view = mDialogThreeButtonStub.inflate();
                    Button neutralButton = (Button) view.findViewById(R.id.button_neutral);
                    Button negativeButton = (Button) view.findViewById(R.id.button_negative);
                    Button positiveButton = (Button) view.findViewById(R.id.button_positive);

                    setOnClickListener(neutralButton, mNeutralText, mNeutralButtonListener,
                        DialogInterface.BUTTON_NEUTRAL);
                    setOnClickListener(negativeButton, mNegativeText, mNegativeButtonListener,
                        DialogInterface.BUTTON_NEGATIVE);
                    setOnClickListener(positiveButton, mPositiveText, mPositiveButtonListener,
                        DialogInterface.BUTTON_POSITIVE);
                } else { // show blue button, as Positive Button, use mNeutralText
                    mBlueButton.setVisibility(View.VISIBLE);
                    setOnClickListener(mBlueButton, mNeutralText, mPositiveButtonListener,
                        DialogInterface.BUTTON_POSITIVE);
                }
            } else if (!TextUtils.isEmpty(mPositiveText) || !TextUtils.isEmpty(mNegativeText)) {
                mDialogFooterContainer.setVisibility(View.VISIBLE);

                // two button version
                View view = mDialogTwoButtonStub.inflate();
                TextView negativeButton = (TextView) view.findViewById(R.id.button_negative);
                TextView positiveButton = (TextView) view.findViewById(R.id.button_positive);

                setOnClickListener(negativeButton, mNegativeText, mNegativeButtonListener,
                    DialogInterface.BUTTON_NEGATIVE);
                setOnClickListener(positiveButton, mPositiveText, mPositiveButtonListener,
                    DialogInterface.BUTTON_POSITIVE);
            }
        }

        public final Builder setOnDismissListener(DialogInterface.OnDismissListener listener) {
            mDialog.setOnDismissListener(listener);
            hasDismissListener = true;
            return this;
        }

        public final Builder setCancelable(boolean flag) {
            mCancelable = flag;
            return this;
        }

        public final Builder setCanceledOnTouchOutside(boolean flag) {
            mCanceledOnTouchOutside = flag;
            return this;
        }

        public final Builder setOnCancelListener(DialogInterface.OnCancelListener listener) {
            mDialog.setOnCancelListener(listener);
            hasCancelListener = true;
            return this;
        }

        Dialog create() {
            // don't set custom action view
            if (mDialogFooterContainer.getChildCount() != 2) {
                configActionView();
            }

            setCancelable(mCancelable);
            mDialog.setCanceledOnTouchOutside(mCanceledOnTouchOutside);

            return mDialog;
        }
    }
}
