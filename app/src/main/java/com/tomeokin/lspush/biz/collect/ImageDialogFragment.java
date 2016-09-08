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
package com.tomeokin.lspush.biz.collect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tomeokin.lspush.R;
import com.tomeokin.lspush.ui.widget.dialog.BaseDialogBuilder;
import com.tomeokin.lspush.ui.widget.dialog.BaseDialogFragment;

import timber.log.Timber;

public class ImageDialogFragment extends BaseDialogFragment {
    private static final String ARG_IMAGE_URL = "image.url";
    private ImageView mImageView;
    private String mUrl;

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    protected BaseDialogFragment.Builder config(@NonNull BaseDialogFragment.Builder builder) {
        mImageView = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.dialog_image, null);
        builder.setTitle(R.string.use_current_image);
        builder.addCustomMessageView(mImageView);

        mUrl = getArguments().getString(ARG_IMAGE_URL);
        Timber.i(mUrl);
        Glide.with(getContext())
            .load(mUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(mImageView);

        return builder;
    }

    public static class Builder extends BaseDialogBuilder<ImageDialogFragment> {
        public Builder(Context context, FragmentManager fragmentManager) {
            super(context, fragmentManager, ImageDialogFragment.class);
        }

        public ImageDialogFragment show(String url) {
            mArgs.putString(ARG_IMAGE_URL, url);
            return super.show();
        }
    }
}
