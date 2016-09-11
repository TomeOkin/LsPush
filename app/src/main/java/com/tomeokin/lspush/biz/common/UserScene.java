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
package com.tomeokin.lspush.biz.common;

public class UserScene {
    public static final String REGISTER = "register";
    public static final String SEND_CAPTCHA = "send-captcha";
    public static final String CHECK_CAPTCHA = "check-captcha";

    public static final int ACTION_SEND_CAPTCHA = 0;
    public static final int ACTION_CHECK_CAPTCHA = 1;
    public static final int ACTION_CHECK_UID = 2;
    public static final int ACTION_REGISTER = 3;
    public static final int ACTION_LOGIN = 4;
}
