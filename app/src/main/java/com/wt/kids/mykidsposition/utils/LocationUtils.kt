package com.wt.kids.mykidsposition.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import androidx.core.app.ActivityCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationUtils @Inject constructor(
    @ApplicationContext private val context: Context,
    private val logger: Logger
) {
    private val logTag = this::class.java.simpleName
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val locationListener = LocationListener {
        val longitude = it.longitude
        val latitude = it.latitude
        logger.logD(logTag, "update location : $longitude, $latitude")
    }

    fun getLocationData(): Location? {
        logger.logD(logTag, "getLocationData")
        var location: Location? = null
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }

        return location
    }

    fun registerLocationSrv() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000L, 1.0f, locationListener)
        }
    }

    fun getCurrentAddress(): String {
        var address ="현재 위치를 확인 할 수 없습니다."
        val geocoder = Geocoder(context, Locale.KOREA)
        val addressList = mutableListOf<Address>()

        try {
            //세번째 파라미터는 좌표에 대해 주소를 리턴 받는 갯수로
            //한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 최대갯수 설정
            getLocationData()?.let { data ->
                addressList.addAll(geocoder.getFromLocation(data.latitude, data.longitude, 1).toMutableList())
            }

            if (addressList.isNotEmpty()) {
                address = addressList[0].getAddressLine(0).toString()
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return address
    }
}