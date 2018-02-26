package com.nebulights.coinstacks.Network.exchanges.Quadriga.model

data class AuthenticationDetails(val key: String,
                                 val nonce: Long,
                                 val signature: String)

