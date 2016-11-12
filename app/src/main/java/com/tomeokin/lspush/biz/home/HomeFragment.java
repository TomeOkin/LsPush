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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.base.BaseFragment;
import com.tomeokin.lspush.biz.base.support.BaseActionCallback;
import com.tomeokin.lspush.biz.common.UserScene;
import com.tomeokin.lspush.biz.user.UserActivity;
import com.tomeokin.lspush.biz.usercase.collection.CollectionAction;
import com.tomeokin.lspush.biz.usercase.collection.FavorAction;
import com.tomeokin.lspush.util.StringUtils;
import com.tomeokin.lspush.data.model.BaseResponse;
import com.tomeokin.lspush.data.model.Collection;
import com.tomeokin.lspush.data.model.CollectionResponse;
import com.tomeokin.lspush.injection.component.HomeComponent;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import timber.log.Timber;

public class HomeFragment extends BaseFragment
    implements BaseActionCallback, FavorAction.OnFavorActionCallback, CollectionListAdapter.Callback,
    UriDialogFragment.OnUrlConfirmListener {
    private static final int REQUEST_OPEN_COLLECTION = 201;
    private static final int REQUEST_EDIT_COLLECTION = 202;
    private static final int REQUEST_GET_URL_INFO = 203;

    private Unbinder mUnBinder;
    private CollectionListAdapter mColListAdapter;
    private UriDialogFragment.Builder mUriDialogBuilder;

    private static final int DEFAULT_PAGE_SIZE = 20;
    private int mPage = 0;
    private boolean mHasMoreData = true;

    private Snackbar mSnackbar;
    private CharSequence mSequence;

    @BindColor(R.color.colorPrimary) int mColorPrimary;

    @BindView(R.id.layout_content) CoordinatorLayout mContentLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.col_rv) RecyclerView mColRv;
    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.empty_layout) View mEmptyLayout;

    @Inject FavorAction mFavorAction;
    @Inject CollectionAction mCollectionAction;
    @Inject CollectionHolder mCollectionHolder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        component(HomeComponent.class).inject(this);

        // https://github.com/Tencent/tinker
        // http://www.jianshu.com/p/2a9fcf3c11e4
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mUnBinder = ButterKnife.bind(this, view);

        setupToolbar();

        mEmptyLayout.setVisibility(View.VISIBLE);
        mColListAdapter = new CollectionListAdapter(getActivity(), null, this);
        mColRv.setVisibility(View.GONE);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mColRv.setLayoutManager(linearLayoutManager);
        mColRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int mVisibleThreshold = 5;
            private int mLastVisibleItem, mTotalItemCount;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mTotalItemCount = linearLayoutManager.getItemCount();
                mLastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                // 在刷新、在加载更多或者没达到上限前不加载更多
                if (!mSwipeRefreshLayout.isRefreshing()
                    && !mColListAdapter.isLoading()
                    && mHasMoreData
                    && mTotalItemCount <= mLastVisibleItem + mVisibleThreshold) {
                    loadMoreCollection();
                }
            }
        });
        mColRv.setAdapter(mColListAdapter);
        mColRv.setItemAnimator(new CollectionItemAnimator());
        mSwipeRefreshLayout.setEnabled(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshCollection();
            }
        });
        return view;
    }

    private void loadMoreCollection() {
        // this is faster than come to state idle
        mColRv.post(new Runnable() {
            @Override
            public void run() {
                mPage = mColListAdapter.getItemCount() / DEFAULT_PAGE_SIZE;
                mColListAdapter.showLoadingProgress();
                mCollectionAction.obtainLatestCollection(mPage, DEFAULT_PAGE_SIZE);
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCollectionAction.attach(this);
        mFavorAction.attach(this);
        refreshCollection();
    }

    private void refreshCollection() {
        Timber.v("refresh collection");
        mSwipeRefreshLayout.setRefreshing(true);
        mPage = 0;
        mCollectionAction.obtainLatestCollection(mPage, DEFAULT_PAGE_SIZE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnBinder.unbind();
        mCollectionAction.detach();
        mFavorAction.detach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnBinder = null;
        mCollectionAction = null;
        mFavorAction = null;
        mCollectionHolder.setCollection(null);
        mCollectionHolder = null;
    }

    private void setupToolbar() {
        if (mToolbar != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(mToolbar);
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setTitle(R.string.title_latest_collections);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_home_menu, menu);
        Drawable drawable = menu.findItem(R.id.action_search).getIcon();
        DrawableCompat.setTint(drawable, Color.WHITE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Toast.makeText(getContext(), "search", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_add:
                showUriDialog();
                return true;
            case R.id.action_pin:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showUriDialog() {
        if (mUriDialogBuilder == null) {
            mUriDialogBuilder =
                new UriDialogFragment.Builder(getContext(), getFragmentManager()).setTargetFragment(this,
                    REQUEST_GET_URL_INFO);
        }
        mUriDialogBuilder.show();
    }

    @Override
    public void onActionSuccess(int action, @Nullable BaseResponse response) {
        if (action == UserScene.ACTION_OBTAIN_LATEST_COLLECTIONS) {
            CollectionResponse colResponse = (CollectionResponse) response;
            if (colResponse != null
                && colResponse.getCollections() != null
                && colResponse.getCollections().size() > 0) {
                mColRv.setVisibility(View.VISIBLE);
                mEmptyLayout.setVisibility(View.GONE);

                mHasMoreData = colResponse.getCollections().size() == DEFAULT_PAGE_SIZE;
                // task is refresh collection
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mColListAdapter.setColList(colResponse.getCollections());
                    mSwipeRefreshLayout.setRefreshing(false);
                } else { // task is load more collection
                    mColListAdapter.hideLoadingProgress();
                    mColListAdapter.insertColList(colResponse.getCollections());
                }
            } else {
                // there is no or no more data
                mHasMoreData = false;
                mColRv.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                mColListAdapter.hideLoadingProgress();
                mEmptyLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onActionFailure(int action, @Nullable BaseResponse response, String message) {
        if (action == UserScene.ACTION_OBTAIN_LATEST_COLLECTIONS) {
            mSwipeRefreshLayout.setRefreshing(false);
            mColListAdapter.hideLoadingProgress();
        }
    }

    @Override
    public void onFavorChange(View favorButton, int position, Collection collection) {
        if (collection.isHasFavor()) {
            mFavorAction.addFavor(position, collection);
        } else {
            mFavorAction.removeFavor(position, collection);
        }
    }

    @Override
    public void onFavorActionSuccess(int position, Collection collection) {

    }

    @Override
    public void onFavorActionFailure(int position, Collection collection) {
        showSnackbarNotification(
            getString(collection.isHasFavor() ? R.string.add_favor_failure : R.string.remove_favor_failure));
        collection.setHasFavor(!collection.isHasFavor());
        mColListAdapter.updateColList(position, collection);
    }

    @Override
    public void onShowMoreExplorers(Collection collection) {

    }

    @Override
    public void onOpenCollectionUrl(int position, Collection collection) {
        mCollectionHolder.setPosition(position);
        mCollectionHolder.setCollection(collection);
        CollectionWebViewActivity.start(this, REQUEST_OPEN_COLLECTION);
    }

    @Override
    public void onOpenUserPage(String uid) {
        UserActivity.start(getContext(), uid);
    }

    @Override
    public void onUrlConfirm(@Nullable Collection collection) {
        if (collection != null) {
            Timber.v("url info: %s", collection.toString());
            mCollectionHolder.setCollection(collection);
            CollectionEditorActivity.start(this, REQUEST_EDIT_COLLECTION);
        } else {
            Timber.v("Url Info Collection is null");
            Toast.makeText(getContext(), R.string.could_not_get_url_info, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Timber.v("Result %d, requestCode %d", resultCode, requestCode);
        if (resultCode != Activity.RESULT_OK) {
            Timber.w("Error result %d, requestCode %d", resultCode, requestCode);
            return;
        }

        if (requestCode == REQUEST_OPEN_COLLECTION) {
            int position = mCollectionHolder.getPosition();
            Collection collection = mCollectionHolder.getCollection();
            if (collection != null) {
                mColListAdapter.updateColList(position, collection);
            }
        } else if (requestCode == REQUEST_EDIT_COLLECTION) {
            mColRv.scrollToPosition(0);
            refreshCollection();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @OnClick(R.id.start_edit)
    public void startEditCollection() {
        showUriDialog();
    }

    public void showSnackbarNotification(@NonNull CharSequence sequence) {
        if (mSnackbar == null) {
            mSequence = sequence;
            mSnackbar = Snackbar.make(mContentLayout, sequence, Snackbar.LENGTH_SHORT);
            final View snackbarView = mSnackbar.getView();
            snackbarView.setBackgroundColor(mColorPrimary);
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
