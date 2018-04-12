package com.nebulights.coinstacks.Portfolio.Additions

import io.reactivex.Observable

/**
 * Created by babramovitch on 4/10/2018.
 *
 */
data class AdditionsFormValidator(private val quantityObservable: Observable<CharSequence>,
                                  private val watchAddressObservable: Observable<CharSequence>,
                                  private val userNameObservable: Observable<CharSequence>,
                                  private val apiKeyObservable: Observable<CharSequence>,
                                  private val apiSecretObservable: Observable<CharSequence>,
                                  private val apiPasswordObservable: Observable<CharSequence>) {


    fun coinsValidator(): Observable<Boolean> {
        return createFormValidator(arrayListOf(quantityObservable))
    }

    fun watchAddressValidator(): Observable<Boolean> {
        return createFormValidator(arrayListOf(watchAddressObservable))
    }

    fun apiValidator(hasUserName: Boolean, hasPassword: Boolean): Observable<Boolean> {
        val observableList = arrayListOf<Observable<CharSequence>>()
        observableList.add(apiKeyObservable)
        observableList.add(apiSecretObservable)
        if (hasUserName) {
            observableList.add(userNameObservable)
        }
        if (hasPassword) {
            observableList.add(apiPasswordObservable)
        }

        return createFormValidator(observableList)
    }

    private fun createFormValidator(observableList: ArrayList<Observable<CharSequence>>): Observable<Boolean> {
        return Observable.combineLatest(observableList, {
            it.forEach {
                if (it.toString().isEmpty()) {
                    return@combineLatest false
                }
            }
            return@combineLatest true
        })
    }
}