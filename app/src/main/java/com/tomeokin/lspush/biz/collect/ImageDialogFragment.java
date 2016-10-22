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
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tomeokin.lspush.R;
import com.tomeokin.lspush.ui.widget.dialog.BaseDialogBuilder;
import com.tomeokin.lspush.ui.widget.dialog.BaseDialogFragment;

public class ImageDialogFragment extends BaseDialogFragment {
    private static final String ARG_IMAGE_URL = "image.url";
    private static final String ARG_IMAGE_WIDTH = "image.width";
    private static final String ARG_IMAGE_HEIGHT = "image.height";

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    protected BaseDialogFragment.Builder config(@NonNull BaseDialogFragment.Builder builder) {
        ImageView imageView = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.dialog_image, null);
        builder.setTitle(R.string.use_current_image)
            .addCustomMessageView(imageView)
            .addPositiveButton(R.string.dialog_ok, this)
            .addNegativeButton(R.string.dialog_cancel, this);

        final String url = getArguments().getString(ARG_IMAGE_URL);
        final Uri uri = Uri.parse(url);
        int width = getArguments().getInt(ARG_IMAGE_WIDTH);
        int height = getArguments().getInt(ARG_IMAGE_HEIGHT);

        final View content = getActivity().getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        final FrameLayout container = builder.getCustomViewHolder();
        final float radio = optimumRadio(content, container, width, height);
        width = (int) (width * radio);
        height = (int) (height * radio);

        ViewGroup.LayoutParams lp = container.getLayoutParams();
        lp.width = width + container.getPaddingLeft() + container.getPaddingRight();
        lp.height = height + container.getPaddingTop() + container.getPaddingBottom();

        Glide.with(this)
            .load(uri)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .override(width, height)
            .fitCenter()
            .into(imageView);

        return builder;
    }

    private float optimumRadio(View content, View container, float width, float height) {
        final int maxWidth = content.getWidth() - container.getPaddingLeft() - container.getPaddingRight() - 50;
        final int maxHeight = content.getHeight() / 2 - container.getPaddingTop() - container.getPaddingBottom();
        //Timber.i("maxWidth: %d, maxHeight: %d", maxWidth, maxHeight);
        final float radioWidth = maxWidth / width * 1.0f;
        final float radioHeight = maxHeight / height * 1.0f;
        //Timber.i("radioWidth: %f, radioHeight: %f", radioWidth, radioHeight);
        //Timber.i("radio: %f", radio);
        return (float) Math.floor(Math.min(radioWidth, radioHeight) * 5) / 5;
    }

    public static class Builder extends BaseDialogBuilder<Builder, ImageDialogFragment> {
        public Builder(Context context, FragmentManager fragmentManager) {
            super(context, fragmentManager, ImageDialogFragment.class);
        }

        @Override
        protected Builder self() {
            return this;
        }

        @NonNull
        @Override
        protected Bundle prepareArguments(Bundle args) {
            return super.prepareArguments(args);
        }

        public ImageDialogFragment show(String url, int width, int height) {
            mArgs.putString(ARG_IMAGE_URL, url);
            mArgs.putInt(ARG_IMAGE_WIDTH, width);
            mArgs.putInt(ARG_IMAGE_HEIGHT, height);
            return super.show();
        }
    }
}
