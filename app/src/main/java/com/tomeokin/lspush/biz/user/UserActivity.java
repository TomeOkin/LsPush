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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;

import com.tomeokin.lspush.R;
import com.tomeokin.lspush.biz.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserActivity extends BaseActivity {
    private static final String ARG_UID = "arg.uid";

    //private final AppBarLayout.OnOffsetChangedListener mStateChangedListener = new AppBarStateChangeListener() {
    //    private AlphaAnimation mAnim;
    //    private boolean mAnimRunning = false;
    //    private final AnimationListenerAdapter mAnimListener = new AnimationListenerAdapter() {
    //        @Override
    //        public void onAnimationEnd(Animation animation) {
    //            super.onAnimationEnd(animation);
    //            mAnimRunning = false;
    //        }
    //    };
    //
    //    @Override
    //    public void onStateChanged(AppBarLayout appBarLayout, @State int state) {
    //        if (mAnimRunning) {
    //            return;
    //        }
    //        int color = mToolbarUserField.getVisibility() == View.VISIBLE ? Color.TRANSPARENT : Color.WHITE;
    //        mToolbar.setBackgroundColor(color);
    //        if (state == STATE_COLLAPSED) {
    //            Timber.i("show toolbar user field");
    //            if (mToolbarUserField.getVisibility() != View.VISIBLE) {
    //                mAnimRunning = true;
    //                mAnim = new AlphaAnimation(0, 1);
    //                mAnim.setDuration(100);
    //                mAnim.setAnimationListener(mAnimListener);
    //                mToolbarUserField.setVisibility(View.VISIBLE);
    //                mToolbarUserField.startAnimation(mAnim);
    //            }
    //        } else {
    //            if (mToolbarUserField.getVisibility() != View.GONE) {
    //                mAnimRunning = true;
    //                Timber.i("hide toolbar user field");
    //                mAnim = new AlphaAnimation(1, 0);
    //                mAnim.setDuration(100);
    //                mAnim.setAnimationListener(mAnimListener);
    //                mToolbarUserField.setVisibility(View.GONE);
    //                mToolbarUserField.startAnimation(mAnim);
    //            }
    //        }
    //    }
    //};

    //private final AppBarLayout.OnOffsetChangedListener mOnOffsetChangeListener =
    //    new AppBarLayout.OnOffsetChangedListener() {
    //        boolean mMeasure = false;
    //
    //        int out[] = new int[2];
    //        Rect mHeaderAvatarLoc = new Rect();
    //        Rect mToolbarAvatarLoc = new Rect();
    //
    //        float mScaleBaseline;
    //        float mTransitionX;
    //        float mTransitionY;
    //
    //        float mHeaderNicknameTextSize;
    //        float mToolbarNicknameTextSize;
    //        float mTextSizeDiff;
    //
    //        float mMaxOffset;
    //
    //        @Override
    //        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
    //            if (verticalOffset == 0) {
    //                if (!mMeasure) {
    //                    mMeasure = true;
    //                    mHeaderAvatar.getLocationOnScreen(out);
    //                    mHeaderAvatarLoc.set(out[0], out[1], out[0] + mHeaderAvatar.getWidth(),
    //                        out[1] + mHeaderAvatar.getHeight());
    //
    //                    mToolbarAvatar.getLocationOnScreen(out);
    //                    mToolbarAvatarLoc.set(out[0], out[1], out[0] + mToolbarAvatar.getWidth(),
    //                        out[1] + mToolbarAvatar.getHeight());
    //
    //                    mTransitionX =
    //                        (mToolbarAvatarLoc.left + mToolbarAvatarLoc.width() / 2.0f) - (mHeaderAvatarLoc.left
    //                            + mHeaderAvatarLoc.width() / 2.0f);
    //                    mTransitionY = (mToolbarAvatarLoc.top - mToolbarAvatar.getPaddingTop()) - (mHeaderAvatarLoc.top
    //                        - mHeaderAvatar.getPaddingTop());
    //
    //                    mScaleBaseline = 1.0f * mToolbarAvatar.getWidth() / mHeaderAvatar.getWidth();
    //
    //                    mMaxOffset = appBarLayout.getTotalScrollRange();
    //
    //                    mHeaderNicknameTextSize = mHeaderNickname.getTextSize();
    //                    mToolbarNicknameTextSize = mToolbarNickname.getTextSize();
    //                    mTextSizeDiff = mToolbarNicknameTextSize - mHeaderNicknameTextSize;
    //
    //                }
    //
    //                // 张开
    //                mToolbarUserField.setVisibility(View.INVISIBLE);
    //                updateAvatar(0, 0, 0, 1);
    //                mHeaderAvatar.setVisibility(View.VISIBLE);
    //
    //                mHeaderNickname.setTextSize(TypedValue.COMPLEX_UNIT_PX, mHeaderNicknameTextSize);
    //                mHeaderNickname.setVisibility(View.VISIBLE);
    //
    //                mHeaderUid.setTextColor(Color.argb(255, 255, 255, 255));
    //                mHeaderUid.setVisibility(View.VISIBLE);
    //            } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
    //                // 收缩
    //                updateAvatar(mTransitionX, mTransitionY, 1, mScaleBaseline);
    //                mHeaderAvatar.setVisibility(View.INVISIBLE);
    //
    //                mHeaderNickname.setTextSize(TypedValue.COMPLEX_UNIT_PX, mToolbarNicknameTextSize);
    //                mHeaderNickname.setVisibility(View.INVISIBLE);
    //
    //                mHeaderUid.setTextColor(Color.argb(0, 255, 255, 255));
    //                mHeaderUid.setVisibility(View.INVISIBLE);
    //
    //                mToolbarUserField.setVisibility(View.VISIBLE);
    //            } else {
    //                mToolbarUserField.setVisibility(View.INVISIBLE);
    //
    //                final float radio = -verticalOffset / mMaxOffset;
    //                final float r = (mMaxOffset + verticalOffset) / mMaxOffset;
    //                final float iScale = (1 - mScaleBaseline) + mScaleBaseline * r;
    //                updateAvatar(mTransitionX, mTransitionY, radio, iScale);
    //                mHeaderAvatar.setVisibility(View.VISIBLE);
    //
    //                // TODO: 2016/11/12
    //
    //                final float textSize = mHeaderNicknameTextSize + mTextSizeDiff * radio;
    //                Timber.i("mTextSizeDiff * radio %f", mTextSizeDiff * radio);
    //                mHeaderNickname.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    //                mHeaderNickname.setVisibility(View.VISIBLE);
    //
    //                mHeaderUid.setTextColor(Color.argb((int) (r * 255), 255, 255, 255));
    //                mHeaderUid.setVisibility(View.VISIBLE);
    //                //float ix = (48 - 128) * 2 * (-verticalOffset * 1f / appBarLayout.getTotalScrollRange());
    //                //Timber.i("TranslationX: %f", ix);
    //                //mHeaderAvatar.setTranslationX(ix);
    //                //mHeaderAvatar.setScaleX();
    //                //int alpha = 255 * Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange();
    //                //mToolbarUserField.setVisibility(View.VISIBLE);
    //                //setToolbarAlpha(alpha);
    //            }
    //        }
    //
    //        private void updateAvatar(float tx, float ty, float radioT, float scale) {
    //            mHeaderAvatar.setTranslationX(tx * radioT);
    //            mHeaderAvatar.setTranslationY(ty * radioT);
    //            mHeaderAvatar.setScaleX(scale);
    //            mHeaderAvatar.setScaleY(scale);
    //        }
    //    };

    private String mUid;

    @BindView(R.id.appBar) AppBarLayout mAppBar;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    //@BindView(R.id.toolbar_user_layout) View mToolbarUserField;
    //@BindView(R.id.toolbar_user_avatar) ImageView mToolbarAvatar;
    //@BindView(R.id.toolbar_user_nickname) TextView mToolbarNickname;
    //
    //@BindView(R.id.user_header_avatar) ImageView mHeaderAvatar;
    //@BindView(R.id.user_header_nickname) TextView mHeaderNickname;
    //@BindView(R.id.user_header_uid) TextView mHeaderUid;

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

        //mAppBar.addOnOffsetChangedListener(mOnOffsetChangeListener);
    }

    public void setToolbarAlpha(int alpha) {
        //mToolbarAvatar.getDrawable().setAlpha(alpha);
        //mToolbarNickname.setTextColor(Color.argb(alpha, 255, 255, 255));
        //mToolbarUserField mZhangdan.getDrawable().setAlpha(alpha);
        //mZhangdan_txt.setTextColor(Color.argb(alpha, 255, 255, 255));
        //mTongxunlu.getDrawable().setAlpha(alpha);
        //mJiahao.getDrawable().setAlpha(alpha);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mAppBar.removeOnOffsetChangedListener(mOnOffsetChangeListener);
    }
}
