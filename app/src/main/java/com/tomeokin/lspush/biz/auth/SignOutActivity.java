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
package com.tomeokin.lspush.biz.auth;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.base.BaseActionCallback;
import com.tomeokin.lspush.biz.base.BaseActivity;
import com.tomeokin.lspush.common.Navigator;
import com.tomeokin.lspush.data.model.BaseResponse;
import com.tomeokin.lspush.injection.ProvideComponent;
import com.tomeokin.lspush.injection.component.AuthComponent;
import com.tomeokin.lspush.injection.component.DaggerAuthComponent;
import com.tomeokin.lspush.injection.module.AuthModule;

public class SignOutActivity extends BaseActivity implements ProvideComponent<AuthComponent>, BaseActionCallback {
    private AuthComponent mComponent;

    @Override
    public AuthComponent component() {
        if (mComponent == null) {
            mComponent = DaggerAuthComponent.builder()
                .appComponent(getAppComponent())
                .activityModule(getActivityModule())
                .authModule(new AuthModule())
                .build();
        }
        return mComponent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        component().inject(this);

        //boolean hasHistoryLogin = mPresenter.hasHistoryLoginUser();
        boolean hasHistoryLogin = false;
        Navigator.moveTo(this, hasHistoryLogin ? LoginFragment.class : CaptchaFragment.class, null);
        //Navigator.moveTo(this, RegisterFragment.class, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActionFailure(int action, @Nullable BaseResponse response, String message) {

    }

    @Override
    public void onActionSuccess(int action, @Nullable BaseResponse response) {

    }
}
