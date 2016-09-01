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
package com.tomeokin.lspush.biz.base;

import android.view.View;

import java.util.ArrayList;

public final class LifecycleListenerSupport {
    private final ArrayList<OnLifecycleListener> listeners;

    public LifecycleListenerSupport() {
        listeners = new ArrayList<>();
    }

    public final void register(OnLifecycleListener listener) {
        listeners.add(listener);
    }

    public final void registerAll(LifecycleListenerSupport listenerSupport) {
        listeners.addAll(listenerSupport.listeners);
    }

    public final void unregister(OnLifecycleListener listener) {
        listeners.remove(listener);
    }

    public final void onCreate() {
        for (int i = listeners.size() - 1; i >= 0; i--) {
            listeners.get(i).onCreate();
        }
    }

    public final void onCreateView(View view) {
        for (int i = listeners.size() - 1; i >= 0; i--) {
            listeners.get(i).onCreateView(view);
        }
    }

    public final void onResume() {
        for (int i = listeners.size() - 1; i >= 0; i--) {
            listeners.get(i).onResume();
        }
    }

    public final void onPause() {
        for (int i = listeners.size() - 1; i >= 0; i--) {
            listeners.get(i).onPause();
        }
    }

    public final void onDestroyView() {
        for (int i = listeners.size() - 1; i >= 0; i--) {
            listeners.get(i).onDestroyView();
        }
    }

    public final void onDestroy() {
        for (int i = listeners.size() - 1; i >= 0; i--) {
            listeners.get(i).onDestroy();
        }
    }
}
