package com.nebulights.coinstacks.Network.BlockExplorers.BlockCypher

import com.nebulights.coinstacks.Network.BlockExplorers.BaseExplorer
import com.nebulights.coinstacks.Network.BlockExplorers.ExplorerNetworkDataUpdate
import com.nebulights.coinstacks.Network.BlockExplorers.Model.WatchAddress
import com.nebulights.coinstacks.Network.ValidationCallback
import com.nebulights.coinstacks.Network.exchanges.NetworkCompletionCallback
import com.nebulights.coinstacks.Types.CryptoTypes
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch


/**
 * Created by babramovitch on 10/25/2017.
 */

class BlockCypherRepository(private val service: BlockCypherService) : BaseExplorer() {
    private val rateLimitDelayInMillis = 1000

    override fun explorerType(): CryptoTypes = CryptoTypes.ETH

    override fun startAddressFeed(address: ArrayList<WatchAddress>, presenterCallback: NetworkCompletionCallback, explorerNetworkDataUpdate: ExplorerNetworkDataUpdate) {
        clearBalanceDisposables()

        launch {
            address.forEach { address ->
                startFeed(service.getBalancesForAddresses(address.address), address, presenterCallback, explorerNetworkDataUpdate)
                delay(rateLimitDelayInMillis)
            }
        }
    }

    override fun validateWatchAddress(address: String, validationCallback: ValidationCallback) {
        validateAddress(service.getBalancesForAddresses(address), validationCallback)
    }
}
