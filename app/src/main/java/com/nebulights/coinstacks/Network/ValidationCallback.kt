package com.nebulights.coinstacks.Network

import com.nebulights.coinstacks.Network.exchanges.Models.BasicAuthentication

/**
 * Created by babramovitch on 4/13/2018.
 *
 */
interface ValidationCallback {
    fun validationSuccess()
    fun validationError(source: String, errorBody: String?)
}