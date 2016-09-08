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
import com.tomeokin.lspush.biz.base.BaseActivity;
import com.tomeokin.lspush.common.Navigator;
import com.tomeokin.lspush.injection.ProvideComponent;
import com.tomeokin.lspush.injection.component.AuthComponent;
import com.tomeokin.lspush.injection.component.DaggerAuthComponent;
import com.tomeokin.lspush.injection.scope.PerActivity;

@PerActivity
public class SignOutActivity extends BaseActivity implements ProvideComponent<AuthComponent> {
    private AuthComponent mComponent;
    //public static final String ACTION_CAPTCHA = "captcha";
    //public static final String ACTION_REGISTER = "register";
    //public static final String ACTION_LOGIN = "login";

    @Override public AuthComponent component() {
        if (mComponent == null) {
            mComponent = DaggerAuthComponent.builder()
                .appComponent(getAppComponent())
                .activityModule(getActivityModule())
                .build();
        }
        return mComponent;
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        component().inject(this);

        boolean hasHistoryLogin = false;
        //moveTo(hasHistoryLogin ? ACTION_LOGIN : ACTION_CAPTCHA);
        //moveTo(hasHistoryLogin ? LoginFragment.class : CaptchaFragment.class, null);
        //Navigator.moveTo(this, hasHistoryLogin ? LoginFragment.class : CaptchaFragment.class, null);
        Navigator.moveTo(this, CaptchaFragment.class, null);
    }

    //public void moveTo(String tag) {
    //    FragmentManager fragmentManager = getSupportFragmentManager();
    //    Fragment current = fragmentManager.findFragmentById(R.id.fragment_container);
    //    Fragment target = fragmentManager.findFragmentByTag(tag);
    //
    //    if (target == null) {
    //        target = createFragment(tag);
    //        if (target == null) {
    //            return;
    //        }
    //        FragmentTransaction transaction = fragmentManager.beginTransaction();
    //        if (current == null) {
    //            transaction.add(R.id.fragment_container, target, tag);
    //        } else {
    //            transaction.replace(R.id.fragment_container, target, tag);
    //            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
    //            transaction.addToBackStack(tag);
    //        }
    //        transaction.commit();
    //    } else {
    //        if (current == target) {
    //            return;
    //        }
    //        fragmentManager.popBackStackImmediate(tag, 0);
    //    }
    //}
    //
    //public Fragment createFragment(String tag) {
    //    switch (tag) {
    //        case ACTION_REGISTER:
    //            return new RegisterFragment();
    //        case ACTION_LOGIN:
    //            return new LoginFragment();
    //        case ACTION_CAPTCHA:
    //            return new CaptchaFragment();
    //        default:
    //            Timber.tag("error").w("error text");
    //            return null;
    //    }
    //}
}
