package com.study.fldemo.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Administrator on 2017/8/8.
 */

class FuLiBean : Parcelable {
    //    "_id": "59826564421aa90ca3bb6bda",
    //    "createdAt": "2017-08-03T07:51:00.249Z",
    //    "desc": "8-3",
    //    "publishedAt": "2017-08-03T12:08:07.258Z",
    //    "source": "chrome",
    //    "type": "\u798f\u5229",
    //    "url": "https://ws1.sinaimg.cn/large/610dc034gy1fi678xgq1ij20pa0vlgo4.jpg",
    //    "used": true,
    //    "who": "\u4ee3\u7801\u5bb6"
    var _id: String? = null
    var createdAt: String? = null
    var desc: String? = null
    var publishedAt: String? = null
    var chrome: String? = null
    var type: String? = null
    var url: String? = null
    var used: Boolean? = null
    var who: String? = null

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this._id)
        dest.writeString(this.createdAt)
        dest.writeString(this.desc)
        dest.writeString(this.publishedAt)
        dest.writeString(this.chrome)
        dest.writeString(this.type)
        dest.writeString(this.url)
        dest.writeValue(this.used)
        dest.writeString(this.who)
    }

    constructor() {}

    protected constructor(`in`: Parcel) {
        this._id = `in`.readString()
        this.createdAt = `in`.readString()
        this.desc = `in`.readString()
        this.publishedAt = `in`.readString()
        this.chrome = `in`.readString()
        this.type = `in`.readString()
        this.url = `in`.readString()
        this.used = `in`.readValue(Boolean::class.java.classLoader) as Boolean?
        this.who = `in`.readString()
    }

    companion object CREATOR : Parcelable.Creator<FuLiBean> {
        override fun createFromParcel(parcel: Parcel): FuLiBean {
            return FuLiBean(parcel)
        }

        override fun newArray(size: Int): Array<FuLiBean?> {
            return arrayOfNulls(size)
        }
    }
}
