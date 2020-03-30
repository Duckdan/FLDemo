package com.study.fldemo.manager;

import com.study.fldemo.bean.AndroidResultBean;
import com.study.fldemo.bean.FuLiResultBean;

import io.reactivex.Observable;
import kotlinx.coroutines.Deferred;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * 网络请求管理者
 */
public interface HttpUrlManager {
    //    http://gank.io/api/data/福利/10/1
    @GET("data/福利/{size}/{page}")
    Observable<FuLiResultBean> getFuLiListData(@Path("size") int size, @Path("page") int page);

    @GET
    Observable<ResponseBody> getImageFile(@Url String url);

    @GET("data/Android/{size}/{page}")
    Observable<AndroidResultBean> getAndroidData(@Path("size") int size, @Path("page") int page);

    @GET("data/iOS/{size}/{page}")
    Observable<AndroidResultBean> getIosData(@Path("size") int size, @Path("page") int page);

    @GET("data/前端/{size}/{page}")
    Observable<AndroidResultBean> getWebData(@Path("size") int size, @Path("page") int page);

    @GET("data/拓展资源/{size}/{page}")
    Observable<AndroidResultBean> getExpandData(@Path("size") int size, @Path("page") int page);

    @GET("data/休息视频/{size}/{page}")
    Observable<AndroidResultBean> getVideoData(@Path("size") int size, @Path("page") int page);

    //============================================
    @GET("data/福利/{size}/{page}")
    Deferred<FuLiResultBean> getFuLiListDataKotlin(@Path("size") int size, @Path("page") int page);

    @GET("data/Android/{size}/{page}")
    Deferred<AndroidResultBean> getAndroidDataKotlin(@Path("size") int size, @Path("page") int page);

    @GET("data/拓展资源/{size}/{page}")
    Deferred<AndroidResultBean> getExpandDataKotlin(@Path("size") int size, @Path("page") int page);
}
