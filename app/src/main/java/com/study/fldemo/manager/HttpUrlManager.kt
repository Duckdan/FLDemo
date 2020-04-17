package com.study.fldemo.manager

import com.study.fldemo.bean.AndroidResultBean
import com.study.fldemo.bean.FuLiResultBean

import io.reactivex.Observable
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

/**
 * 网络请求管理者
 */
interface HttpUrlManager {

    @GET
    fun getImageFile(@Url url: String): Observable<ResponseBody>

    @GET("data/福利/{size}/{page}")
    fun getFuLiListDataKotlin(@Path("size") size: Int, @Path("page") page: Int): Deferred<FuLiResultBean>

    @GET("data/Android/{size}/{page}")
    fun getAndroidDataKotlin(@Path("size") size: Int, @Path("page") page: Int): Deferred<AndroidResultBean>

    @GET("data/iOS/{size}/{page}")
    fun getIosDataKotlin(@Path("size") size: Int, @Path("page") page: Int): Deferred<AndroidResultBean>

    @GET("data/前端/{size}/{page}")
    fun getWebDataKotlin(@Path("size") size: Int, @Path("page") page: Int): Deferred<AndroidResultBean>

    @GET("data/拓展资源/{size}/{page}")
    fun getExpandDataKotlin(@Path("size") size: Int, @Path("page") page: Int): Deferred<AndroidResultBean>

    @GET("data/休息视频/{size}/{page}")
    fun getVideoDataKotlin(@Path("size") size: Int, @Path("page") page: Int): Deferred<AndroidResultBean>
}
