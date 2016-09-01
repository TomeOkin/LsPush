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
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class SoftInputUtils {
    public static void hideInput(View view) {
        ((InputMethodManager) view.getContext().
            getSystemService(Context.INPUT_METHOD_SERVICE)).
            hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showInput(View view) {
        ((InputMethodManager) view.getContext().
            getSystemService(Context.INPUT_METHOD_SERVICE)).
            showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }
}
