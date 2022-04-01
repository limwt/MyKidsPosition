package com.wt.kids.mykidsposition.data

data class LocationData(
    val lastBuildDate: String = "",
    val start: Int = -1,
    val items: MutableList<LocationItemData> = mutableListOf()
)
