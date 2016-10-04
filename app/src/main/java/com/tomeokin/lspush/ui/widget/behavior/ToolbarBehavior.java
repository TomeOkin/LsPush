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
package com.tomeokin.lspush.ui.widget.behavior;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ToolbarBehavior extends AppBarLayout.Behavior {
    private boolean mScrollable = false;
    private int mCount;

    public ToolbarBehavior() {}

    public ToolbarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
        return mScrollable && super.onInterceptTouchEvent(parent, child, ev);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child, View directTargetChild,
        View target, int nestedScrollAxes) {
        updatedScrollable(directTargetChild);
        return mScrollable && super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, float velocityX,
        float velocityY, boolean consumed) {
        return mScrollable && super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }

    private void updatedScrollable(View directTargetChild) {
        if (directTargetChild instanceof RecyclerView) {
            RecyclerView rv = (RecyclerView) directTargetChild;
            RecyclerView.Adapter adapter = rv.getAdapter();
            if (adapter != null && adapter.getItemCount() != mCount) {
                mScrollable = false;
                mCount = adapter.getItemCount();
                RecyclerView.LayoutManager layoutManager = rv.getLayoutManager();
                if (layoutManager != null) {
                    final int lastVisibleItem = getLatestVisibleItemId(rv.getLayoutManager());
                    mScrollable = lastVisibleItem < mCount - 1;
                }
            }
        } else { mScrollable = true; }
    }

    private int getLatestVisibleItemId(RecyclerView.LayoutManager layoutManager) {
        int lastVisibleItem = 0;
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            lastVisibleItem = Math.abs(linearLayoutManager.findLastCompletelyVisibleItemPosition());
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            int[] lastItems = staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(
                new int[staggeredGridLayoutManager.getSpanCount()]);
            lastVisibleItem = Math.abs(lastItems[lastItems.length - 1]);
        }
        return lastVisibleItem;
    }
}
