package com.nebulights.coinstacks.Network.exchanges.Gdax

import android.util.Base64

import com.nebulights.coinstacks.CryptoPairs
import com.nebulights.coinstacks.Network.NetworkCompletionCallback
import com.nebulights.coinstacks.Network.NetworkDataUpdate
import com.nebulights.coinstacks.Network.Exchange
import com.nebulights.coinstacks.Network.ExchangeProvider
import com.nebulights.coinstacks.Network.exchanges.BaseExchange
import com.nebulights.coinstacks.Network.exchanges.Gdax.model.AuthenticationDetails
import com.nebulights.coinstacks.Network.exchanges.Quadriga.Hasher
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

/**
 * Created by babramovitch on 10/25/2017.
 */

class GdaxRepository(val service: GdaxService) : BaseExchange(), Exchange {

    override fun feedType(): String = ExchangeProvider.GDAX_NAME

    override fun startPriceFeed(tickers: List<CryptoPairs>, presenterCallback: NetworkCompletionCallback, networkDataUpdate: NetworkDataUpdate) {
        clearDisposables()

//        tickers.forEach { ticker ->
//            startPriceFeed(service.getCurrentTradingInfo(ticker.ticker),
//                    ticker, presenterCallback, networkDataUpdate)
//        }

        startAccountFeed()

    }

    override fun startAccountFeed() {
        startAccountBalanceFeed(
                Observable
                        .defer<AuthenticationDetails> { Observable.just(generateAuthenticationDetails()) }
                        .flatMap<Any> { details -> service.getBalances(details.key, details.signature, details.timestamp, details.passphrase) }
                        .repeatWhen { done -> done.delay(10, TimeUnit.SECONDS) }, feedType())
    }


    override fun generateAuthenticationDetails(): AuthenticationDetails {

        val timestamp = (System.currentTimeMillis() / 1000).toString()
        val method = "GET"
        val passphrase = ""
        val key = ""

        val secret = ""
        val decodedSecret = Base64.decode(secret, Base64.NO_WRAP)

        val mergedString = timestamp + method + "/accounts" + ""

        val signature: String = Hasher.generateHashWithHmac256(mergedString.toByteArray(), decodedSecret)

        return AuthenticationDetails(key, signature, timestamp, passphrase)

    }
}
