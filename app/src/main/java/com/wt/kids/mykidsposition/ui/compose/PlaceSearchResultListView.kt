package com.wt.kids.mykidsposition.ui.compose

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.wt.kids.mykidsposition.data.tmap.SearchPoiInfo
import com.wt.kids.mykidsposition.model.MainViewModel
import com.wt.kids.mykidsposition.ui.theme.MyKidsPositionTheme
import timber.log.Timber

private val logTag = "[Jeff]PlaceSearchResultListView"

@Composable
fun PlaceSearchResultListView(viewModel: MainViewModel) {
    val result: SearchPoiInfo by viewModel.searPoiInfo.observeAsState(initial = SearchPoiInfo())
    //Timber.tag(logTag).d("result : $result")
    LazyColumn {
        items(result.pois.poi) { poi ->
            Timber.tag(logTag).d("result : ${poi.upperAddrName}, ${poi.middleAddrName}, ${poi.roadName}")
            /*poi.upperAddrName?.let { address ->
                Timber.tag(logTag).d("result : $address")
                Text(text = address)
            }*/
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlaceSearchResultPreview() {
    MyKidsPositionTheme {
        PlaceSearchResultListView(hiltViewModel())
    }
}
