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
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.tomeokin.lspush.R;

public class NotificationBar extends AppCompatTextView {
    public static final int STATE_DOWN_SHORT = 1;
    public static final int STATE_DOWN_REMAIN = 2;
    public static final int STATE_OVER = 3;

    private Animation mSlideDownAnim;
    private Animation mSlideUpAnim;
    private int mState = 3;
    private final Runnable mHideRunnable = new Runnable() {
        @Override public void run() {
            sideUp();
        }
    };

    public NotificationBar(Context context) {
        super(context);
        init(context);
    }

    public NotificationBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NotificationBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mSlideDownAnim = AnimationUtils.loadAnimation(context, R.anim.notification_slide_down);
        mSlideUpAnim = AnimationUtils.loadAnimation(context, R.anim.notification_slide_up);
    }

    public final void showTemporaryInverse(String message) {
        showTemporary(message, ContextCompat.getColor(getContext(), R.color.error_state), true);
    }

    public final void showTemporary(String message, @ColorInt int color, boolean inverse) {
        if (mState == STATE_OVER) {
            mState = STATE_DOWN_SHORT;
            perform(message, color, inverse);
            postDelayed(mHideRunnable, 3000);
        }
    }

    public final void showRemain(String message, @ColorInt int color, boolean inverse) {
        removeCallbacks(mHideRunnable);
        if (mState != STATE_OVER) {
            sideUp();
        }
        mState = STATE_DOWN_REMAIN;
        perform(message, color, inverse);
    }

    private void perform(String message, @ColorInt int color, boolean inverse) {
        if (inverse) {
            setText(message);
            setTextColor(color);
            setBackground(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.white)));
        } else {
            setText(message);
            setBackground(new ColorDrawable(color));
        }
        startAnimation(mSlideDownAnim);
    }

    public final void cancel() {
        if (mState != STATE_OVER) {
            removeCallbacks(this.mHideRunnable);
            sideUp();
        }
    }

    public void sideUp() {
        startAnimation(mSlideUpAnim);
        mState = STATE_OVER;
    }
}
