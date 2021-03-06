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
import com.tomeokin.lspush.data.model.Collection;
import com.tomeokin.lspush.data.model.CollectionBinding;
import com.tomeokin.lspush.data.model.CollectionResponse;
import com.tomeokin.lspush.data.model.CryptoToken;
import com.tomeokin.lspush.data.model.UploadResponse;
import com.tomeokin.lspush.data.model.UrlCollectionResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface LsPushService {
    @POST("api/user/login")
    Call<AccessResponse> login(@Body CryptoToken cryptoToken);

    @POST("api/user/login")
    Observable<AccessResponse> loginObservable(@Body CryptoToken cryptoToken);

    @POST("api/user/refreshExpireToken")
    Observable<AccessResponse> refreshExpireToken(@Body CryptoToken cryptoToken);

    @POST("api/user/refreshRefreshToken")
    Observable<AccessResponse> refreshRefreshToken(@Body CryptoToken cryptoToken);

    @POST("api/user/register")
    Call<AccessResponse> register(@Body CryptoToken cryptoToken);

    @POST("api/user/sendCaptcha")
    Call<BaseResponse> sendCaptcha(@Body CaptchaRequest captchaRequest);

    @POST("api/user/checkCaptcha")
    Call<BaseResponse> checkCaptcha(@Body CryptoToken cryptoToken);

    @GET("api/user/checkUIDExisted/{uid}")
    Call<BaseResponse> checkUIDExisted(@Path("uid") String uid);

    /**
     * resourceType: 1: avatar，2：other
     */
    @Multipart
    @POST("api/resource/upload/{resourceType}")
    Call<UploadResponse> upload(@Path("resourceType") int resourceType, @Part("description") RequestBody description,
        @Part MultipartBody.Part image);

    @POST("api/collection/post")
    Call<BaseResponse> postCollection(@Header(value = "token") String token, @Body Collection collection);

    @GET("api/collection/get")
    Call<CollectionResponse> getCollections(@Query("uid") String uid, @Query("page") int page, @Query("size") int size);

    @GET("api/collection/getByUrl")
    Call<CollectionResponse> getCollectionByUrl(@Query("uid") String uid, @Query("url") String url,
        @Query("page") int page, @Query("size") int size);

    @GET("api/collection/getLatest")
    Call<CollectionResponse> getLatestCollections(@Query("uid") String uid, @Query("page") int page,
        @Query("size") int size);

    @POST("/api/favor/set")
    Call<BaseResponse> addFavor(@Header(value = "token") String token, @Body CollectionBinding colBinding);

    @POST("/api/favor/remove")
    Call<BaseResponse> removeFavor(@Header(value = "token") String token, @Body long colId);

    @GET("/api/link/fetch")
    Call<UrlCollectionResponse> getUrlInfo(@Header(value = "token") String token, @Query("url") String url);
}