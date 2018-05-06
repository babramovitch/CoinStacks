package com.nebulights.coinstacks.Network.BlockExplorers

import com.nebulights.coinstacks.Network.BlockExplorers.Model.WatchAddress
import com.nebulights.coinstacks.Network.BlockExplorers.Model.WatchAddressBalance
import com.nebulights.coinstacks.Network.ValidationCallback
import com.nebulights.coinstacks.Network.exchanges.NetworkCompletionCallback

import com.nebulights.coinstacks.Types.CryptoTypes

/**
 * Created by babramovitch on 11/9/2017.
 */

interface ExplorerNetworkDataUpdate {
    fun updateWatchAddressData(address: String, data: WatchAddressBalance)
}

interface Explorer {
    fun stopFeed()
    fun startAddressFeed(address: ArrayList<WatchAddress>, presenterCallback: NetworkCompletionCallback, explorerNetworkDataUpdate: ExplorerNetworkDataUpdate)
    fun validateWatchAddress(address: String, validationCallback: ValidationCallback)
    fun explorerType(): CryptoTypes
}

object Explorers : ExplorerNetworkDataUpdate {

    private var repositories: List<Explorer> = listOf()
    private var apiData: MutableMap<String, WatchAddressBalance> = mutableMapOf()

    fun loadRepositories(explorerProvider: ExplorerProvider) {
        if(repositories.isEmpty()) {
            repositories = explorerProvider.getAllRepositories()
        }
    }

    fun startFeed(watchAddress: MutableList<WatchAddress>, presenterCallback: NetworkCompletionCallback) {
        removeNoLongerValidWatchAddresses(watchAddress)

        repositories.forEach { repository ->
            val array: ArrayList<WatchAddress> = arrayListOf()
            (watchAddress.forEach { watchAddress ->
                if (watchAddress.type.cryptoType == repository.explorerType()) {
                    array.add(watchAddress)
                }
            })
            repository.startAddressFeed(array, presenterCallback, this)

        }
    }

    fun validateWatchAddress(address: String, cryptoType: CryptoTypes, validationCallback: ValidationCallback) {
        repositories.forEach { repository ->
            if (cryptoType == repository.explorerType()) {
                repository.validateWatchAddress(address, validationCallback)
            }
        }
    }

    fun removeNoLongerValidWatchAddresses(watchAddress: MutableList<WatchAddress>) {
        apiData = apiData.filter {
            watchAddress.map { it.address }.contains(it.key) &&
                    watchAddress.map { it.type }.contains(it.value.type)
        }.toMutableMap()
    }


    fun stopFeed() {
        repositories.forEach { repository ->
            repository.stopFeed()
        }
    }

    override fun updateWatchAddressData(address: String, data: WatchAddressBalance) {
        apiData[address] = data
    }

    fun getWatchAddressData(): MutableMap<String, WatchAddressBalance> = apiData

}
