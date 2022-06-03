package com.wt.kids.mykidsposition.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.wt.kids.mykidsposition.data.repository.ReverseGeocoderRepository
import com.wt.kids.mykidsposition.data.repository.SearchPlaceRepository
import com.wt.kids.mykidsposition.data.response.ResponsePlaceData
import com.wt.kids.mykidsposition.utils.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: SearchPlaceRepository,
    private val reverseGeocoderRepository: ReverseGeocoderRepository,
    private val logger: Logger
) : ViewModel() {
    private val logTag = this::class.java.simpleName
    private val gSon = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()
    private val _searchData = MutableLiveData<ResponsePlaceData>()
    val searchData: LiveData<ResponsePlaceData>
        get() = _searchData

    fun searchPlace(keyword: String) {
        viewModelScope.launch {
            val call = repository.getResultPlace(keyword)
            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        logger.logD(logTag, "onResponse : ${response.body()}")
                        val result = gSon.fromJson(response.body(), ResponsePlaceData::class.java)
                        _searchData.postValue(result)
                    } else {
                        logger.logD(logTag, "onResponse - Error : ${response.errorBody().toString()}")
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    logger.logD(logTag, "onFailure : ${t.message}")
                }
            })
        }
    }

    fun searchAddress(coord: String) {
        logger.logD(logTag, "searchAddress : $coord")
        viewModelScope.launch {
            val call = reverseGeocoderRepository.getAddress(coord)
            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    logger.logD(logTag, "onResponse : ${response.body()}")
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    logger.logD(logTag, "onFailure : ${t.message}")
                }

            })
        }
    }
}