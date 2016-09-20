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
package com.tomeokin.lspush.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.tomeokin.lspush.R;

public class Navigator {
    @IdRes private static final int DEFAULT_LAYOUT_ID = R.id.fragment_container;
    private static final boolean DEFAULT_ADD_TO_BACK_STACK = true;
    public static final int REQUEST_CODE = 72;

    private Navigator() {}

    public static void moveTo(Fragment base, Class<? extends Fragment> fragment, Bundle args) {
        moveTo(base.getContext(), base.getFragmentManager(), fragment, args, DEFAULT_LAYOUT_ID,
            DEFAULT_ADD_TO_BACK_STACK);
    }

    public static void moveTo(Fragment base, Class<? extends Fragment> fragment, Bundle args, boolean addToBackStack) {
        moveTo(base.getContext(), base.getFragmentManager(), fragment, args, DEFAULT_LAYOUT_ID, addToBackStack);
    }

    public static void moveTo(Fragment base, Class<? extends Fragment> fragment, Bundle args, @IdRes int id) {
        moveTo(base.getContext(), base.getFragmentManager(), fragment, args, id, DEFAULT_ADD_TO_BACK_STACK);
    }

    public static void moveTo(Fragment base, Class<? extends Fragment> fragment, Bundle args, @IdRes int id,
        boolean addToBackStack) {
        moveTo(base.getContext(), base.getFragmentManager(), fragment, args, id, addToBackStack);
    }

    public static void moveTo(FragmentActivity base, Class<? extends Fragment> fragment, Bundle args) {
        moveTo(base, base.getSupportFragmentManager(), fragment, args, DEFAULT_LAYOUT_ID, DEFAULT_ADD_TO_BACK_STACK);
    }

    public static void moveTo(FragmentActivity base, Class<? extends Fragment> fragment, Bundle args,
        boolean addToBackStack) {
        moveTo(base, base.getSupportFragmentManager(), fragment, args, DEFAULT_LAYOUT_ID, addToBackStack);
    }

    public static void moveTo(FragmentActivity base, Class<? extends Fragment> fragment, Bundle args, @IdRes int id) {
        moveTo(base, base.getSupportFragmentManager(), fragment, args, id, DEFAULT_ADD_TO_BACK_STACK);
    }

    public static void moveTo(FragmentActivity base, Class<? extends Fragment> fragment, Bundle args, @IdRes int id,
        boolean addToBackStack) {
        moveTo(base, base.getSupportFragmentManager(), fragment, args, id, addToBackStack);
    }

    public static void moveTo(Context context, FragmentManager fragmentManager, Class<? extends Fragment> fragment,
        Bundle args) {
        moveTo(context, fragmentManager, fragment, args, DEFAULT_LAYOUT_ID, DEFAULT_ADD_TO_BACK_STACK);
    }

    public static void moveTo(Context context, FragmentManager fragmentManager, Class<? extends Fragment> fragment,
        Bundle args, boolean addToBackStack) {
        moveTo(context, fragmentManager, fragment, args, DEFAULT_LAYOUT_ID, addToBackStack);
    }

    public static void moveTo(Context context, FragmentManager fragmentManager, Class<? extends Fragment> fragment,
        Bundle args, @IdRes int id) {
        moveTo(context, fragmentManager, fragment, args, id, DEFAULT_ADD_TO_BACK_STACK);
    }

    public static void moveTo(Context context, FragmentManager fragmentManager, Class<? extends Fragment> fragment,
        Bundle args, @IdRes int containerId, boolean addToBackStack) {
        final String tag = fragment.getName();
        Fragment current = fragmentManager.findFragmentById(containerId);
        Fragment target = fragmentManager.findFragmentByTag(tag);

        if (target == null) {
            try {
                target = Fragment.instantiate(context, fragment.getName(), args);
            } catch (Exception e) {
                // ignore
            }
            if (target == null) {
                return;
            }

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (current == null) {
                transaction.add(containerId, target, tag);
            } else {
                transaction.replace(containerId, target, tag);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                if (addToBackStack) {
                    transaction.addToBackStack(tag);
                }
            }
            transaction.commit();
        } else {
            if (current == target) {
                return;
            }
            // set result
            Intent intent = new Intent();
            intent.putExtras(args);
            target.onActivityResult(REQUEST_CODE, Activity.RESULT_OK, intent);
            fragmentManager.popBackStackImmediate(tag, 0);
        }
    }
}
