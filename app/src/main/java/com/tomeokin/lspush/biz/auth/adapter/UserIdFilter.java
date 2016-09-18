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
package com.tomeokin.lspush.biz.auth.adapter;

import android.text.LoginFilter;

public class UserIdFilter extends LoginFilter.UsernameFilterGeneric {
    private final int mRequestId;
    private final FilterCallback mCallback;

    public UserIdFilter(int requestId, FilterCallback callback) {
        this(requestId, callback, false);
    }

    public UserIdFilter(int requestId, FilterCallback callback, boolean appendInvalid) {
        super(appendInvalid);
        mRequestId = requestId;
        mCallback = callback;
    }

    @Override
    public boolean isAllowed(char c) {
        if ('0' <= c && c <= '9') return true;
        if ('a' <= c && c <= 'z') return true;
        if ('A' <= c && c <= 'Z') return true;
        // un-support another characters
        return false;
    }

    @Override
    public void onInvalidCharacter(char c) {
        super.onInvalidCharacter(c);
        mCallback.onInvalidCharacter(mRequestId, c);
    }
}
