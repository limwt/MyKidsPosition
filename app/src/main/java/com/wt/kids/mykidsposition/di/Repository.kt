package com.wt.kids.mykidsposition.di

import com.wt.kids.mykidsposition.constants.ApiConstants
import javax.inject.Inject

class Repository @Inject constructor(private val apiService: ApiService) {
    fun getSearch(query: String) = apiService.getSearch(
        id = ApiConstants.CLIENT_ID.value,
        secret = ApiConstants.CLIENT_SECRET.value,
        type = ApiConstants.SEARCH_TYPE.value,
        query = query
    )
}