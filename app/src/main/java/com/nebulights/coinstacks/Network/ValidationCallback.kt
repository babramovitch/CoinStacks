package com.nebulights.coinstacks.Network

/**
 * Created by babramovitch on 4/13/2018.
 *
 */
interface ValidationCallback {
    fun validationSuccess()
    fun validationError(errorBody: String?)
}