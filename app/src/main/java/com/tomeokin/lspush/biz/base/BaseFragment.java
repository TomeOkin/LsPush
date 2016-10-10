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

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;

import com.tomeokin.lspush.biz.base.lifecycle.LifecycleDispatcherManager;
import com.tomeokin.lspush.biz.base.lifecycle.LifecycleListenerSupport;
import com.tomeokin.lspush.biz.base.lifecycle.OnLifecycleListener;
import com.tomeokin.lspush.injection.ProvideComponent;

import java.util.ArrayList;

public abstract class BaseFragment extends Fragment implements LifecycleDispatcherManager {
    private final LifecycleListenerSupport listenerSupport;

    public BaseFragment() {
        listenerSupport = new LifecycleListenerSupport();
    }

    public void dispatchOnCreateView(View view) {
        if (view != null) {
            listenerSupport.onCreateView(view);
        }
    }

    public void dispatchOnResume() {
        listenerSupport.onResume();
    }

    public void dispatchOnPause() {
        listenerSupport.onPause();
    }

    public void dispatchOnDestroyView() {
        listenerSupport.onDestroyView();
    }

    public void registerAll(LifecycleListenerSupport listenerSupport) {
        listenerSupport.registerAll(listenerSupport);
    }

    @Override
    public void registerLifecycleListener(OnLifecycleListener listener) {
        listenerSupport.register(listener);
    }

    public void unregisterLifecycleListener(OnLifecycleListener listener) {
        listenerSupport.unregister(listener);
    }

    public Activity getParentActivity() {
        Activity parent = getActivity().getParent();
        return parent == null ? getActivity() : parent;
    }

    /**
     * Gets a component for dependency injection by its type.
     */
    @SuppressWarnings("unchecked")
    protected <C> C component(Class<C> componentType) {
        return componentType.cast(((ProvideComponent<C>) getActivity()).component());
    }

    public boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getContext().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }
        // other
        return true;
    }

    public boolean hasPermissions(String[] permissions) {
        for (String permission : permissions) {
            if (!hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    public String[] needPermissions(@NonNull String[] permissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : permissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result.toArray(new String[result.size()]);
    }
}
