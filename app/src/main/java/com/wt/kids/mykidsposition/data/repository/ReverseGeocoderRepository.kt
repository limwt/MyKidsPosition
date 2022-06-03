package com.wt.kids.mykidsposition.data.repository

import com.wt.kids.mykidsposition.data.source.ReverseGeocoderService
import retrofit2.Call
import javax.inject.Inject

class ReverseGeocoderRepository @Inject constructor(
    private val service: ReverseGeocoderService
) {
    suspend fun getAddress(coord: String): Call<String> {
        return service.getAddressResult(coord = coord)
    }
}