package com.wt.kids.mykidsposition.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import com.wt.kids.mykidsposition.data.tmap.SearchPoiInfo
import com.wt.kids.mykidsposition.data.tmap.SearchResponseData
import com.wt.kids.mykidsposition.di.Repository
import com.wt.kids.mykidsposition.utils.RxUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    private val logTag = "[Jeff]${this::class.java.simpleName}"
    private val gSon = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
    private var reqDisposable: Disposable? = null
    private val _searchPoiInfo = MutableLiveData<SearchPoiInfo>()
    val searPoiInfo: LiveData<SearchPoiInfo>
        get() = _searchPoiInfo

    fun searchPlace(keyword: String) {
        clearDisposable()
        reqDisposable = repository.getSearchLocation(keyword = keyword) // repository.getSearchKeyword(query = keyword)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { response ->
                if (response.isSuccessful) response.body()?.string() ?: ""
                else response.errorBody()?.string() ?: ""
            }
            .subscribe({ result ->
                Timber.tag(logTag).d("response")
                //val response = gSon.fromJson(result, ResultSearchKeyword::class.java)
                val response = gSon.fromJson(result, SearchResponseData::class.java)
                _searchPoiInfo.value = response.searchPoiInfo
                clearDisposable()
            }, { error ->
                Timber.tag(logTag).e("Error : ${error.message}")
                clearDisposable()
            })
        RxUtils.addDisposable(reqDisposable)
    }

    private fun clearDisposable() {
        RxUtils.clearDispose(reqDisposable)
        reqDisposable = null
    }
}