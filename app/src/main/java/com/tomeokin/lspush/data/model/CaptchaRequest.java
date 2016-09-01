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

public class CaptchaRequest implements Parcelable {
    private String sendObject;
    private String region; // 地区，request when object is phone

    public String getSendObject() {
        return sendObject;
    }

    public void setSendObject(String sendObject) {
        this.sendObject = sendObject;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CaptchaRequest request = (CaptchaRequest) o;

        if (!sendObject.equals(request.sendObject)) return false;
        return region != null ? region.equals(request.region) : request.region == null;

    }

    @Override public int hashCode() {
        int result = sendObject.hashCode();
        result = 31 * result + (region != null ? region.hashCode() : 0);
        return result;
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sendObject);
        dest.writeString(this.region);
    }

    public CaptchaRequest() {}

    protected CaptchaRequest(Parcel in) {
        this.sendObject = in.readString();
        this.region = in.readString();
    }

    public static final Parcelable.Creator<CaptchaRequest> CREATOR = new Parcelable.Creator<CaptchaRequest>() {
        @Override public CaptchaRequest createFromParcel(Parcel source) {return new CaptchaRequest(source);}

        @Override public CaptchaRequest[] newArray(int size) {return new CaptchaRequest[size];}
    };
}
