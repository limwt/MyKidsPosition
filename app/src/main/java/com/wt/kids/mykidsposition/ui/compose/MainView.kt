package com.wt.kids.mykidsposition.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.wt.kids.mykidsposition.model.MainViewModel
import com.wt.kids.mykidsposition.ui.theme.MyKidsPositionTheme

@Composable
fun MainView(
    viewModel: MainViewModel = hiltViewModel()
) {
    Column(modifier = Modifier
        .background(Color.White)
        .fillMaxSize()) {
        PlaceSearchView(viewModel = viewModel)
        PlaceSearchResultListView(viewModel = viewModel)
    }
}

/*@Composable
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
}*/

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyKidsPositionTheme {
        MainView()
    }
}