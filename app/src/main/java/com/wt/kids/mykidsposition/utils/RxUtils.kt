package com.wt.kids.mykidsposition.utils

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

object RxUtils {
    private val compositeDisposable = CompositeDisposable()
    // singleton이나 static으로 구현한 클래스에서 compositeDisposable 처리가 곤란할때만 사용할 것
    // clear 시점은 app terminate 시점
    @JvmStatic
    fun addDisposable(disposable: Disposable?) {
        if (disposable != null) {
            try {
                compositeDisposable.add(disposable)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @JvmStatic
    fun clearDisposable() {
        try {
            compositeDisposable.clear()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun clearDispose(disposable: Disposable?) {
        disposable?.let {
            compositeDisposable.remove(it)
        }
    }
}