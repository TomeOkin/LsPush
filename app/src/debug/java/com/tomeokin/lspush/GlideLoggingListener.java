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
package com.tomeokin.lspush;

import android.util.Log;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.Locale;

public class GlideLoggingListener<T, D> implements RequestListener<T, D> {
    public static final String TAG = "GLIDE";

    @Override
    public boolean onException(Exception e, T model, Target<D> target, boolean isFirstResource) {
        Log.d(TAG, String.format(Locale.ROOT, "onException(%s, %s, %s, %s)", e, model, target, isFirstResource), e);
        return false;
    }

    @Override
    public boolean onResourceReady(D resource, T model, Target<D> target, boolean isFromMemoryCache,
        boolean isFirstResource) {
        Log.d(TAG, String.format(Locale.ROOT, "onResourceReady(%s, %s, %s, %s, %s)", resource, model, target,
            isFromMemoryCache, isFirstResource));
        return false;
    }
}
