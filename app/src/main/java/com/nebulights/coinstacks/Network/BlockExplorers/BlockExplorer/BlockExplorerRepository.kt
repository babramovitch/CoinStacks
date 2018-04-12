package com.nebulights.coinstacks.Network.BlockExplorers.BlockExplorer

import com.nebulights.coinstacks.Network.*
import com.nebulights.coinstacks.Network.BlockExplorers.BaseExplorer
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch


/**
 * Created by babramovitch on 10/25/2017.
 */

class BlockExplorerRepository(private val service: BlockExplorerService) : BaseExplorer() {

    private val rateLimitDelayInMillis = 1000

    override fun startAddressFeed(address: ArrayList<String>, presenterCallback: NetworkCompletionCallback, networkDataUpdate: NetworkDataUpdate) {
        launch {
            address.forEach { address ->
                startFeed(service.getBalancesForAddresses(address), presenterCallback, networkDataUpdate)
                delay(rateLimitDelayInMillis)
            }
        }
    }
}
