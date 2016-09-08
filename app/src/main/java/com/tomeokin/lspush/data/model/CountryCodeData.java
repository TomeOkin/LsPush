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
import android.support.annotation.NonNull;

public class CountryCodeData implements Parcelable, Comparable<CountryCodeData> {
    public final String countryCode; // e.g. 86
    public final String country; // e.g. CN
    private final String displayString; // e.g. 中国

    public CountryCodeData(String countryCode, String displayString, String country) {
        this.countryCode = countryCode;
        this.displayString = displayString;
        this.country = country;
    }

    public final String formatCountryCode() {
        return "+" + countryCode;
    }

    public final String formatSimple() {
        return String.format("%s +%s", country, countryCode);
    }

    public final String formatWithDescription() {
        return String.format("%s (+%s)", this.displayString, this.countryCode);
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.countryCode);
        dest.writeString(this.country);
        dest.writeString(this.displayString);
    }

    protected CountryCodeData(Parcel in) {
        this.countryCode = in.readString();
        this.country = in.readString();
        this.displayString = in.readString();
    }

    public static final Creator<CountryCodeData> CREATOR = new Creator<CountryCodeData>() {
        @Override public CountryCodeData createFromParcel(Parcel source) {return new CountryCodeData(source);}

        @Override public CountryCodeData[] newArray(int size) {return new CountryCodeData[size];}
    };

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CountryCodeData that = (CountryCodeData) o;

        if (countryCode != null ? !countryCode.equals(that.countryCode) : that.countryCode != null) return false;
        return country != null ? country.equals(that.country) : that.country == null;
    }

    @Override public int hashCode() {
        int result = countryCode != null ? countryCode.hashCode() : 0;
        result = 31 * result + (country != null ? country.hashCode() : 0);
        return result;
    }

    @Override public int compareTo(@NonNull CountryCodeData another) {
        return displayString.compareTo(another.displayString);
    }
}
