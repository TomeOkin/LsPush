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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.base.BaseActivity;
import com.tomeokin.lspush.biz.base.support.BaseActionCallback;
import com.tomeokin.lspush.biz.common.UserScene;
import com.tomeokin.lspush.biz.usercase.collection.AddFavorAction;
import com.tomeokin.lspush.common.ClipboardUtil;
import com.tomeokin.lspush.common.IntentUtils;
import com.tomeokin.lspush.common.StringUtils;
import com.tomeokin.lspush.data.model.BaseResponse;
import com.tomeokin.lspush.data.model.Collection;
import com.tomeokin.lspush.injection.ProvideComponent;
import com.tomeokin.lspush.injection.component.CollectionWebViewComponent;
import com.tomeokin.lspush.injection.component.DaggerCollectionWebViewComponent;
import com.tomeokin.lspush.injection.module.CollectionModule;
import com.tomeokin.lspush.ui.widget.ShadowLayout;
import com.tomeokin.lspush.ui.widget.listener.AnimationListenerAdapter;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CollectionWebViewActivity extends BaseActivity
    implements View.OnClickListener, BaseActionCallback, AppBarLayout.OnOffsetChangedListener,
    ProvideComponent<CollectionWebViewComponent> {
    public static final String REQUEST_RESULT_COLLECTION = "request.result.collection";
    private static final String EXTRA_COLLECTION = "extra.collection";

    private CollectionWebViewComponent mComponent;
    private Snackbar mSnackbar;
    private CharSequence mSequence;

    @BindColor(R.color.grey_7_whiteout) int activeColor;
    @BindColor(R.color.grey_5_whiteout) int disableColor;

    @BindView(R.id.layout_content) CoordinatorLayout mContentLayout;
    @BindView(R.id.appBar) AppBarLayout mAppBar;
    @BindView(R.id.toolbar) Toolbar mToolBar;
    @BindView(R.id.titleLayout) LinearLayout mTitleLayout;
    @BindView(R.id.title_tv) TextView mTitle;
    @BindView(R.id.toolbar_action_close) ImageButton mCloseButton;
    @BindView(R.id.toolbar_action_more) ImageButton mMoreButton;

    @BindView(R.id.action_back) ImageButton mBackButton;
    @BindView(R.id.action_forward) ImageButton mForwardButton;
    @BindView(R.id.action_favor) ImageButton mFavorButton;

    @BindView(R.id.menu_layout) View mMenuLayout;
    @BindView(R.id.shadow_layout) ShadowLayout mShadowLayout;

    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.webView) WebView mWebView;

    @Inject AddFavorAction mAddFavorAction;

    private Collection mCollection;

    @Override
    public CollectionWebViewComponent component() {
        if (mComponent == null) {
            mComponent = DaggerCollectionWebViewComponent.builder()
                .appComponent(getAppComponent())
                .activityModule(getActivityModule())
                .collectionModule(new CollectionModule())
                .build();
        }
        return mComponent;
    }

    public static void start(@NonNull Fragment source, @NonNull Collection col, int requestCode) {
        Intent starter = new Intent(source.getContext(), CollectionWebViewActivity.class);
        starter.putExtra(EXTRA_COLLECTION, col);
        source.startActivityForResult(starter, requestCode);
        source.getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.hold);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_web_view);

        mCollection = getIntent().getParcelableExtra(EXTRA_COLLECTION);
        if (mCollection == null || mCollection.getLink() == null) {
            Toast.makeText(this, getString(R.string.unexpected_error), Toast.LENGTH_SHORT).show();
            finish();
        }
        ButterKnife.bind(this);
        setupToolbar();
        setupWebView();
        setupMenu();
        setupBottomBar();
    }

    private void setupToolbar() {
        mAppBar.addOnOffsetChangedListener(this);
        setSupportActionBar(mToolBar);
        mCloseButton.setOnClickListener(this);
        mMoreButton.setOnClickListener(this);
        mTitle.setText(mCollection.getLink().getTitle());
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
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false; // let the web view handle the url
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                boolean canGoBack = view.canGoBack();
                mBackButton.setEnabled(canGoBack);
                mBackButton.setImageResource(canGoBack ? R.drawable.ic_action_back_deep : R.drawable.ic_action_back);

                boolean canGoForward = view.canGoForward();
                mForwardButton.setEnabled(canGoForward);
                mForwardButton.setImageResource(
                    canGoForward ? R.drawable.ic_action_forward_deep : R.drawable.ic_action_forward);
            }
        });
        mWebView.loadUrl(mCollection.getLink().getUrl());
    }

    private void setupMenu() {
        mMenuLayout.setOnClickListener(this);
    }

    private void setupBottomBar() {
        mBackButton.setEnabled(false);
        mBackButton.setOnClickListener(this);
        mForwardButton.setEnabled(false);
        mForwardButton.setOnClickListener(this);
        mFavorButton.setOnClickListener(this);
        updateFavor(mCollection.isHasFavor());
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra(REQUEST_RESULT_COLLECTION, mCollection);
        setResult(Activity.RESULT_OK, data);
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.hold, R.anim.slide_right_out);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        component().inject(this);
        mAddFavorAction.attach(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAddFavorAction.detach();
        mAddFavorAction = null;
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
                hideMenu();
                onBackPressed();
                break;
            case R.id.toolbar_action_more:
                showMenu();
                break;
            case R.id.menu_layout:
                hideMenu();
                break;
            case R.id.action_back:
                mWebView.goBack();
                break;
            case R.id.action_forward:
                mWebView.goForward();
                break;
            case R.id.action_favor:
                updateFavor(!mCollection.isHasFavor());
                if (mCollection.isHasFavor()) {
                    mAddFavorAction.addFavor(mCollection);
                } else {
                    // TODO: 2016/10/19 remove favor
                }
                break;
            default:
                dispatchMenuItem(id);
        }
    }

    private void updateFavor(boolean favor) {
        mCollection.setHasFavor(favor);
        mFavorButton.setImageResource(favor ? R.drawable.ic_action_heart_solid : R.drawable.ic_action_heart_hollow);
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

    @Override
    public void onActionSuccess(int action, @Nullable BaseResponse response) {
        if (action == UserScene.ACTION_ADD_FAVOR) {
            Toast.makeText(this, getString(R.string.add_favor_success), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActionFailure(int action, @Nullable BaseResponse response, String message) {

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
