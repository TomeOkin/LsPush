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
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.base.BaseActivity;
import com.tomeokin.lspush.data.model.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CollectionWebViewActivity extends BaseActivity {
    private static final String EXTRA_COLLECTION = "extra.collection";

    @Nullable @BindView(R.id.toolbar) Toolbar mToolBar;
    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.webView) WebView mWebView;

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
        setContentView(R.layout.activity_collection_web_view);

        mCollection = getIntent().getParcelableExtra(EXTRA_COLLECTION);
        if (mCollection == null || mCollection.getLink() == null) {
            Toast.makeText(this, getString(R.string.unexpected_error), Toast.LENGTH_SHORT).show();
            finish();
        }
        ButterKnife.bind(this);
        setupToolbar();
        setupWebView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.collection_web_view_menu, menu);
        if (menu instanceof MenuBuilder) {
            MenuBuilder builder = (MenuBuilder) menu;
            builder.setOptionalIconsVisible(true);
        }
        //if(menu.getClass().getSimpleName().equals("MenuBuilder")){
        //    try{
        //        Method m = menu.getClass().getDeclaredMethod(
        //            "setOptionalIconsVisible", Boolean.TYPE);
        //        m.setAccessible(true);
        //        m.invoke(menu, true);
        //    }
        //    catch(NoSuchMethodException e){
        //        Log.e(TAG, "onMenuOpened", e);
        //    }
        //    catch(Exception e){
        //        throw new RuntimeException(e);
        //    }
        //}
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.collect:
                return true;
            case R.id.copy_link:
                return true;
            case R.id.share:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupToolbar() {
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mCollection.getLink().getTitle());
            }
            mToolBar.setNavigationIcon(R.drawable.ic_nav_cancel);
            mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
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
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(title);
                }
            }
        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return true;
            }
        });
        //final WebSettings settings = mWebView.getSettings();
        //settings.setSupportZoom(true);
        mWebView.loadUrl(mCollection.getLink().getUrl());
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }
}
