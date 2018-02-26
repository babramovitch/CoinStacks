package com.nebulights.coinstacks.Network.exchanges.Quadriga

import com.nebulights.coinstacks.CryptoPairs
import com.nebulights.coinstacks.Network.ExchangeProvider
import com.nebulights.coinstacks.Network.NetworkCompletionCallback
import com.nebulights.coinstacks.Network.NetworkDataUpdate
import com.nebulights.coinstacks.Network.exchanges.BaseExchange
import com.nebulights.coinstacks.Network.exchanges.Quadriga.model.AuthenticationDetails
import io.reactivex.Observable

/**
 * Created by babramovitch on 10/25/2017.
 */

class QuadrigaRepository(private val service: QuadrigaService) : BaseExchange() {

    override fun feedType(): String = ExchangeProvider.QUADRIGACX_NAME

    override fun startAccountFeed() {
        super.startAccountBalanceFeed(Observable
                .defer<AuthenticationDetails> { Observable.just(generateAuthenticationDetails()) }
                .flatMap<Any> { balances -> service.getBalances(balances) }, feedType())
    }

    override fun startPriceFeed(tickers: List<CryptoPairs>, presenterCallback: NetworkCompletionCallback, networkDataUpdate: NetworkDataUpdate) {
        clearDisposables()

//        tickers.forEach { ticker ->
//            startPriceFeed(service.getCurrentTradingInfo(ticker.ticker),
//                    ticker, presenterCallback, networkDataUpdate)
//        }

        //NOTE code 300 if permission denied - check key for permission and warn user if key has permission
        startAccountFeed()

    }

    override fun generateAuthenticationDetails(): AuthenticationDetails {

        val nonce = System.currentTimeMillis()
        val clientId = ""
        val secret = ""
        val key = ""

        val mergedString = nonce.toString() + clientId + key
        val signature = Hasher.generateDigestHashWithHmac256(mergedString.toByteArray(), secret.toByteArray())

        return AuthenticationDetails(key, nonce, signature)

    }
}
