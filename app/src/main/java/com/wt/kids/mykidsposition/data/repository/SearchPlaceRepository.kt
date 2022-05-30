package com.wt.kids.mykidsposition.data.repository

import com.wt.kids.mykidsposition.data.source.ApiService
import retrofit2.Call
import javax.inject.Inject

class SearchPlaceRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getResultPlace(keyword: String): Call<String> {
        return apiService.getSearchResult(query = keyword)
    }
}