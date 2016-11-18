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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public abstract class BaseListAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected final int VIEW_TYPE_NORMAL = 0;
    protected final int VIEW_TYPE_LOADING = 1;
    private boolean mIsLoading = false;

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_LOADING) {
            return onCreateLoadingView(inflater, parent, viewType);
        } else {
            return onCreateNormalView(inflater, parent, viewType);
        }
    }

    public abstract VH onCreateNormalView(LayoutInflater inflater, ViewGroup parent, int viewType);

    public abstract VH onCreateLoadingView(LayoutInflater inflater, ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(VH holder, int position) {

    }

    public void showLoadingProgress() {
        mIsLoading = true;
        notifyItemInserted(getItemNormalCount());
    }

    public void hideLoadingProgress() {
        mIsLoading = false;
        notifyItemRemoved(getItemNormalCount());
    }

    public boolean isLoading() {
        return mIsLoading;
    }

    @Override
    public int getItemViewType(int position) {
        return position < getItemNormalCount() ? VIEW_TYPE_NORMAL : VIEW_TYPE_LOADING;
    }

    @Override
    public int getItemCount() {
        return getItemNormalCount() + (mIsLoading ? 1 : 0);
    }

    public int getItemNormalCount() {
        return 0;
    }
}
