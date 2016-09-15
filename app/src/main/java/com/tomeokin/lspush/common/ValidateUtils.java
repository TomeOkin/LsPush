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

import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Pattern;

public class ValidateUtils {
    private ValidateUtils() {}

    private static final String PHONE_NUMBER_REGEX = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile(PHONE_NUMBER_REGEX);

    public static boolean isPhoneValid(String phone, String region) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        }
        final String str = PhoneNumberUtils.stripSeparators(phone);
        if (region.equals("CN")) {
            return PHONE_NUMBER_PATTERN.matcher(str).matches();
        }
        return str.length() >= 7;
    }

    public static boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches();
    }
}
