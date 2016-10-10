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

public class User implements Parcelable {
    private String uid; // ([a-zA-Z0-9]){3,24}, restraints by UserIdFilter, LengthFilter and valid checker
    private String nickname;
    private String email;
    private String phone;
    private String region; // 地区码, use for phone
    private String password;
    private int validate; // 00：未验证，01：手机号已验证，02：email 已验证，03：手机号和 email 都已验证
    private String image;

    private long colId;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getValidate() {
        return validate;
    }

    public void setValidate(int validate) {
        this.validate = validate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getColId() {
        return colId;
    }

    public void setColId(long colId) {
        this.colId = colId;
    }

    @Override
    public String toString() {
        return "User{" +
            "uid='" + uid + '\'' +
            ", nickname='" + nickname + '\'' +
            ", email='" + email + '\'' +
            ", phone='" + phone + '\'' +
            ", region='" + region + '\'' +
            ", password='" + password + '\'' +
            ", validate=" + validate +
            ", image='" + image + '\'' +
            ", colId=" + colId +
            '}';
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (validate != user.validate) return false;
        if (colId != user.colId) return false;
        if (uid != null ? !uid.equals(user.uid) : user.uid != null) return false;
        if (nickname != null ? !nickname.equals(user.nickname) : user.nickname != null) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        if (phone != null ? !phone.equals(user.phone) : user.phone != null) return false;
        if (region != null ? !region.equals(user.region) : user.region != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;
        return image != null ? image.equals(user.image) : user.image == null;
    }

    @Override
    public int hashCode() {
        int result = uid != null ? uid.hashCode() : 0;
        result = 31 * result + (nickname != null ? nickname.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (region != null ? region.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + validate;
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + (int) (colId ^ (colId >>> 32));
        return result;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.nickname);
        dest.writeString(this.email);
        dest.writeString(this.phone);
        dest.writeString(this.region);
        dest.writeString(this.password);
        dest.writeInt(this.validate);
        dest.writeString(this.image);
        dest.writeLong(this.colId);
    }

    public User() {}

    protected User(Parcel in) {
        this.uid = in.readString();
        this.nickname = in.readString();
        this.email = in.readString();
        this.phone = in.readString();
        this.region = in.readString();
        this.password = in.readString();
        this.validate = in.readInt();
        this.image = in.readString();
        this.colId = in.readLong();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {return new User(source);}

        @Override
        public User[] newArray(int size) {return new User[size];}
    };
}
