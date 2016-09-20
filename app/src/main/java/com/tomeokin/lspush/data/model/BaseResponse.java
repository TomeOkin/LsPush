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
import android.os.Parcelable;

public class BaseResponse implements Parcelable {
    public static final int COMMON_SUCCESS = 0;
    public static final String COMMON_SUCCESS_MESSAGE = "success";

    protected int resultCode;
    protected String result;

    public BaseResponse() {
        resultCode = COMMON_SUCCESS;
        result = COMMON_SUCCESS_MESSAGE;
    }

    public BaseResponse(int resultCode, String result) {
        this.resultCode = resultCode;
        this.result = result;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.resultCode);
        dest.writeString(this.result);
    }

    protected BaseResponse(Parcel in) {
        this.resultCode = in.readInt();
        this.result = in.readString();
    }

    public static final Parcelable.Creator<BaseResponse> CREATOR = new Parcelable.Creator<BaseResponse>() {
        @Override
        public BaseResponse createFromParcel(Parcel source) {return new BaseResponse(source);}

        @Override
        public BaseResponse[] newArray(int size) {return new BaseResponse[size];}
    };
}
