package com.wt.kids.mykidsposition.di

import com.wt.kids.mykidsposition.constants.ApiConstants
import com.wt.kids.mykidsposition.constants.KakaoOpenApiConstants
import com.wt.kids.mykidsposition.constants.TMapOpenApiConstants
import com.wt.kids.mykidsposition.data.tmap.AddressInfoResponseData
import io.reactivex.rxjava3.core.Single
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {
    @GET("search/{type}")
    fun getSearch(
        @Header("X-Naver-Client-Id") id: String,
        @Header("X-Naver-Client-Secret") secret: String,
        @Path("type") type: String,
        @Query("query") query: String,
        @Query("display") display: Int,
        @Query("start") start: Int
    ): Call<String>

    @GET(TMapOpenApiConstants.GET_TMAP_LOCATION)
    fun getSearchLocation(
        @Header("appKey") appKey: String = ApiConstants.TMAP_API_KEY.value,
        @Query("version") version: Int = VERSION,
        @Query("callback") callback: String? = null,
        @Query("page") page: Int = START_PAGE,
        @Query("count") count: Int = MAX_PAGE_CONTENT_SIZE, // 한페이지에 얼마나 나타낼 지
        @Query("searchKeyword") keyword: String, // 검색어
        @Query("areaLLCode") areaLLCode: String? = null,
        @Query("areaLMCode") areaLMCode: String? = null,
        @Query("resCoordType") resCoordType: String? = null,
        @Query("searchType") searchType: String? = null,
        @Query("multiPoint") multiPoint: String? = null,
        @Query("searchtypCd") searchtypCd: String? = null,
        @Query("radius") radius: String? = null,
        @Query("reqCoordType") reqCoordType: String? = null,
        @Query("centerLon") centerLon: String? = null,
        @Query("centerLat") centerLat: String? = null
    ): Single<Response<ResponseBody>>

    @GET(TMapOpenApiConstants.GET_TMAP_REVERSE_GEO_CODE)
    fun getReverseGeoCode(
        @Header("appKey") appKey: String = ApiConstants.TMAP_API_KEY.value,
        @Query("version") version: Int = VERSION,
        @Query("callback") callback: String? = null,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("coordType") coordType: String? = null,
        @Query("addressType") addressType: String? = null
    ): Response<AddressInfoResponseData>

    //KAKAO
    @GET(KakaoOpenApiConstants.GET_SEARCH_KEYWORD)
    fun getSearchKeyword(
        @Header("Authorization") key: String = ApiConstants.KAKAO_API_KEY.value,
        @Query("query") query: String,
        @Query("page") page: Int = 10
    ): Single<Response<ResponseBody>>

    //TMap Open Api
    companion object {
        const val MAX_PAGE_CONTENT_SIZE = 30
        const val VERSION = 1
        const val START_PAGE = 1
    }
}