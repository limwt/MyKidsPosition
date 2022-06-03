package com.wt.kids.mykidsposition.data.source

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ReverseGeocoderService {
    @GET("gc")
    fun getAddressResult(
        @Header("X-NCP-APIGW-API-KEY-ID") id: String = "ktq5tw7vkh",
        @Header("X-NCP-APIGW-API-KEY") pw: String = "HqkqMfWwSNSVrXHjOjen9QDfH0oq92ug7Jn3n6CB",
        @Query("coord") coord: String,
        // 좌표 체계: UTM-K
        @Query("sourcecrs") sourcecrs: String = "epsg:4326",
        // 변환 작업: 좌표 -> 법정동 & 도로명 주소
        @Query("orders") orders: String = "legalcode,roadaddr",
        @Query("output") output: String = "json"
    ): Call<String>
}