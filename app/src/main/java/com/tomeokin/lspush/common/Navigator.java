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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.tomeokin.lspush.R;

public class Navigator {
    public static void moveTo(Fragment base, Class<? extends Fragment> fragment, Bundle args) {
        moveTo(base.getContext(), base.getFragmentManager(), fragment, args);
    }

    public static void moveTo(FragmentActivity base, Class<? extends Fragment> fragment, Bundle args) {
        moveTo(base, base.getSupportFragmentManager(), fragment, args);
    }

    public static void moveTo(Context context, FragmentManager fragmentManager, Class<? extends Fragment> fragment,
        Bundle args) {
        final String tag = fragment.getName();
        Fragment current = fragmentManager.findFragmentById(R.id.fragment_container);
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
                transaction.add(R.id.fragment_container, target, tag);
            } else {
                transaction.replace(R.id.fragment_container, target, tag);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.addToBackStack(tag);
            }
            transaction.commit();
        } else {
            if (current == target) {
                return;
            }
            fragmentManager.popBackStackImmediate(tag, 0);
        }
    }
}
