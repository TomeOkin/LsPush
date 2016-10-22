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

public class WebPageInfo implements Parcelable {
    private String url;
    private String title;
    private String description;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "WebPageInfo{" +
            "url='" + url + '\'' +
            ", title='" + title + '\'' +
            ", description='" + description + '\'' +
            '}';
    }

    public Collection toCollection() {
        Link link = new Link();
        link.setUrl(url);
        link.setTitle(title);

        Collection collection = new Collection();
        collection.setDescription(description);
        collection.setLink(link);
        return collection;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.title);
        dest.writeString(this.description);
    }

    public WebPageInfo() {}

    protected WebPageInfo(Parcel in) {
        this.url = in.readString();
        this.title = in.readString();
        this.description = in.readString();
    }

    public static final Parcelable.Creator<WebPageInfo> CREATOR = new Parcelable.Creator<WebPageInfo>() {
        @Override
        public WebPageInfo createFromParcel(Parcel source) {return new WebPageInfo(source);}

        @Override
        public WebPageInfo[] newArray(int size) {return new WebPageInfo[size];}
    };
}
