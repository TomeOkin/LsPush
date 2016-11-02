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
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewStub;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.usercase.collection.FavorAction;
import com.tomeokin.lspush.data.model.Collection;
import com.tomeokin.lspush.injection.ProvideComponent;
import com.tomeokin.lspush.injection.component.CollectionWebViewComponent;
import com.tomeokin.lspush.injection.component.DaggerCollectionWebViewComponent;
import com.tomeokin.lspush.injection.module.CollectionModule;
import com.tomeokin.lspush.ui.widget.BaseWebViewActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CollectionWebViewActivity extends BaseWebViewActivity
    implements FavorAction.OnFavorActionCallback, ProvideComponent<CollectionWebViewComponent> {

    private CollectionWebViewComponent mComponent;
    private int mPosition;
    private Collection mCollection;
    private BottomBar mBottomBar;

    @Inject FavorAction mFavorAction;
    @Inject CollectionHolder mCollectionHolder;

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

    public static void start(@NonNull Fragment source, int requestCode) {
        Intent starter = new Intent(source.getContext(), CollectionWebViewActivity.class);
        source.startActivityForResult(starter, requestCode);
        source.getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.hold);
    }

    @Override
    protected boolean onPrepareActivity() {
        component().inject(this);
        mPosition = mCollectionHolder.getPosition();
        mCollection = mCollectionHolder.getCollection();
        return mCollection != null && mCollection.getLink() != null;
    }

    @Override
    protected void onPrepareInflater(ViewStub container) {
        container.setLayoutResource(R.layout.layout_collection_web_view_bottom_bar);
        View bottomBarView = container.inflate();
        mBottomBar = new BottomBar(bottomBarView);
    }

    @Override
    protected String onPrepareTitle() {
        return mCollection.getLink().getTitle();
    }

    @Override
    protected void onPrepareWebView(WebView webView) {
        super.onPrepareWebView(webView);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false; // let the web view handle the url
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                boolean canGoBack = view.canGoBack();
                mBottomBar.backButton.setEnabled(canGoBack);
                mBottomBar.backButton.setImageResource(
                    canGoBack ? R.drawable.ic_action_back_deep : R.drawable.ic_action_back);

                boolean canGoForward = view.canGoForward();
                mBottomBar.forwardButton.setEnabled(canGoForward);
                mBottomBar.forwardButton.setImageResource(
                    canGoForward ? R.drawable.ic_action_forward_deep : R.drawable.ic_action_forward);
            }
        });
    }

    @Override
    protected String onPrepareUrl() {
        return mCollection.getLink().getUrl();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBottomBar.backButton.setEnabled(false);
        mBottomBar.backButton.setOnClickListener(this);
        mBottomBar.forwardButton.setEnabled(false);
        mBottomBar.forwardButton.setOnClickListener(this);
        mBottomBar.favorButton.setOnClickListener(this);
        updateFavor(mCollection.isHasFavor());
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        mCollectionHolder.setCollection(mCollection);
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
        mFavorAction.attach(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFavorAction != null) {
            mFavorAction.detach();
            mFavorAction = null;
        }
    }

    @Override
    public boolean dispatchChildOnClick(int id) {
        switch (id) {
            case R.id.action_back:
                getWebView().goBack();
                return true;
            case R.id.action_forward:
                getWebView().goForward();
                return true;
            case R.id.action_favor:
                updateFavor(!mCollection.isHasFavor());
                mBottomBar.favorButton.setEnabled(false);
                if (mCollection.isHasFavor()) {
                    mFavorAction.addFavor(mBottomBar.favorButton, mPosition, mCollection);
                } else {
                    mFavorAction.removeFavor(mBottomBar.favorButton, mPosition, mCollection);
                }
                return true;
            default:
                return super.dispatchChildOnClick(id);
        }
    }

    private void updateFavor(boolean favor) {
        if (mCollection.isHasFavor() != favor) {
            mCollection.setFavorCount(mCollection.getFavorCount() + (favor ? 1 : -1));
            mCollection.setHasFavor(favor);
        }
        mBottomBar.favorButton.setImageResource(
            favor ? R.drawable.ic_action_heart_solid : R.drawable.ic_action_heart_hollow);
        mBottomBar.favorCount.setText(getString(R.string.favor_count, mCollection.getFavorCount()));
    }

    @Override
    public void onFavorActionSuccess(View favorButton, int position, Collection collection) {
        favorButton.setEnabled(true);
    }

    @Override
    public void onFavorActionFailure(View favorButton, int position, Collection collection) {
        favorButton.setEnabled(true);
        showSnackbarNotification(
            getString(mCollection.isHasFavor() ? R.string.add_favor_failure : R.string.remove_favor_failure));
        updateFavor(!mCollection.isHasFavor());
    }

    class BottomBar {
        @BindView(R.id.action_back) ImageButton backButton;
        @BindView(R.id.action_forward) ImageButton forwardButton;
        @BindView(R.id.action_favor) ImageButton favorButton;
        @BindView(R.id.favor_count) TextView favorCount;

        public BottomBar(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
