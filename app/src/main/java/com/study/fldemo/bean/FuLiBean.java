package com.study.fldemo.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/8/8.
 */

public class FuLiBean implements Parcelable {
//    "_id": "59826564421aa90ca3bb6bda",
//    "createdAt": "2017-08-03T07:51:00.249Z",
//    "desc": "8-3",
//    "publishedAt": "2017-08-03T12:08:07.258Z",
//    "source": "chrome",
//    "type": "\u798f\u5229",
//    "url": "https://ws1.sinaimg.cn/large/610dc034gy1fi678xgq1ij20pa0vlgo4.jpg",
//    "used": true,
//    "who": "\u4ee3\u7801\u5bb6"
    public String _id;
    public String createdAt;
    public String desc;
    public String publishedAt;
    public String chrome;
    public String type;
    public String url;
    public Boolean used;
    public String who;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._id);
        dest.writeString(this.createdAt);
        dest.writeString(this.desc);
        dest.writeString(this.publishedAt);
        dest.writeString(this.chrome);
        dest.writeString(this.type);
        dest.writeString(this.url);
        dest.writeValue(this.used);
        dest.writeString(this.who);
    }

    public FuLiBean() {
    }

    protected FuLiBean(Parcel in) {
        this._id = in.readString();
        this.createdAt = in.readString();
        this.desc = in.readString();
        this.publishedAt = in.readString();
        this.chrome = in.readString();
        this.type = in.readString();
        this.url = in.readString();
        this.used = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.who = in.readString();
    }

    public static final Parcelable.Creator<FuLiBean> CREATOR = new Parcelable.Creator<FuLiBean>() {
        @Override
        public FuLiBean createFromParcel(Parcel source) {
            return new FuLiBean(source);
        }

        @Override
        public FuLiBean[] newArray(int size) {
            return new FuLiBean[size];
        }
    };
}
