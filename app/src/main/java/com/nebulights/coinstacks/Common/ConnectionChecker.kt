package com.nebulights.coinstacks.Common

import android.content.Context
import android.net.ConnectivityManager
import com.nebulights.coinstacks.R

class ConnectionChecker(val context: Context) {

     var noInternetMessage: String = context.getString(R.string.no_internet_connection)

     fun isInternetAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnected ?: false
    }
}