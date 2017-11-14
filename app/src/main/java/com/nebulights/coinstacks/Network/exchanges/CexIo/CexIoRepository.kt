package com.nebulights.coinstacks.Network.exchanges.CexIo

import com.nebulights.coinstacks.CryptoPairs
import com.nebulights.coinstacks.Network.ExchangeProvider
import com.nebulights.coinstacks.Network.NetworkCompletionCallback
import com.nebulights.coinstacks.Network.NetworkDataUpdate
import com.nebulights.coinstacks.Network.exchanges.BaseExchange

/**
 * Created by babramovitch on 10/25/2017.
 */

class CexIoRepository(private val service: CexIoService) : BaseExchange() {
    override fun feedType(): String {
        return ExchangeProvider.CEXIO_NAME
    }

    override fun startFeed(tickers: List<CryptoPairs>, presenterCallback: NetworkCompletionCallback, networkDataUpdate: NetworkDataUpdate) {
        clearDisposables()

        tickers.forEach { ticker ->
            startFeed(service.getCurrentTradingInfo(ticker.cryptoType.name, ticker.currencyType.name),
                    ticker, presenterCallback, networkDataUpdate)
        }
    }
}
