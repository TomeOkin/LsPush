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
package com.tomeokin.lspush.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import com.tomeokin.lspush.R;

public class CollapsingUserHeaderLayout extends CollapsingToolbarLayout {
    private AppBarLayout.OnOffsetChangedListener mOnOffsetChangedListener;

    private View mToolbarUserLayout;
    private ImageView mToolbarAvatar;
    private TextView mToolbarNickname;

    private View mUserHeaderLayout;
    private ImageView mHeaderAvatar;
    private View mHeaderNameLayout;
    private TextView mHeaderNickname;
    private TextView mHeaderUid;

    public CollapsingUserHeaderLayout(Context context) {
        this(context, null);
    }

    public CollapsingUserHeaderLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollapsingUserHeaderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mToolbarUserLayout = findViewById(R.id.toolbar_user_layout);
        mToolbarAvatar = (ImageView) findViewById(R.id.toolbar_user_avatar);
        mToolbarNickname = (TextView) findViewById(R.id.toolbar_user_nickname);

        mUserHeaderLayout = findViewById(R.id.user_header_layout);
        mHeaderAvatar = (ImageView) findViewById(R.id.user_header_avatar);
        mHeaderNameLayout = findViewById(R.id.user_header_name_layout);
        mHeaderNickname = (TextView) findViewById(R.id.user_header_nickname);
        mHeaderUid = (TextView) findViewById(R.id.user_header_uid);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final ViewParent parent = getParent();
        if (parent instanceof AppBarLayout) {
            if (mOnOffsetChangedListener == null) {
                mOnOffsetChangedListener = new OnOffsetUpdateListener();
            }
            ((AppBarLayout) parent).addOnOffsetChangedListener(mOnOffsetChangedListener);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onDetachedFromWindow() {
        // Remove our OnOffsetChangedListener if possible and it exists
        final ViewParent parent = getParent();
        if (mOnOffsetChangedListener != null && parent instanceof AppBarLayout) {
            ((AppBarLayout) parent).removeOnOffsetChangedListener(mOnOffsetChangedListener);
        }
        super.onDetachedFromWindow();
    }

    public final class OnOffsetUpdateListener implements AppBarLayout.OnOffsetChangedListener {
        boolean mMeasure = false;

        int out[] = new int[2];
        Rect mHeaderAvatarLoc = new Rect();
        Rect mToolbarAvatarLoc = new Rect();

        float mScaleBaseline;
        float mTransitionX;
        float mTransitionY;

        Rect mHeaderNicknameLoc = new Rect();
        Rect mToolbarNicknameLoc = new Rect();
        float mTextScaleBaselineX;
        float mTextScaleBaselineY;
        float mTextTransitionX;
        float mTextTransitionY;

        float mMaxOffset;
        float mParallaxMultiplier;

        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            if (verticalOffset == 0) {
                if (!mMeasure) {
                    mMeasure = true;
                    mMaxOffset = appBarLayout.getTotalScrollRange();

                    initParallaxMultiplier();
                    initAvatar();
                    initNickname();
                }

                // 张开
                mToolbarUserLayout.setVisibility(View.INVISIBLE);
                updateAvatar(0, 0, 0, 1);
                mHeaderAvatar.setVisibility(View.VISIBLE);

                updateNickname(0, 0, 0, 1, 1);
                mHeaderNickname.setVisibility(View.VISIBLE);

                mHeaderUid.setTextColor(Color.argb(255, 255, 255, 255));
                mHeaderUid.setVisibility(View.VISIBLE);
            } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                // 收缩
                updateAvatar(mTransitionX, mTransitionY, 1, mScaleBaseline);
                mHeaderAvatar.setVisibility(View.INVISIBLE);

                updateNickname(mTextTransitionX, mTextTransitionY, 1, mTextScaleBaselineX, mTextScaleBaselineY);
                mHeaderNickname.setVisibility(View.INVISIBLE);

                mHeaderUid.setTextColor(Color.argb(0, 255, 255, 255));
                mHeaderUid.setVisibility(View.INVISIBLE);

                mToolbarUserLayout.setVisibility(View.VISIBLE);
            } else {
                mToolbarUserLayout.setVisibility(View.INVISIBLE);

                final float radio = -verticalOffset / mMaxOffset;
                final float r = (mMaxOffset + verticalOffset) / mMaxOffset;
                final float iScale = mScaleBaseline + (1 - mScaleBaseline) * r;
                updateAvatar(mTransitionX, mTransitionY, radio, iScale);
                mHeaderAvatar.setVisibility(View.VISIBLE);

                final float tScaleX = mTextScaleBaselineX + (1 - mTextScaleBaselineX) * r;
                final float tScaleY = mTextScaleBaselineY + (1 - mTextScaleBaselineY) * r;
                updateNickname(mTextTransitionX, mTextTransitionY, radio, tScaleX, tScaleY);
                mHeaderNickname.setVisibility(View.VISIBLE);

                mHeaderUid.setTextColor(Color.argb((int) (r * 255), 255, 255, 255));
                mHeaderUid.setVisibility(View.VISIBLE);
                //float ix = (48 - 128) * 2 * (-verticalOffset * 1f / appBarLayout.getTotalScrollRange());
                //Timber.i("TranslationX: %f", ix);
                //mHeaderAvatar.setTranslationX(ix);
                //mHeaderAvatar.setScaleX();
                //int alpha = 255 * Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange();
                //mToolbarUserLayout.setVisibility(View.VISIBLE);
                //setToolbarAlpha(alpha);
            }
        }

