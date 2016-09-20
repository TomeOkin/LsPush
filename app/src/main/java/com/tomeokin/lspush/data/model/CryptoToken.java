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

public class CryptoToken implements Parcelable {
    public String key;
    public String param;
    public String value;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.param);
        dest.writeString(this.value);
    }

    public CryptoToken() {}

    protected CryptoToken(Parcel in) {
        this.key = in.readString();
        this.param = in.readString();
        this.value = in.readString();
    }

    public static final Parcelable.Creator<CryptoToken> CREATOR = new Parcelable.Creator<CryptoToken>() {
        @Override
        public CryptoToken createFromParcel(Parcel source) {return new CryptoToken(source);}

        @Override
        public CryptoToken[] newArray(int size) {return new CryptoToken[size];}
    };
}
