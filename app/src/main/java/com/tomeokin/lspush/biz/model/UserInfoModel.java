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
package com.tomeokin.lspush.biz.model;

import java.util.Arrays;

public class UserInfoModel {
    public static final int USER_ID_MAX_LENGTH = 24;
    public static final int USER_NAME_MAX_LENGTH = 30;
    public static final int USER_PASSWORD_MAX_LENGTH = 24;
    public static final int USER_ID_MIN_LENGTH = 3;
    public static final int USER_NAME_MIN_LENGTH = 3;
    public static final int USER_PASSWORD_MIN_LENGTH = 6;

    public static final String PASSWORD_SPECIAL = ".,;";
    public static final char[] PASSWORD_SPECIAL_SORT = PASSWORD_SPECIAL.toCharArray();

    static {
        Arrays.sort(PASSWORD_SPECIAL_SORT);
    }
}
