package com.wt.kids.mykidsposition.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchUtils @Inject constructor(
    @ApplicationContext private val context: Context,
    private val logger: Logger
) {
    fun searchPlace(keyword: String) {
        val searchKeyword = URLEncoder.encode(keyword, "UTF-8")
        val apiUrl = "https://openapi.naver.com/v1/search/local.json"
    }
}