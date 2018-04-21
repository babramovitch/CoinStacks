package com.nebulights.coinstacks.Types

/**
 * Created by babramovitch on 4/19/2018.
 *
 */
sealed class NetworkErrors {
    object NULL: NetworkErrors()
    object UNKNOWN: NetworkErrors()
    object RATE_LIMIT: NetworkErrors()
}