        private void initParallaxMultiplier() {
            LayoutParams lp = (LayoutParams) mUserHeaderLayout.getLayoutParams();
            if (lp.getCollapseMode() == LayoutParams.COLLAPSE_MODE_PARALLAX) {
                mParallaxMultiplier = lp.getParallaxMultiplier();
            } else {
                mParallaxMultiplier = 0;
            }
        }

        private void initAvatar() {
            mHeaderAvatar.getLocationOnScreen(out);
            mHeaderAvatarLoc.set(out[0], out[1], out[0] + mHeaderAvatar.getWidth(), out[1] + mHeaderAvatar.getHeight());

            mToolbarAvatar.getLocationOnScreen(out);
            mToolbarAvatarLoc.set(out[0], out[1], out[0] + mToolbarAvatar.getWidth(),
                out[1] + mToolbarAvatar.getHeight());

            mTransitionX = (mToolbarAvatarLoc.left + mToolbarAvatarLoc.width() / 2.0f) - (mHeaderAvatarLoc.left
                + mHeaderAvatarLoc.width() / 2.0f);
            mTransitionY = (mToolbarAvatarLoc.top - mToolbarAvatar.getPaddingTop()) - (mHeaderAvatarLoc.top
                - mHeaderAvatar.getPaddingTop());

            mScaleBaseline = 1.0f * mToolbarAvatar.getWidth() / mHeaderAvatar.getWidth();
        }

        private void initNickname() {
            mToolbarNickname.getLocationOnScreen(out);
            mToolbarNicknameLoc.set(out[0], out[1], out[0] + mToolbarNickname.getWidth(),
                out[1] + mToolbarNickname.getHeight());

            mHeaderNameLayout.getLocationOnScreen(out);
            mHeaderNicknameLoc.set(out[0], out[1], out[0] + mHeaderNickname.getWidth(),
                out[1] + mHeaderNickname.getHeight());

            mTextTransitionX = (mToolbarNicknameLoc.left + mToolbarNicknameLoc.width() / 2.0f + dp2px(8)) - (
                mHeaderNicknameLoc.left
                    + mHeaderNicknameLoc.width() / 2.0f
                    + dp2px(16));
            mTextTransitionY =
                (mToolbarNicknameLoc.top - mToolbarNickname.getPaddingTop()) - (mHeaderNicknameLoc.top - mHeaderNickname
                    .getPaddingTop());
            mTextTransitionY *= (1 - mParallaxMultiplier);

            mTextScaleBaselineX = 1.0f * mToolbarNickname.getWidth() / mHeaderNickname.getWidth();
            mTextScaleBaselineY = 1.0f * mToolbarNickname.getHeight() / mHeaderNickname.getHeight();
        }

        private void updateAvatar(float tx, float ty, float radioT, float scale) {
            updateView(mHeaderAvatar, tx, ty, radioT, scale, scale);
        }

        private void updateNickname(float tx, float ty, float radioT, float scaleX, float scaleY) {
            updateView(mHeaderNameLayout, tx, ty, radioT, scaleX, scaleY);
        }

        private void updateView(View view, float tx, float ty, float radioT, float scaleX, float scaleY) {
            view.setTranslationX(tx * radioT);
            view.setTranslationY(ty * radioT);
            view.setScaleX(scaleX);
            view.setScaleY(scaleY);
        }

        private float dp2px(float dp) {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        }
    }
}
