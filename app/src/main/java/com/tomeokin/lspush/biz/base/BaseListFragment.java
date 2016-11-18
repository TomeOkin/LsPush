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
package com.tomeokin.lspush.biz.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.tomeokin.lspush.R;

public abstract class BaseListFragment<A extends BaseListAdapter<VH>, VH extends RecyclerView.ViewHolder>
    extends BaseFragment {
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected RecyclerView mListRv;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected A mListAdapter;
    protected ViewStub mEmptyViewStub;
    protected View mEmptyView;

    private int[] mLastVisibleItemPositions;
    protected boolean mHasMoreData = true;
    private final int mPageSize;
    private int mPage = 0;

    public BaseListFragment() {
        mPageSize = getPageSize();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_list, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mListRv = (RecyclerView) view.findViewById(R.id.list_rv);

        mListRv.setLayoutManager(getLayoutManager());
        mListRv.setAdapter(getListAdapter());
        mListRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int mVisibleThreshold = getVisibleRows() * getColumnCount();
            private int mLastVisibleItem, mTotalItemCount;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mTotalItemCount = mLayoutManager.getItemCount();
                mLastVisibleItem = findLastVisibleItemPosition();
                // 在刷新、在加载更多或者没达到上限前不加载更多
                if (!mSwipeRefreshLayout.isRefreshing()
                    && !mListAdapter.isLoading()
                    && hasMoreData()
                    && mTotalItemCount <= mLastVisibleItem + mVisibleThreshold) {
                    dispatchLoadMoreData();
                }
            }
        });

        mEmptyViewStub = (ViewStub) view.findViewById(R.id.empty_layout_stub);
        final int emptyLayoutRes = getEmptyLayout();
        if (emptyLayoutRes > 0) {
            mEmptyViewStub.setLayoutResource(emptyLayoutRes);
            mEmptyView = mEmptyViewStub.inflate();
            showEmptyView();
        } else {
            showListRv();
        }

        mSwipeRefreshLayout.setEnabled(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dispatchRefreshData();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dispatchRefreshData();
    }

    public void showEmptyView() {
        mEmptyViewStub.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.VISIBLE);
        mListRv.setVisibility(View.GONE);
    }

    public void showListRv() {
        mEmptyViewStub.setVisibility(View.GONE);
        mListRv.setVisibility(View.VISIBLE);
    }

    /**
     * If resource is valid, show empty view.
     */
    @LayoutRes
    public int getEmptyLayout() {
        return 0;
    }

    @NonNull
    public abstract A getListAdapter();

    @NonNull
    public RecyclerView.LayoutManager getLayoutManager() {
        if (mLayoutManager == null) {
            mLayoutManager = new LinearLayoutManager(getContext());
        }
        return mLayoutManager;
    }

    public int getVisibleRows() {
        return 5;
    }

    public int getColumnCount() {
        if (mLayoutManager instanceof GridLayoutManager) {
            return ((GridLayoutManager) mLayoutManager).getSpanCount();
        } else if (mLayoutManager instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager) mLayoutManager).getSpanCount();
        } else {
            return 1;
        }
    }

    public int findLastVisibleItemPosition() {
        if (mLayoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        } else if (mLayoutManager instanceof StaggeredGridLayoutManager) {
            final StaggeredGridLayoutManager lm = (StaggeredGridLayoutManager) mLayoutManager;
            if (mLastVisibleItemPositions == null) {
                mLastVisibleItemPositions = new int[lm.getSpanCount()];
            }

            lm.findLastVisibleItemPositions(mLastVisibleItemPositions);
            // get maximum element within the list
            return getLastVisibleItem(mLastVisibleItemPositions);
        }
        return 0;
    }

    public int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }

    public boolean hasMoreData() {
        return mHasMoreData;
    }

    public int getPageSize() {
        return 20;
    }

    public void dispatchLoadMoreData() {
        mPage = mListAdapter.getItemCount() / mPageSize;
        onLoadMoreData(mPage, mPageSize);
    }

    public abstract void onLoadMoreData(int page, int size);

    public void dispatchRefreshData() {
        mSwipeRefreshLayout.setRefreshing(true);
        mPage = 0;
        onRefreshData(mPageSize);
    }

    public abstract void onRefreshData(int size);

    public void scrollToPosition(int position) {
        mListRv.scrollToPosition(position);
    }
}
