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
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.scalpel.ScalpelFrameLayout;
import com.tomeokin.lspush.BuildConfig;
import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.base.BaseActivity;
import com.tomeokin.lspush.data.model.Collection;
import com.tomeokin.lspush.ui.widget.ShadowLayout;
import com.tomeokin.lspush.ui.widget.listener.AnimationListenerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CollectionWebViewActivity extends BaseActivity implements View.OnClickListener {
    private static final String EXTRA_COLLECTION = "extra.collection";

    @BindView(R.id.toolbar) Toolbar mToolBar;
    @BindView(R.id.title_tv) TextView mTitle;
    @BindView(R.id.toolbar_action_close) ImageButton mCloseButton;
    @BindView(R.id.toolbar_action_more) ImageButton mMoreButton;

    @BindView(R.id.menu_layout) View mMenuLayout;
    @BindView(R.id.shadow_layout) ShadowLayout mShadowLayout;

    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.webView) WebView mWebView;

    ScalpelFrameLayout mScalpelLayout;

    private Collection mCollection;

    public static void start(Activity activity, @NonNull Collection col) {
        Intent starter = new Intent(activity, CollectionWebViewActivity.class);
        starter.putExtra(EXTRA_COLLECTION, col);
        activity.startActivity(starter);
        activity.overridePendingTransition(R.anim.slide_right_in, R.anim.hold);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
            View root = getLayoutInflater().inflate(R.layout.activity_collection_web_view, null);
            mScalpelLayout = new ScalpelFrameLayout(this);
            mScalpelLayout.addView(root);
            setContentView(mScalpelLayout);
        } else {
            setContentView(R.layout.activity_collection_web_view);
        }

        mCollection = getIntent().getParcelableExtra(EXTRA_COLLECTION);
        if (mCollection == null || mCollection.getLink() == null) {
            Toast.makeText(this, getString(R.string.unexpected_error), Toast.LENGTH_SHORT).show();
            finish();
        }
        ButterKnife.bind(this);
        setupToolbar();
        setupWebView();
        setupMenu();
    }

    //@Override
    //public boolean onCreateOptionsMenu(Menu menu) {
    //    getMenuInflater().inflate(R.menu.collection_web_view_menu, menu);
    //    if (menu instanceof MenuBuilder) {
    //        MenuBuilder builder = (MenuBuilder) menu;
    //        builder.setOptionalIconsVisible(true);
    //    }
    //    //if(menu.getClass().getSimpleName().equals("MenuBuilder")){
    //    //    try{
    //    //        Method m = menu.getClass().getDeclaredMethod(
    //    //            "setOptionalIconsVisible", Boolean.TYPE);
    //    //        m.setAccessible(true);
    //    //        m.invoke(menu, true);
    //    //    }
    //    //    catch(NoSuchMethodException e){
    //    //        Log.e(TAG, "onMenuOpened", e);
    //    //    }
    //    //    catch(Exception e){
    //    //        throw new RuntimeException(e);
    //    //    }
    //    //}
    //    return true;
    //}
    //
    //@Override
    //public boolean onOptionsItemSelected(MenuItem item) {
    //    switch (item.getItemId()) {
    //        case R.id.collect:
    //            return true;
    //        case R.id.copy_link:
    //            return true;
    //        case R.id.share:
    //            return true;
    //        default:
    //            return super.onOptionsItemSelected(item);
    //    }
    //}

    private void setupToolbar() {
        setSupportActionBar(mToolBar);
        mCloseButton.setOnClickListener(this);
        mMoreButton.setOnClickListener(this);
        mTitle.setText(mCollection.getLink().getTitle());
    }

    private void setupWebView() {
        mProgressBar.setMax(100);
        mProgressBar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
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
                //mTitle.setText(title);
            }
        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return true;
            }
        });
        mWebView.loadUrl(mCollection.getLink().getUrl());
    }

    private void setupMenu() {
        mMenuLayout.setOnClickListener(this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.hold, R.anim.slide_right_out);
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
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.toolbar_action_close) {
            onBackPressed();
        } else if (id == R.id.toolbar_action_more) {
            showMenu();
        } else if (id == R.id.menu_layout) {
            hideMenu();
        } else {
            dispatchMenuItem(id);
        }
    }

    public boolean dispatchMenuItem(int id) {
        switch (id) {
            case R.id.action_refresh:
                hideMenu();
                // TODO: 2016/10/16 refresh
                return true;
            case R.id.action_collect:
                hideMenu();
                // TODO: 2016/10/16
                return true;
            case R.id.action_copy_link:
                hideMenu();
                // TODO: 2016/10/16
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
}
