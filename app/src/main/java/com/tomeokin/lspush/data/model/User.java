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

public class User {
    private String uid; // ([a-zA-Z0-9]){3,24}, restraints by UserIdFilter, LengthFilter and valid checker
    private String nickname;
    private String email;
    private String phone;
    private String region; // 地区码, use for phone
    private String password;
    private int validate; // 00：未验证，01：手机号已验证，02：email已验证，03：手机号和 email都已验证
    private String image;

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
            '}';
    }
}
