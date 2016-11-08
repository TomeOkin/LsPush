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
package com.tomeokin.lspush.ui.widget.listener;

import android.support.annotation.IntDef;
import android.support.design.widget.AppBarLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {
    public static final int STATE_EXPANDED = 0;
    public static final int STATE_COLLAPSED = 1;
    public static final int STATE_IDLE = 2;

    @IntDef({
        STATE_EXPANDED,
        STATE_COLLAPSED,
        STATE_IDLE
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface State { }

    @State private int mCurrentState = STATE_IDLE;

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (verticalOffset == 0) {
            updateState(appBarLayout, STATE_EXPANDED);
        } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
            updateState(appBarLayout, STATE_COLLAPSED);
        } else {
            updateState(appBarLayout, STATE_IDLE);
        }
    }

    private void updateState(AppBarLayout appBarLayout, @State int state) {
        if (mCurrentState != state) {
            mCurrentState = state;
            onStateChanged(appBarLayout, state);
        }
    }

    public abstract void onStateChanged(AppBarLayout appBarLayout, @State int state);
}
