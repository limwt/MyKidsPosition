package com.wt.kids.mykidsposition.di

import com.wt.kids.mykidsposition.constants.ApiConstants
import javax.inject.Inject

class Repository @Inject constructor(private val apiService: ApiService) {
    fun getSearch(query: String) = apiService.getSearch(
        id = ApiConstants.CLIENT_ID.value,
        secret = ApiConstants.CLIENT_SECRET.value,
        type = ApiConstants.SEARCH_TYPE_LOCAL.value,
        query = query,
        display = 10,
        start = 1
    )

    fun getSearchLocation(keyword: String) = apiService.getSearchLocation(keyword = keyword)

    fun getSearchKeyword(query: String) = apiService.getSearchKeyword(query = query)
}