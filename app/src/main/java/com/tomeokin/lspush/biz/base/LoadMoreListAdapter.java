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

import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tomeokin.lspush.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class LoadMoreListAdapter extends BaseListAdapter<RecyclerView.ViewHolder> {

    public LoadMoreListAdapter() {
        mShowFooter = true;
    }

    @Override
    public RecyclerView.ViewHolder onCreateFooterViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        final View view = inflater.inflate(R.layout.layout_loading, parent, false);
        return new LoadingViewHolder(view);
    }

    @Override
    protected void onBindFooterViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((LoadingViewHolder) holder).render();
    }

    public final class LoadingViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.loading_progress_bar) ContentLoadingProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void render() {
            progressBar.show();
        }
    }

    public interface OnLoadMoreListener {

    }
}
