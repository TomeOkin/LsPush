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

import com.tomeokin.lspush.data.support.GsonIgnore;

import java.util.Date;
import java.util.List;

public class Collection implements Parcelable {
    private long id;
    private User user;
    private Link link;
    private String description;
    private String image;
    private Date createDate;
    private Date updateDate;

    private List<String> tags;
    private List<User> explorers;
    private long favorCount;
    private boolean hasFavor;

    @GsonIgnore private boolean hasRead;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<User> getExplorers() {
        return explorers;
    }

    public void setExplorers(List<User> explorers) {
        this.explorers = explorers;
    }

    public long getFavorCount() {
        return favorCount;
    }

    public void setFavorCount(long favorCount) {
        this.favorCount = favorCount;
    }

    public boolean isHasFavor() {
        return hasFavor;
    }

    public void setHasFavor(boolean hasFavor) {
        this.hasFavor = hasFavor;
    }

    public boolean isHasRead() {
        return hasRead;
    }

    public void setHasRead(boolean hasRead) {
        this.hasRead = hasRead;
    }

    @Override
    public String toString() {
        return "Collection{" +
            "id=" + id +
            ", user=" + user +
            ", link=" + link +
            ", description='" + description + '\'' +
            ", image='" + image + '\'' +
            ", createDate=" + createDate +
            ", updateDate=" + updateDate +
            ", tags=" + tags +
            ", explorers=" + explorers +
            ", favorCount=" + favorCount +
            ", hasFavor=" + hasFavor +
            ", hasRead=" + hasRead +
            '}';
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Collection that = (Collection) o;

        if (id != that.id) return false;
        if (favorCount != that.favorCount) return false;
        if (hasFavor != that.hasFavor) return false;
        if (hasRead != that.hasRead) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        if (link != null ? !link.equals(that.link) : that.link != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (image != null ? !image.equals(that.image) : that.image != null) return false;
        if (createDate != null ? !createDate.equals(that.createDate) : that.createDate != null) return false;
        if (updateDate != null ? !updateDate.equals(that.updateDate) : that.updateDate != null) return false;
        if (tags != null ? !tags.equals(that.tags) : that.tags != null) return false;
        return explorers != null ? explorers.equals(that.explorers) : that.explorers == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (link != null ? link.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (updateDate != null ? updateDate.hashCode() : 0);
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        result = 31 * result + (explorers != null ? explorers.hashCode() : 0);
        result = 31 * result + (int) (favorCount ^ (favorCount >>> 32));
        result = 31 * result + (hasFavor ? 1 : 0);
        result = 31 * result + (hasRead ? 1 : 0);
        return result;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeParcelable(this.user, flags);
        dest.writeParcelable(this.link, flags);
        dest.writeString(this.description);
        dest.writeString(this.image);
        dest.writeLong(this.createDate != null ? this.createDate.getTime() : -1);
        dest.writeLong(this.updateDate != null ? this.updateDate.getTime() : -1);
        dest.writeStringList(this.tags);
        dest.writeTypedList(this.explorers);
        dest.writeLong(this.favorCount);
        dest.writeByte(this.hasFavor ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hasRead ? (byte) 1 : (byte) 0);
    }

    public Collection() {}

    protected Collection(Parcel in) {
        this.id = in.readLong();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.link = in.readParcelable(Link.class.getClassLoader());
        this.description = in.readString();
        this.image = in.readString();
        long tmpCreateDate = in.readLong();
        this.createDate = tmpCreateDate == -1 ? null : new Date(tmpCreateDate);
        long tmpUpdateDate = in.readLong();
        this.updateDate = tmpUpdateDate == -1 ? null : new Date(tmpUpdateDate);
        this.tags = in.createStringArrayList();
        this.explorers = in.createTypedArrayList(User.CREATOR);
        this.favorCount = in.readLong();
        this.hasFavor = in.readByte() != 0;
        this.hasRead = in.readByte() != 0;
    }

    public static final Creator<Collection> CREATOR = new Creator<Collection>() {
        @Override
        public Collection createFromParcel(Parcel source) {return new Collection(source);}

        @Override
        public Collection[] newArray(int size) {return new Collection[size];}
    };
}
