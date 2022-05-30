package com.wt.kids.mykidsposition.data.response

// for retrofit
data class ResponsePlaceData(
    val total: Int = -1,
    val items: List<ResponseItemsData> = emptyList()
)
