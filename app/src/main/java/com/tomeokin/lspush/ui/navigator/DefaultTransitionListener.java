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
package com.tomeokin.lspush.ui.navigator;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public abstract class DefaultTransitionListener implements TransitionHelper.OnTransitionListener {
    @Override
    public AnimatorSet createAnimation(View copy, TransitionHelper.TransitionOption option) {
        float translationX =
            option.source.left + option.source.width() / 2.0f - (option.target.left + option.target.width() / 2.0f);
        float translationY =
            option.source.top + option.source.height() / 2.0f - (option.target.top + option.target.height() / 2.0f);

        Log.v("trans-helper", "translationX: " + translationX);
        Log.v("trans-helper", "translationY: " + translationY);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(copy, "translationX", 0, -translationX),
            ObjectAnimator.ofFloat(copy, "translationY", 0, -translationY),
            ObjectAnimator.ofFloat(copy, "scaleX", (float) option.source.width() / option.target.width(), 1),
            ObjectAnimator.ofFloat(copy, "scaleY", (float) option.source.height() / option.target.height(), 1));
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.setDuration(500);
        return animatorSet;
    }
}
