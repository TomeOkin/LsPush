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
package com.tomeokin.lspush.ui.widget;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.base.BaseActivity;
import com.tomeokin.lspush.common.ClipboardUtil;
import com.tomeokin.lspush.common.IntentUtils;
import com.tomeokin.lspush.common.StringUtils;
import com.tomeokin.lspush.ui.widget.listener.AnimationListenerAdapter;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BaseWebViewActivity extends BaseActivity
    implements View.OnClickListener, AppBarLayout.OnOffsetChangedListener {
    private Snackbar mSnackbar;
    private CharSequence mSequence;

    @BindString(R.string.unexpected_error) String mErrorNotification;

    @BindView(R.id.layout_content) CoordinatorLayout mContentLayout;
    @BindView(R.id.appBar) AppBarLayout mAppBar;
    @BindView(R.id.toolbar) Toolbar mToolBar;
    @BindView(R.id.titleLayout) LinearLayout mTitleLayout;
    @BindView(R.id.title_tv) TextView mTitle;
    @BindView(R.id.subTitle_tv) TextView mSubTitle;
    @BindView(R.id.toolbar_action_close) ImageButton mCloseButton;
    @BindView(R.id.toolbar_action_more) ImageButton mMoreButton;

    @BindView(R.id.menu_layout) View mMenuLayout;
    @BindView(R.id.shadow_layout) ShadowLayout mShadowLayout;

    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.webView) WebView mWebView;

    @BindView(R.id.custom_layout_container) ViewStub mCustomLayoutContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_webview);

        if (!onPrepareActivity()) {
            Toast.makeText(this, mErrorNotification, Toast.LENGTH_SHORT).show();
            finish();
        }

        ButterKnife.bind(this);
        onPrepareInflater(mCustomLayoutContainer);

        setupToolbar();
        setupWebView();
        setupMenu();
    }

    protected boolean onPrepareActivity() {
        return true;
    }

    protected void onPrepareInflater(ViewStub container) {}

    private void setupToolbar() {
        mAppBar.addOnOffsetChangedListener(this);
        setSupportActionBar(mToolBar);
        mCloseButton.setOnClickListener(this);
        mMoreButton.setOnClickListener(this);
        mTitle.setText(onPrepareTitle());
    }

    protected String onPrepareTitle() {
        return null;
    }

    protected void setSubTitle(CharSequence subTitle) {
        mSubTitle.setVisibility(View.VISIBLE);
        mSubTitle.setText(subTitle);
    }

    private void setupWebView() {
        mProgressBar.setMax(100);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                mTitle.setText(title);
                mTitle.requestLayout();
                mTitleLayout.requestLayout();
            }
        });
        onPrepareWebView(mWebView);
        mWebView.loadUrl(onPrepareUrl());
    }

    protected void onPrepareWebView(WebView webView) {}

    protected String onPrepareUrl() {
        return "about:blank";
    }

    public WebView getWebView() {
        return mWebView;
    }

    private void setupMenu() {
        mMenuLayout.setOnClickListener(this);
    }

    protected void showMenu() {
        mMenuLayout.setVisibility(View.VISIBLE);
        Animation popupAnim = AnimationUtils.loadAnimation(this, R.anim.popup_layout_show);
        mShadowLayout.startAnimation(popupAnim);
    }

    protected void hideMenu() {
        Animation popupAnim = AnimationUtils.loadAnimation(this, R.anim.popup_layout_hide);
        mShadowLayout.startAnimation(popupAnim);
        popupAnim.setAnimationListener(new AnimationListenerAdapter() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mMenuLayout.setVisibility(View.GONE);
            }
        });
    }

    protected boolean isShowingMenu() {
        return mMenuLayout.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        ViewCompat.setTranslationY(mProgressBar, verticalOffset);

        ViewCompat.setTranslationY(mMenuLayout, Math.max(verticalOffset, 0));
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.toolbar_action_close:
                if (isShowingMenu()) {
                    hideMenu();
                }
                onBackPressed();
                break;
            case R.id.toolbar_action_more:
                showMenu();
                break;
            case R.id.menu_layout:
                hideMenu();
                break;
            case R.id.action_back:
                if (isShowingMenu()) {
                    hideMenu();
                }
                mWebView.goBack();
                break;
            case R.id.action_forward:
                mWebView.goForward();
                break;
            default:
                if (!dispatchChildOnClick(id)) {
                    dispatchMenuItem(id);
                }
        }
    }

    public boolean dispatchChildOnClick(int id) {
        return false;
    }

    public boolean dispatchMenuItem(int id) {
        switch (id) {
            case R.id.action_refresh:
                hideMenu();
                mWebView.reload();
                return true;
            case R.id.action_open_in_browser:
                hideMenu();
                IntentUtils.openInBrowser(this, mWebView.getUrl());
                return true;
            case R.id.action_copy_link:
                hideMenu();
                ClipboardUtil.setText(this, mWebView.getUrl());
                showSnackbarNotification(getString(R.string.copied_to_clipboard));
                return true;
            case R.id.action_share:
                hideMenu();
                // TODO: 2016/10/16
                return true;
            default:
                return false;
        }
    }

    @OnClick(R.id.webView_container)
    public void onBlankPositionClink() {
        hideMenu();
    }

    public void showSnackbarNotification(@NonNull CharSequence sequence) {
        if (mSnackbar == null) {
            mSequence = sequence;
            mSnackbar = Snackbar.make(mContentLayout, sequence, Snackbar.LENGTH_SHORT);
            final View snackbarView = mSnackbar.getView();
            snackbarView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
            mSnackbar.show();
        } else if (mSnackbar.isShown()) {
            if (!StringUtils.isEqual(sequence, mSequence)) {
                mSequence = sequence;
                mSnackbar.dismiss();
                mSnackbar.setText(sequence);
                mSnackbar.show();
            }
        } else {
            mSequence = sequence;
            mSnackbar.setText(sequence);
            mSnackbar.show();
        }
    }
}
