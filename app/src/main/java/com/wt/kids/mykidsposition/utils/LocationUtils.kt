package com.wt.kids.mykidsposition.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import com.google.gson.GsonBuilder
import com.wt.kids.mykidsposition.service.LocationData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
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

    private fun getLocationData(): Location? {
        logger.logD(logTag, "getLocationData")
        var location: Location? = null
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }

        return location
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

        CoroutineScope(Dispatchers.IO).launch {
            searchPlace("걸포 파머스 영어학원")
        }

        return address
    }

    //naver place search api.
    fun searchPlace(place: String) {
        val clientId = "cDiBU6P02t8um9zOW5ug"
        val clientSecret = "1m4XlKEDIL"
        try {
            val text = URLEncoder.encode(place, "UTF-8")
            val apiURL = "https://openapi.naver.com/v1/search/local.json?query=$text&display=20&start=1&sort=random"
            val url = URL(apiURL)
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "GET"
            con.setRequestProperty("X-Naver-Client-Id", clientId)
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret)
            val responseCode = con.responseCode
            val br = if(responseCode==200) {
                BufferedReader(InputStreamReader(con.inputStream))
            } else {
                BufferedReader(InputStreamReader(con.errorStream))
            }

            val response = StringBuffer()
            while (br.readLine() != null) {
                response.append(br.readLine())
            }

            br.close()
            val result = GsonBuilder().setPrettyPrinting().create().fromJson(response.trim().toString(), LocationData::class.java)
            logger.logD(logTag, "response : $result")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}