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
package com.tomeokin.lspush.ui.widget.dialog;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class SimpleDialogBuilder extends BaseDialogBuilder<SimpleDialogBuilder, BaseDialogFragment> {
    public SimpleDialogBuilder(Context context, FragmentManager fragmentManager) {
        super(context, fragmentManager, BaseDialogFragment.class);
    }

    public SimpleDialogBuilder(Fragment fragment) {
        super(fragment.getContext(), fragment.getFragmentManager(), BaseDialogFragment.class);
    }

    public SimpleDialogBuilder(FragmentActivity activity) {
        super(activity, activity.getSupportFragmentManager(), BaseDialogFragment.class);
    }

    @Override
    protected SimpleDialogBuilder self() {
        return this;
    }
}
