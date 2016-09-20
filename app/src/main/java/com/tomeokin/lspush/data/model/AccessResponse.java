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
package com.tomeokin.lspush.data.model;

import android.os.Parcel;

public class AccessResponse extends BaseResponse {
    // 表示截至的访问时间，超过该时间后 expireToken 无效，需要使用 refreshToken 来获取新的 expireToken，否则用户需要重新登录。
    private long expireTime;
    // 超过该刷新时间后，refreshToken 也需要重新获取一个新的，更新 refreshToken 需要以旧换新。
    private long refreshTime;
    // 提供 userId 供后续使用
    private String userId;

    private CryptoToken expireToken;
    private CryptoToken refreshToken;

    public AccessResponse() {
        super();
    }

    public AccessResponse(int errorCode, String result) {
        super(errorCode, result);
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public long getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(long refreshTime) {
        this.refreshTime = refreshTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public CryptoToken getExpireToken() {
        return expireToken;
    }

    public void setExpireToken(CryptoToken expireToken) {
        this.expireToken = expireToken;
    }

    public CryptoToken getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(CryptoToken refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(this.expireTime);
        dest.writeLong(this.refreshTime);
        dest.writeString(this.userId);
        dest.writeParcelable(this.expireToken, flags);
        dest.writeParcelable(this.refreshToken, flags);
    }

    protected AccessResponse(Parcel in) {
        super(in);
        this.expireTime = in.readLong();
        this.refreshTime = in.readLong();
        this.userId = in.readString();
        this.expireToken = in.readParcelable(CryptoToken.class.getClassLoader());
        this.refreshToken = in.readParcelable(CryptoToken.class.getClassLoader());
    }

    public static final Creator<AccessResponse> CREATOR = new Creator<AccessResponse>() {
        @Override
        public AccessResponse createFromParcel(Parcel source) {return new AccessResponse(source);}

        @Override
        public AccessResponse[] newArray(int size) {return new AccessResponse[size];}
    };
}
