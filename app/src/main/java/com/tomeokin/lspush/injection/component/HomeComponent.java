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

import com.tomeokin.lspush.biz.home.HomeActivity;
import com.tomeokin.lspush.biz.home.HomeFragment;
import com.tomeokin.lspush.biz.home.UriDialogFragment;
import com.tomeokin.lspush.injection.module.ActivityModule;
import com.tomeokin.lspush.injection.module.CollectionModule;
import com.tomeokin.lspush.injection.scope.PerActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = AppComponent.class, modules = {ActivityModule.class, CollectionModule.class})
public interface HomeComponent extends ActivityComponent {
    void inject(HomeActivity homeActivity);

    void inject(HomeFragment homeFragment);

    void inject(UriDialogFragment uriDialogFragment);
}
