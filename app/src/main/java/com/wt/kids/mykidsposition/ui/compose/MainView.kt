package com.wt.kids.mykidsposition.ui.compose

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Place
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wt.kids.mykidsposition.model.MainViewModel
import com.wt.kids.mykidsposition.ui.theme.MyKidsPositionTheme

@Composable
fun MainView(
    viewModel: MainViewModel = hiltViewModel()
) {
    val address: String by viewModel.currentAddress.observeAsState("")
    CurrentPositionContent(address = address)
}

@Composable
fun CurrentPositionContent(address: String) {
    Scaffold(bottomBar = { BottomBar() }) {
        ContentView(address = address)
    }
}

@Composable
fun ContentView(address: String) {
    Column(modifier = Modifier.fillMaxSize()) {
        CurrentAddressText(address)
        FavoriteListView()
    }
}

@Composable
fun CurrentAddressText(address: String) {
    Text(
        text = "현위치 : $address",
        modifier = Modifier.padding(bottom = 8.dp),
        style = MaterialTheme.typography.h5,
    )
}

@Composable
fun FavoriteListView() {
    Text(text = "즐겨찾기 리스트")
}

@Composable
fun BottomBar() {
    val selectedIndex = remember { mutableStateOf(0) }
    BottomNavigation(elevation = 10.dp) {
        BottomNavigationItem(
            icon = {
                Icon(imageVector = Icons.Outlined.Home,"")
            },
            label = {
                Text(text = "우리집")
            },
            selected = (selectedIndex.value == 0),
            onClick = {
                selectedIndex.value = 0
                Log.d("[Jeff]MainView", "test")
            }
        )
        BottomNavigationItem(
            icon = {
                Icon(imageVector = Icons.Outlined.Place,"")
            },
            label = {
                Text(text = "즐겨찾기")
            },
            selected = (selectedIndex.value == 1),
            onClick = {
                selectedIndex.value = 1
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyKidsPositionTheme {
        MainView(viewModel = hiltViewModel())
    }
}