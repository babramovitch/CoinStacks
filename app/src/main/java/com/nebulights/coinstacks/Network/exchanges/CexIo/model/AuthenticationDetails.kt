package com.nebulights.coinstacks.Network.exchanges.CexIo.model

/**
 * Created by babramovitch on 2018-02-25.
 */
data class AuthenticationDetails(val key: String,
                                 val signature: String,
                                 val nonce: String)