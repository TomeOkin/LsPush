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
package com.tomeokin.lspush.ui.glide;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tomeokin.lspush.R;

public class ImageLoader {
    public static void loadAvatar(Context context, ImageView avatar, @Nullable String image) {
        Glide.with(context)
            .load(TextUtils.isEmpty(image) ? R.drawable.avatar3 : image)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .transform(new CircleTransform(context))
            .placeholder(R.drawable.avatar3)
            .into(avatar);
    }

    public static void loadImage(Context context, ImageView target, @Nullable String image) {
        if (TextUtils.isEmpty(image)) {
            target.setVisibility(View.GONE);
        } else {
            target.setVisibility(View.VISIBLE);
            Glide.with(context).load(image).diskCacheStrategy(DiskCacheStrategy.ALL).into(target);
        }
    }
}