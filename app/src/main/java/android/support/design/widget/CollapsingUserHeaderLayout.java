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
package android.support.design.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import com.tomeokin.lspush.R;

import java.util.Arrays;

import timber.log.Timber;

public class CollapsingUserHeaderLayout extends CollapsingToolbarLayout {
    private AppBarLayout.OnOffsetChangedListener mOnOffsetChangedListener;
    //private com.tomeokin.lspush.util.CollapsingTextHelper mTextHelper;

    private View mToolbarUserLayout;
    private ImageView mToolbarAvatar;
    private TextView mToolbarNickname;

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
        //mTextHelper.setText(title);
        //
        //mTextHelper.setCollapsedBounds(titleInsetStart,
        //    0,
        //    width - titleInsetEnd,
        //    (int) collapsedHeight);
        //
        //mTextHelper.setExpandedBounds(titleInsetStart,
        //    (int) titleInsetTop,
        //    width - titleInsetEnd,
        //    getMinimumHeight() - titleInsetBottom);
        //mTextHelper.setCollapsedTextColor(paint.getColor());
        //mTextHelper.setExpandedTextColor(paint.getColor());
        //mTextHelper.setCollapsedTextSize(collapsedTextSize);
        //
        //int expandedTitleTextSize = (int) Math.max(collapsedTextSize,
        //    ViewUtils.getSingleLineTextSize(displayText.toString(), paint,
        //        width - titleInsetStart - titleInsetEnd,
        //        collapsedTextSize,
        //        maxExpandedTextSize, 0.5f, getResources().getDisplayMetrics()));
        //mTextHelper.setExpandedTextSize(expandedTitleTextSize);
        //
        //
        //mTextHelper.setTypeface(paint.getTypeface());
        //
        //
        //TypedArray a = context.obtainStyledAttributes(attrs, android.support.design.R.styleable.CollapsingToolbarLayout,
        //    defStyleAttr, android.support.design.R.style.Widget_Design_CollapsingToolbar);
        //
        //mTextHelper.setExpandedTextGravity(mCollapsingTextHelper.getExpandedTextGravity());
        //mCollapsingTextHelper.setExpandedTextGravity(
        //    a.getInt(android.support.design.R.styleable.CollapsingToolbarLayout_expandedTitleGravity,
        //        GravityCompat.START | Gravity.BOTTOM));
        //
        //mTextHelper.setCollapsedTextGravity(mCollapsingTextHelper.getCollapsedTextGravity());
        //mCollapsingTextHelper.setCollapsedTextGravity(
        //    a.getInt(android.support.design.R.styleable.CollapsingToolbarLayout_collapsedTitleGravity,
        //        GravityCompat.START | Gravity.CENTER_VERTICAL));
        //
        //mTextHelper.setExpandedTextColor(mCollapsingTextHelper.getExpandedTextColor());
        //mTextHelper.setExpandedTextSize(mCollapsingTextHelper.getExpandedTextSize());
        //
        //// First load the default text appearances
        //mCollapsingTextHelper.setExpandedTextAppearance(
        //    android.support.design.R.style.TextAppearance_Design_CollapsingToolbar_Expanded);
        //mCollapsingTextHelper.setCollapsedTextAppearance(
        //    android.support.v7.appcompat.R.style.TextAppearance_AppCompat_Widget_ActionBar_Title);
        //
        //// Now overlay any custom text appearances
        //if (a.hasValue(android.support.design.R.styleable.CollapsingToolbarLayout_expandedTitleTextAppearance)) {
        //    mCollapsingTextHelper.setExpandedTextAppearance(
        //        a.getResourceId(android.support.design.R.styleable.CollapsingToolbarLayout_expandedTitleTextAppearance,
        //            0));
        //}
        //if (a.hasValue(android.support.design.R.styleable.CollapsingToolbarLayout_collapsedTitleTextAppearance)) {
        //    mCollapsingTextHelper.setCollapsedTextAppearance(
        //        a.getResourceId(android.support.design.R.styleable.CollapsingToolbarLayout_collapsedTitleTextAppearance,
        //            0));
        //}
        //
        //a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mToolbarUserLayout = findViewById(R.id.toolbar_user_layout);
        mToolbarAvatar = (ImageView) findViewById(R.id.toolbar_user_avatar);
        mToolbarNickname = (TextView) findViewById(R.id.toolbar_user_nickname);

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
        //mTextHelper.draw(canvas);
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

        float mHeaderNicknameTextSize;
        float mToolbarNicknameTextSize;
        float mTextSizeDiff;

        Rect mHeaderNicknameLoc = new Rect();
        Rect mToolbarNicknameLoc = new Rect();
        float mTextScaleBaselineX;
        float mTextScaleBaselineY;
        float mTextTransitionX;
        float mTextTransitionY;

