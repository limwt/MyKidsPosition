package com.wt.kids.mykidsposition.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import com.wt.kids.mykidsposition.di.Repository
import com.wt.kids.mykidsposition.service.LocationData
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    private val _currentAddress = MutableLiveData<String>()
    val currentAddress: LiveData<String>
        get() = _currentAddress

    fun updateCurrentPosition(address: String) {
        //logger.logD(logTag, "updateCurrentAddress")
        val call = repository.getSearch(address)
        Log.d("[Jeff]ViewModel", "updateCurrentPosition")
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val locationData = GsonBuilder().setPrettyPrinting().create().fromJson(response.body(), LocationData::class.java)
                    _currentAddress.value = locationData.items[0].title
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
            }
        })
    }
}