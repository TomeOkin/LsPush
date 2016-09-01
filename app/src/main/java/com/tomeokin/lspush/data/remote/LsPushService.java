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
package com.tomeokin.lspush.data.remote;

import com.tomeokin.lspush.data.model.AccessResponse;
import com.tomeokin.lspush.data.model.BaseResponse;
import com.tomeokin.lspush.data.model.CaptchaRequest;
import com.tomeokin.lspush.data.model.CryptoToken;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LsPushService {
    @POST("api/user/login") Call<AccessResponse> login(@Body CryptoToken cryptoToken);

    @POST("api/user/refreshExpireToken") Call<AccessResponse> refreshExpireToken(@Body CryptoToken cryptoToken);

    @POST("api/user/refreshRefreshToken") Call<AccessResponse> refreshRefreshToken(@Body CryptoToken cryptoToken);

    @POST("api/user/register") Call<AccessResponse> register(@Body CryptoToken cryptoToken);

    @POST("api/user/sendCaptcha") Call<BaseResponse> sendCaptcha(@Body CaptchaRequest captchaRequest);

    @POST("api/user/checkCaptcha") Call<BaseResponse> checkCaptcha(@Body CryptoToken cryptoToken);
}