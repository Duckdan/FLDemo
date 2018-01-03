package com.study.fldemo.dao;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;

/**
 * Created by Administrator on 2017/12/27.
 */
@Entity(nameInDb = "collect")
public class DatabaseBean implements Parcelable {
    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "identify")
    private String _id;
    private String createdAt;
    private String desc;
    private String publishedAt;
    private String source;
    private String type;
    private String url;
    private String who;
    private String imageUrl;
    private Integer saveTime;
    //用于保存置顶之前的保存时间
    private Integer stickTime;
    @Transient
    private boolean isCheck;

    @Generated(hash = 772484586)
    public DatabaseBean(Long id, String _id, String createdAt, String desc, String publishedAt, String source,
            String type, String url, String who, String imageUrl, Integer saveTime, Integer stickTime) {
        this.id = id;
        this._id = _id;
        this.createdAt = createdAt;
        this.desc = desc;
        this.publishedAt = publishedAt;
        this.source = source;
        this.type = type;
        this.url = url;
        this.who = who;
        this.imageUrl = imageUrl;
        this.saveTime = saveTime;
        this.stickTime = stickTime;
    }

    @Generated(hash = 289390549)
    public DatabaseBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String get_id() {
        return this._id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPublishedAt() {
        return this.publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWho() {
        return this.who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getSaveTime() {
        return this.saveTime;
    }

    public void setSaveTime(Integer saveTime) {
        this.saveTime = saveTime;
    }

    public Integer getStickTime() {
        return this.stickTime;
    }

    public void setStickTime(Integer stickTime) {
        this.stickTime = stickTime;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this._id);
        dest.writeString(this.createdAt);
        dest.writeString(this.desc);
        dest.writeString(this.publishedAt);
        dest.writeString(this.source);
        dest.writeString(this.type);
        dest.writeString(this.url);
        dest.writeString(this.who);
        dest.writeString(this.imageUrl);
        dest.writeValue(this.saveTime);
        dest.writeValue(this.stickTime);
        dest.writeByte(this.isCheck ? (byte) 1 : (byte) 0);
    }

    public boolean getIsCheck() {
        return this.isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    protected DatabaseBean(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this._id = in.readString();
        this.createdAt = in.readString();
        this.desc = in.readString();
        this.publishedAt = in.readString();
        this.source = in.readString();
        this.type = in.readString();
        this.url = in.readString();
        this.who = in.readString();
        this.imageUrl = in.readString();
        this.saveTime = (Integer) in.readValue(Integer.class.getClassLoader());
        this.stickTime = (Integer) in.readValue(Integer.class.getClassLoader());
        this.isCheck = in.readByte() != 0;
    }

    public static final Parcelable.Creator<DatabaseBean> CREATOR = new Parcelable.Creator<DatabaseBean>() {
        @Override
        public DatabaseBean createFromParcel(Parcel source) {
            return new DatabaseBean(source);
        }

        @Override
        public DatabaseBean[] newArray(int size) {
            return new DatabaseBean[size];
        }
    };
}
