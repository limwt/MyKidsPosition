package com.wt.kids.mykidsposition

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.wt.kids.mykidsposition.di.Repository
import com.wt.kids.mykidsposition.model.MainViewModel
import com.wt.kids.mykidsposition.service.JeffService
import com.wt.kids.mykidsposition.ui.compose.MainView
import com.wt.kids.mykidsposition.ui.theme.MyKidsPositionTheme
import com.wt.kids.mykidsposition.utils.LocationUtils
import com.wt.kids.mykidsposition.utils.Logger
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val logTag = this::class.java.simpleName
    private val permissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                showMainView()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                showMainView()
            }
            permissions.getOrDefault(Manifest.permission.SEND_SMS, false) -> logger.logD(logTag, "get sms permission")
            else -> {
                // No location access granted.
            }
        }
    }

    @Inject lateinit var logger: Logger
    @Inject lateinit var locationUtils: LocationUtils
    @Inject lateinit var repository: Repository
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (verifyPermissions(this)) {
            showMainView()
        } else {
            requestPermissions()
        }
    }

    private fun showMainView() {
        val intent = Intent(this, JeffService::class.java)
        startForegroundService(intent)

        setContent {
            MyKidsPositionTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    MainView(viewModel = viewModel)
                }
            }
        }

        viewModel.updateCurrentPosition(address = "걸포 파머스 영어학원")
    }

    private fun requestPermissions() {
        permissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.SEND_SMS))
    }

    private fun verifyPermissions(context: Context): Boolean {
        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || context.checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
        return true
    }
}