        float mMaxOffset;

        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            if (verticalOffset == 0) {
                if (!mMeasure) {
                    mMeasure = true;
                    mHeaderAvatar.getLocationOnScreen(out);
                    mHeaderAvatarLoc.set(out[0], out[1], out[0] + mHeaderAvatar.getWidth(),
                        out[1] + mHeaderAvatar.getHeight());

                    mToolbarAvatar.getLocationOnScreen(out);
                    mToolbarAvatarLoc.set(out[0], out[1], out[0] + mToolbarAvatar.getWidth(),
                        out[1] + mToolbarAvatar.getHeight());

                    mTransitionX = (mToolbarAvatarLoc.left + mToolbarAvatarLoc.width() / 2.0f) - (mHeaderAvatarLoc.left
                        + mHeaderAvatarLoc.width() / 2.0f);
                    mTransitionY =
                        (mToolbarAvatarLoc.top - mToolbarAvatar.getPaddingTop()) - (mHeaderAvatarLoc.top - mHeaderAvatar
                            .getPaddingTop());

                    mScaleBaseline = 1.0f * mToolbarAvatar.getWidth() / mHeaderAvatar.getWidth();

                    mMaxOffset = appBarLayout.getTotalScrollRange();

                    mHeaderNicknameTextSize = mHeaderNickname.getTextSize();
                    mToolbarNicknameTextSize = mToolbarNickname.getTextSize();
                    mTextSizeDiff = mToolbarNicknameTextSize - mHeaderNicknameTextSize;

                    //mTextHelper = new CollapsingTextHelper(CollapsingUserHeaderLayout.this);
                    //
                    //mTextHelper.setCollapsedTextColor(Color.WHITE);
                    //mTextHelper.setExpandedTextColor(Color.WHITE);
                    //mTextHelper.setCollapsedTextSize(mToolbarNicknameTextSize);
                    //mTextHelper.setExpandedTextSize(mHeaderNicknameTextSize);
                    //
                    //mTextHelper.setText(mHeaderNickname.getText());

                    mToolbarNickname.getLocationOnScreen(out);
                    mToolbarNicknameLoc.set(out[0], out[1], out[0] + mToolbarNickname.getWidth(),
                        out[1] + mToolbarNickname.getHeight());
                    Timber.i("mToolbarNicknameLoc %s", mToolbarNicknameLoc.toString()); // Rect(176, 87 - 331, 125)

                    //mTextHelper.setCollapsedBounds(out[0], out[1], out[0] + mToolbarNickname.getWidth(),
                    //    out[1] + mToolbarNickname.getHeight());

                    //Rect rect = new Rect();
                    mHeaderNameLayout.getLocationOnScreen(out);
                    Timber.i("mHeaderNameLayout.getLocationInWindow %s", Arrays.toString(out)); // [275, 127]

                    mHeaderNickname.getLocationOnScreen(out);
                    mHeaderNicknameLoc.set(out[0], out[1], out[0] + mHeaderNickname.getWidth(),
                        out[1] + mHeaderNickname.getHeight()); // Rect(275, 135 - 456, 173)
                    Timber.i("mHeaderNicknameLoc %s", mHeaderNicknameLoc.toString());

                    //mTextHelper.setExpandedBounds(out[0], out[1], out[0] + mHeaderNickname.getWidth(),
                    //    out[1] + mHeaderNickname.getHeight());

                    //mTextHelper.setCollapsedTextGravity(mToolbarNickname.getGravity());
                    //mTextHelper.setExpandedTextGravity(mHeaderNickname.getGravity());
                    //
                    //mTextHelper.setTextSizeInterpolator(AnimationUtils.DECELERATE_INTERPOLATOR);
                    //mTextHelper.recalculate();

                    mTextTransitionX = (mToolbarNicknameLoc.left + mToolbarNicknameLoc.width() / 2.0f) - (
                        mHeaderNicknameLoc.left
                            + mHeaderNicknameLoc.width() / 2.0f);
                    mTextTransitionX -= 16;
                    Timber.i("mTextTransitionX %f", mTextTransitionX); // -128.000000
                    mTextTransitionY = (mToolbarNicknameLoc.top - mToolbarNickname.getPaddingTop()) - (
                        mHeaderNicknameLoc.top
                            - mHeaderNickname.getPaddingTop()) + 32;
                    mTextTransitionY /= 2;

                    Timber.i("mTextTransitionY %f", mTextTransitionY); // -8.000000

                    mTextScaleBaselineX = 1.0f * mToolbarNickname.getWidth() / mHeaderNickname.getWidth();
                    mTextScaleBaselineY = 1.0f * mToolbarNickname.getHeight() / mHeaderNickname.getHeight();
                    Timber.i("mTextScaleBaselineX %f", mTextScaleBaselineX);
                    Timber.i("mTextScaleBaselineY %f", mTextScaleBaselineY);
                }

                // 张开
                mToolbarUserLayout.setVisibility(View.INVISIBLE);
                updateAvatar(0, 0, 0, 1);
                mHeaderAvatar.setVisibility(View.VISIBLE);

                //mHeaderNickname.setTextSize(TypedValue.COMPLEX_UNIT_PX, mHeaderNicknameTextSize);
                updateNickname(0, 0, 0, 1, 1);
                mHeaderNickname.setVisibility(View.VISIBLE);

                mHeaderUid.setTextColor(Color.argb(255, 255, 255, 255));
                mHeaderUid.setVisibility(View.VISIBLE);
            } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                // 收缩
                updateAvatar(mTransitionX, mTransitionY, 1, mScaleBaseline);
                mHeaderAvatar.setVisibility(View.INVISIBLE);

                //mHeaderNickname.setTextSize(TypedValue.COMPLEX_UNIT_PX, mToolbarNicknameTextSize);
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

                // TODO: 2016/11/12

                //final float textSize = mHeaderNicknameTextSize + mTextSizeDiff * radio;
                //Timber.i("mTextSizeDiff * radio %f", mTextSizeDiff * radio);
                //mHeaderNickname.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

                final float tScaleX = mTextScaleBaselineX + (1 - mTextScaleBaselineX) * r;
                final float tScaleY = mTextScaleBaselineY + (1 - mTextScaleBaselineY) * r;
                updateNickname(mTextTransitionX, mTextTransitionY, radio, tScaleX, tScaleY);
                Timber.i("tScaleX %f", tScaleX);
                Timber.i("tScaleY %f", tScaleY);
                Timber.i("current ty %f", mTextTransitionY * radio);
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
    }
}
