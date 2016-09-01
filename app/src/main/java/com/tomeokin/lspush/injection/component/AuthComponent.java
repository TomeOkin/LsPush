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
package com.tomeokin.lspush.injection.component;

import com.tomeokin.lspush.biz.auth.CaptchaFragment;
import com.tomeokin.lspush.biz.auth.CaptchaConfirmationFragment;
import com.tomeokin.lspush.biz.auth.RegisterFragment;
import com.tomeokin.lspush.biz.auth.SignOutActivity;
import com.tomeokin.lspush.injection.module.ActivityModule;
import com.tomeokin.lspush.injection.scope.PerActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface AuthComponent extends ActivityComponent {
    void inject(SignOutActivity signOutActivity);
    void inject(CaptchaFragment captchaFragment);
    void inject(CaptchaConfirmationFragment captchaConfirmationFragment);
    void inject(RegisterFragment registerFragment);
}
