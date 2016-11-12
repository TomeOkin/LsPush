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
package com.tomeokin.lspush.biz.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.base.support.BaseActionCallback;
import com.tomeokin.lspush.biz.base.support.BaseStateAdapter;
import com.tomeokin.lspush.biz.common.UserScene;
import com.tomeokin.lspush.biz.usercase.collection.LinkAction;
import com.tomeokin.lspush.util.ClipboardUtils;
import com.tomeokin.lspush.util.SoftInputUtils;
import com.tomeokin.lspush.data.model.BaseResponse;
import com.tomeokin.lspush.data.model.Collection;
import com.tomeokin.lspush.data.model.UrlCollectionResponse;
import com.tomeokin.lspush.data.model.WebPageInfo;
import com.tomeokin.lspush.injection.ProvideComponent;
import com.tomeokin.lspush.injection.component.HomeComponent;
import com.tomeokin.lspush.ui.widget.dialog.BaseDialogBuilder;
import com.tomeokin.lspush.ui.widget.dialog.BaseDialogFragment;
import com.tomeokin.lspush.ui.widget.listener.TextWatcherAdapter;

import javax.inject.Inject;

import timber.log.Timber;

public class UriDialogFragment extends BaseDialogFragment implements BaseActionCallback {
    public static final String REQUEST_RESULT_URL = "request.result.url";

    private TextView mNextButton;
    private int mNextState;
    private ProgressBar mProgressBar;
    private TextWatcher mUrlWatcher = new TextWatcherAdapter() {
        @Override
        public void afterTextChanged(Editable s) {
            if (isValidWebUri(s.toString())) {
                activeNextButton();
            } else {
                disableNextButton();
            }
        }
    };
    private EditText mUrlField;

    @Inject LinkAction mLinkAction;

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    protected BaseDialogFragment.Builder config(@NonNull BaseDialogFragment.Builder builder) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_link, null);
        builder.addCustomMessageView(view);

        final View content = getActivity().getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        final FrameLayout container = builder.getCustomViewHolder();
        ViewGroup.LayoutParams lp = container.getLayoutParams();
        lp.width = content.getWidth() / 6 * 5;
        container.setLayoutParams(lp);

        mUrlField = (EditText) view.findViewById(R.id.url_field);

        mNextButton = (TextView) view.findViewById(R.id.next_button);
        mNextButton.setTextColor(Color.BLACK);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUrlInfo();
            }
        });
        mNextButton.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT && isAvailableCheck() && isValidWebUri(
                    mUrlField.getText())) {
                    getUrlInfo();
                    return true;
                }

                return false;
            }
        });
        Drawable clear = getContext().getDrawable(R.drawable.search_clear);
        DrawableCompat.setTint(clear, ContextCompat.getColor(getContext(), R.color.blue_5_whiteout));
        mNextButton.setCompoundDrawables(null, null, clear, null);
        mProgressBar = (ProgressBar) view.findViewById(R.id.next_progress);

        final String text = ClipboardUtils.getText(getContext());
        if (!TextUtils.isEmpty(text) && isValidWebUri(text)) {
            mUrlField.setText(text);
            activeNextButton();
        } else {
            disableNextButton();
        }

        return builder;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        if (getActivity() != null && getActivity() instanceof HomeActivity) {
            Timber.v("instance of home activity");
            component(HomeComponent.class).inject(this);
            mLinkAction.attach(this);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * Gets a component for dependency injection by its type.
     */
    @SuppressWarnings("unchecked")
    protected <C> C component(Class<C> componentType) {
        return componentType.cast(((ProvideComponent<C>) getActivity()).component());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mLinkAction.detach();
    }

    public boolean isValidWebUri(CharSequence url) {
        return Patterns.WEB_URL.matcher(url).matches();
    }

    private void activeNextButton() {
        mNextState = BaseStateAdapter.ACTIVE;
        mNextButton.setText(getString(R.string.next));
        mNextButton.setTextColor(ContextCompat.getColor(getContext(), R.color.black_87_transparent));
        mNextButton.setEnabled(true);
        mProgressBar.setVisibility(View.GONE);
    }

    private void disableNextButton() {
        mNextState = BaseStateAdapter.DISABLE;
        mNextButton.setText(getString(R.string.next));
        mNextButton.setTextColor(ContextCompat.getColor(getContext(), R.color.black_25_transparent));
        mNextButton.setEnabled(false);
        mProgressBar.setVisibility(View.GONE);
    }

    private void waitingNextButton() {
        mNextState = BaseStateAdapter.WAITING;
        mNextButton.setText("");
        mNextButton.setEnabled(false);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private boolean isAvailableCheck() {
        return mNextState != BaseStateAdapter.WAITING && mNextState != BaseStateAdapter.INFO;
    }

    private void getUrlInfo() {
        mUrlField.setEnabled(false);
        waitingNextButton();
        mLinkAction.getUrlInfo(mUrlField.getText().toString());
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mUrlField.requestFocus();
        SoftInputUtils.showInput(mUrlField);
        mUrlField.addTextChangedListener(mUrlWatcher);
    }

    @Override
    public void onPause() {
        super.onPause();
        SoftInputUtils.hideInput(mUrlField);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
        mUrlField.removeTextChangedListener(mUrlWatcher);
    }

    @Override
    public void onActionSuccess(int action, @Nullable BaseResponse response) {
        if (action == UserScene.ACTION_GET_URL_INFO) {
            UrlCollectionResponse target = (UrlCollectionResponse) response;
            setUrlInfoResult(target == null ? null : target.getCollection());
        }
    }

    @Override
    public void onActionFailure(int action, @Nullable BaseResponse response, String message) {
        if (action == UserScene.ACTION_GET_URL_INFO) {
            Toast.makeText(getContext(), getString(R.string.add_url_failure), Toast.LENGTH_SHORT).show();
            mUrlField.setEnabled(true);
            activeNextButton();
        }
    }

    public void setUrlInfoResult(@Nullable Collection col) {
        OnUrlConfirmListener listener = getDialogListener(OnUrlConfirmListener.class);
        if (listener != null) {
            listener.onUrlConfirm(col);
        } else {
            Intent data = new Intent();
            data.putExtra(REQUEST_RESULT_URL, col != null ? col : new WebPageInfo());
            setResult(requestCode, Activity.RESULT_OK, data);
        }
        dismiss();
    }

    public interface OnUrlConfirmListener {
        void onUrlConfirm(@Nullable Collection collection);
    }

    public static class Builder extends BaseDialogBuilder<Builder, UriDialogFragment> {
        @Override
        protected Builder self() {
            return this;
        }

        public Builder(Context context, FragmentManager fragmentManager) {
            super(context, fragmentManager, UriDialogFragment.class);
        }
    }
}
