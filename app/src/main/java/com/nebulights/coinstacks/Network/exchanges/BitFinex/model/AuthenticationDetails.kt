package com.nebulights.coinstacks.Network.exchanges.BitFinex.model

data class AuthenticationDetails(val key: String,
                                 val payload64: String,
                                 val signature: String)