package com.wt.kids.mykidsposition.data.source

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiService {
    @GET("search/local.json")
    fun getSearchResult(
        @Header("X-Naver-Client-Id") id: String = "cDiBU6P02t8um9zOW5ug",
        @Header("X-Naver-Client-Secret") pw: String = "1m4XlKEDIL",
        @Query("query") query: String,
        @Query("display") display: Int = 5,
        @Query("start") start: Int = 1,
        @Query("sort") sort: String = "random"
    ): Call<String>
}