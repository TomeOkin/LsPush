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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import static android.content.Context.WINDOW_SERVICE;

public class TransitionHelper {
    private static final String ARG_TRANSITION_OPTION = "arg.transition.option";

    public static void moveTo(@NonNull final Activity source, @NonNull Class<? extends Activity> target,
        @Nullable Bundle args, @NonNull final View view) {
        final Intent intent = new Intent(source, target);
        if (args != null) {
            intent.putExtras(args);
        }
        view.post(new Runnable() {
            @Override
            public void run() {
                Bundle option = TransitionOption.from(view);
                intent.putExtra(ARG_TRANSITION_OPTION, option);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                source.startActivity(intent);
                source.overridePendingTransition(0, 0);
            }
        });
    }

    public static void transition(@NonNull final Activity target, @NonNull Intent intent, final View view,
        final OnTransitionListener listener) {
        final Bundle bundle = intent.getBundleExtra(ARG_TRANSITION_OPTION);

        final ViewGroup windowContent = (ViewGroup) target.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        windowContent.post(new Runnable() {
            @Override
            public void run() {
                final FrameLayout.LayoutParams params =
                    new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                FrameLayout container = new FrameLayout(target);
                container.setBackgroundColor(Color.TRANSPARENT);
                windowContent.addView(container, params);

                final TransitionOption option = new TransitionOption(target, bundle, view);
                int targetWidth = option.target.width();
                int targetHeight = option.target.height();

                final View moveView = listener.createCopyView(target);
                final FrameLayout.LayoutParams viewParams = new FrameLayout.LayoutParams(targetWidth, targetHeight);
                int marginLeft = option.source.left - (int) (targetWidth / 2.0f - option.source.width() / 2.0f);
                int marginTop = option.source.top
                    - (int) (targetHeight / 2.0f - option.source.height() / 2.0f)
                    - option.unreachableTop;
                viewParams.setMargins(marginLeft, marginTop, 0, 0);
                Log.v("trans-helper", "marginLeft: " + marginLeft);
                Log.v("trans-helper", "marginTop: " + marginTop);
                container.addView(moveView, viewParams);

                AnimatorSet animatorSet = listener.createAnimation(moveView, option);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        listener.onTransitionEnd(moveView, view);
                    }
                });
                animatorSet.start();
                listener.onTransitionStart(moveView);
            }
        });
    }

    public interface OnTransitionListener {
        View createCopyView(Context context);

        AnimatorSet createAnimation(View copy, TransitionOption option);

        void onTransitionStart(View copy);

        void onTransitionEnd(View copy, View target);
    }

    public static class TransitionOption {
        private static final String ARG_SOURCE_LOCATION = "transition.arg.source";

        int unreachableTop; // 无法用于绘制的区域，包括状态栏和标题栏
        public Rect source;
        public Rect target;

        TransitionOption(@NonNull Activity activity, @NonNull Bundle options, @NonNull View view) {
            WindowManager wm = (WindowManager) activity.getSystemService(WINDOW_SERVICE);
            Point size = new Point();
            wm.getDefaultDisplay().getSize(size); // 除了导航栏外其他部分的宽高

            final View content = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
            unreachableTop = size.y - content.getHeight();
            Log.v("trans-helper", "unreachableTop: " + unreachableTop);

            source = options.getParcelable(ARG_SOURCE_LOCATION);
            if (source == null) {
                throw new IllegalArgumentException("Options not contain all required args! "
                    + "This only use for those activity who start by TransitionHelper.moveTo");
            }

            target = measure(view);
            Log.v("trans-helper", "target view: " + target.toString());
        }

        public static Bundle from(View view) {
            Rect source = measure(view);
            Log.v("trans-helper", "source view: " + source.toString());

            Bundle bundle = new Bundle();
            bundle.putParcelable(ARG_SOURCE_LOCATION, source);
            return bundle;
        }

        private static Rect measure(View view) {
            int out[] = new int[2];
            view.getLocationOnScreen(out);
            return new Rect(out[0], out[1], out[0] + view.getWidth(), out[1] + view.getHeight());
        }
    }
}
