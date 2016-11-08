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
package com.tomeokin.lspush.biz.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.base.BaseActivity;
import com.tomeokin.lspush.ui.widget.listener.AnimationListenerAdapter;
import com.tomeokin.lspush.ui.widget.listener.AppBarStateChangeListener;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class UserActivity extends BaseActivity {
    private static final String ARG_UID = "arg.uid";

    private final AppBarLayout.OnOffsetChangedListener mStateChangedListener = new AppBarStateChangeListener() {
        private AlphaAnimation mAnim;
        private boolean mAnimRunning = false;
        private final AnimationListenerAdapter mAnimListener = new AnimationListenerAdapter() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                mAnimRunning = false;
            }
        };

        @Override
        public void onStateChanged(AppBarLayout appBarLayout, @State int state) {
            if (mAnimRunning) {
                return;
            }
            int color = mToolbarUserField.getVisibility() == View.VISIBLE ? Color.TRANSPARENT : Color.WHITE;
            mToolbar.setBackgroundColor(color);
            if (state == STATE_COLLAPSED) {
                Timber.i("show toolbar user field");
                if (mToolbarUserField.getVisibility() != View.VISIBLE) {
                    mAnimRunning = true;
                    mAnim = new AlphaAnimation(0, 1);
                    mAnim.setDuration(100);
                    mAnim.setAnimationListener(mAnimListener);
                    mToolbarUserField.setVisibility(View.VISIBLE);
                    mToolbarUserField.startAnimation(mAnim);
                }
            } else {
                if (mToolbarUserField.getVisibility() != View.GONE) {
                    mAnimRunning = true;
                    Timber.i("hide toolbar user field");
                    mAnim = new AlphaAnimation(1, 0);
                    mAnim.setDuration(100);
                    mAnim.setAnimationListener(mAnimListener);
                    mToolbarUserField.setVisibility(View.GONE);
                    mToolbarUserField.startAnimation(mAnim);
                }
            }
        }
    };

    private String mUid;

    @BindView(R.id.appBar) AppBarLayout mAppBar;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.toolbar_user_field) View mToolbarUserField;
    @BindView(R.id.avatar) ImageView mAvatar;
    @BindView(R.id.username) TextView mUsername;
    @BindDimen(R.dimen.toolbar_size) float mToolbarSize;

    public static void start(Context context, String uid) {
        Intent starter = new Intent(context, UserActivity.class);
        starter.putExtra(ARG_UID, uid);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);

        mUid = getIntent().getStringExtra(ARG_UID);

        mAppBar.addOnOffsetChangedListener(mStateChangedListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAppBar.removeOnOffsetChangedListener(mStateChangedListener);
    }
}
