package com.nebulights.coinstacks.Network.exchanges.Gdax.model

/**
 * Created by babramovitch on 2018-02-22.
 */
data class AuthenticationDetails(val key: String,
                                 val signature: String,
                                 val timestamp: String,
                                 val passphrase: String)