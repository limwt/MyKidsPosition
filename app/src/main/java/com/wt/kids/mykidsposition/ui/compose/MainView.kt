package com.wt.kids.mykidsposition.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wt.kids.mykidsposition.model.MainViewModel
import com.wt.kids.mykidsposition.ui.theme.MyKidsPositionTheme
import com.wt.kids.mykidsposition.utils.Logger

@Composable
fun MainView(
    viewModel: MainViewModel,
    logger: Logger
) {
    val address: String by viewModel.currentAddress.observeAsState("")
    logger.logD(msg = "MainView : $address")
    CurrentPositionContent(address = address)
}

@Composable
fun CurrentPositionContent(address: String) {
    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        Text(
            text = "현위치 : $address",
            modifier = Modifier.padding(bottom = 8.dp),
            style = MaterialTheme.typography.h5,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyKidsPositionTheme {
        MainView(viewModel = MainViewModel(), logger = Logger())
    }
}