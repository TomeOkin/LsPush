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

import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.tomeokin.lspush.biz.base.support.BaseAction;
import com.tomeokin.lspush.biz.base.support.CommonCallback;
import com.tomeokin.lspush.biz.common.UserScene;
import com.tomeokin.lspush.biz.usercase.user.LsPushUserState;
import com.tomeokin.lspush.data.model.BaseResponse;
import com.tomeokin.lspush.data.model.Collection;
import com.tomeokin.lspush.data.model.CollectionBinding;
import com.tomeokin.lspush.data.remote.LsPushService;

import java.util.Collections;
import java.util.Date;

import retrofit2.Call;

public class FavorAction extends BaseAction {
    private final LsPushService mLsPushService;
    private final LsPushUserState mLsPushUserState;

    private Call<BaseResponse> mAddFavorCall;
    private Call<BaseResponse> mRemoveFavorCall;

    public FavorAction(Resources resources, LsPushService lsPushService, LsPushUserState lsPushUserState) {
        super(resources);
        this.mLsPushService = lsPushService;
        this.mLsPushUserState = lsPushUserState;
    }

    public void addFavor(@NonNull Collection collection) {
        CollectionBinding.Data data = new CollectionBinding.Data();
        data.uid = mLsPushUserState.getUid();
        data.date = new Date();
        CollectionBinding binding = new CollectionBinding();
        binding.setFavors(Collections.singletonList(data));
        binding.setCollectionId(collection.getId());

        checkAndCancel(mAddFavorCall);
        mAddFavorCall = mLsPushService.addFavor(mLsPushUserState.getExpireTokenString(), binding);
        mAddFavorCall.enqueue(new CommonCallback<>(mResource, UserScene.ACTION_ADD_FAVOR, mCallback));
    }

    public void removeFavor(@NonNull Collection collection) {
        checkAndCancel(mRemoveFavorCall);
        mRemoveFavorCall = mLsPushService.removeFavor(mLsPushUserState.getExpireTokenString(), collection.getId());
        mRemoveFavorCall.enqueue(new CommonCallback<>(mResource, UserScene.ACTION_REMOVE_FAVOR, mCallback));
    }

    @Override
    public void cancel(int action) {
        super.cancel(action);
        if (action == UserScene.ACTION_ADD_FAVOR) {
            checkAndCancel(mAddFavorCall);
        } else if (action == UserScene.ACTION_REMOVE_FAVOR) {
            checkAndCancel(mRemoveFavorCall);
        }
    }

    @Override
    public void detach() {
        super.detach();
        checkAndCancel(mAddFavorCall);
        checkAndCancel(mRemoveFavorCall);
    }
}
