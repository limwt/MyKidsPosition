package com.wt.kids.mykidsposition.di

import retrofit2.Call
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
}