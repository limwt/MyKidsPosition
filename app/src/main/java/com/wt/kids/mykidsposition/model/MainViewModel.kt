package com.wt.kids.mykidsposition.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val _currentAddress = MutableLiveData<String>()
    val currentAddress: LiveData<String>
        get() = _currentAddress

    fun updateCurrentPosition(address: String) {
        //logger.logD(logTag, "updateCurrentAddress")
        //Log.d("[Jeff]MainViewModel", "updateCurrentAddress : $address")
        _currentAddress.value = address
    }
}