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
package com.tomeokin.lspush.biz.usercase.collection;

import android.support.annotation.NonNull;

import com.tomeokin.lspush.biz.base.support.CommonAction;
import com.tomeokin.lspush.biz.common.UserScene;
import com.tomeokin.lspush.biz.usercase.user.LsPushUserState;
import com.tomeokin.lspush.data.model.BaseResponse;
import com.tomeokin.lspush.data.model.Collection;
import com.tomeokin.lspush.data.model.CollectionBinding;
import com.tomeokin.lspush.data.remote.LsPushService;

import java.util.Collections;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public final class FavorAction extends CommonAction<FavorAction.OnFavorActionCallback> {
    private final LsPushService mLsPushService;
    private final LsPushUserState mLsPushUserState;

    public FavorAction(LsPushService lsPushService, LsPushUserState lsPushUserState) {
        mLsPushService = lsPushService;
        mLsPushUserState = lsPushUserState;
    }

    public void addFavor(int position, @NonNull Collection collection) {
        CollectionBinding.Data data = new CollectionBinding.Data();
        data.uid = mLsPushUserState.getUid();
        data.date = new Date();
        CollectionBinding binding = new CollectionBinding();
        binding.setFavors(Collections.singletonList(data));
        binding.setCollectionId(collection.getId());

        Call<BaseResponse> addFavorCall = mLsPushService.addFavor(mLsPushUserState.getExpireTokenString(), binding);
        addFavorCall.enqueue(new FavorActionCallback(position, collection));
    }

    public void removeFavor(int position, @NonNull Collection collection) {
        Call<BaseResponse> removeFavorCall =
            mLsPushService.removeFavor(mLsPushUserState.getExpireTokenString(), collection.getId());
        removeFavorCall.enqueue(new FavorActionCallback(position, collection));
    }

    public final class FavorActionCallback implements Callback<BaseResponse> {
        private final int position;
        private final Collection collection;

        public FavorActionCallback(int position, @NonNull Collection collection) {
            this.position = position;
            this.collection = collection;
        }

        @Override
        public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
            if (response.isSuccessful()) {
                BaseResponse baseResponse = response.body();
                if (baseResponse.getResultCode() == BaseResponse.COMMON_SUCCESS) {
                    if (mCallback != null) {
                        mCallback.onFavorActionSuccess(position, collection);
                    }
                } else {
                    if (mCallback != null) {
                        mCallback.onFavorActionFailure(position, collection);
                    }
                }
            } else {
                try {
                    Timber.tag(UserScene.TAG_NETWORK).w(response.errorBody().string());
                } catch (Exception e) {
                    // ignore
                }
                if (mCallback != null) {
                    mCallback.onFavorActionFailure(position, collection);
                }
            }
        }

        @Override
        public void onFailure(Call<BaseResponse> call, Throwable t) {
            Timber.w(t);
            mCallback.onFavorActionFailure(position, collection);
        }
    }

    public interface OnFavorActionCallback {
        void onFavorActionSuccess(int position, Collection collection);

        void onFavorActionFailure(int position, Collection collection);
    }
}
