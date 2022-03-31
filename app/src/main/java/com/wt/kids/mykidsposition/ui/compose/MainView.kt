package com.wt.kids.mykidsposition.ui.compose

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Place
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
    MainContentView(address = address)
}

@Composable
fun MainContentView(address: String) {
    val context = LocalContext.current
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    Toast.makeText(context, "Floating Button!!!", Toast.LENGTH_SHORT).show()
                },
                backgroundColor = Color.Blue,
                content = {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_input_add),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) {
        Box() {
            PlaceEditText()
            Column() {
                CurrentAddressText(address)
                FavoriteListView()
            }
        }
    }
}

@Composable
fun PlaceEditText() {
    var text by rememberSaveable { mutableStateOf("") }
    TextField(
        value = text,
        onValueChange = {
            text = it
        },
        label = { Text(text = "등록할 위치 검색") },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(10.dp)
    )
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
        MainContentView(address = "테스트 위치")
    }
}