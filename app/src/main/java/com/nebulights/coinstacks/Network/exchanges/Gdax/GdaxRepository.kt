package com.nebulights.coinstacks.Network.exchanges.Gdax

import android.util.Base64
import com.nebulights.coinstacks.Constants
import com.nebulights.coinstacks.Network.ValidationCallback
import com.nebulights.coinstacks.Network.exchanges.*
import com.nebulights.coinstacks.Network.exchanges.Gdax.model.AuthenticationDetails
import com.nebulights.coinstacks.Network.exchanges.Models.BasicAuthentication
import com.nebulights.coinstacks.Network.security.HashGenerator
import com.nebulights.coinstacks.Network.security.HashingAlgorithms
import com.nebulights.coinstacks.Types.CryptoPairs
import io.reactivex.Observable
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

/**
 * Created by babramovitch on 10/25/2017.
 */

class GdaxRepository(private val service: GdaxService) : BaseExchange(), Exchange {

    override val userNameRequired: Boolean = false
    override val passwordRequired: Boolean = true

    override fun feedType(): String = ExchangeProvider.GDAX_NAME

    override fun startPriceFeed(tickers: List<CryptoPairs>, presenterCallback: NetworkCompletionCallback, exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate) {
        clearTickerDisposables()

        launch {
            tickers.forEach { ticker ->
                startPriceFeed(service.getCurrentTradingInfo(ticker.ticker),
                        ticker, presenterCallback, exchangeNetworkDataUpdate)
                if (tickers.size > Constants.rateLimitSizeThreshold) {
                    delay(Constants.tickerDelayInMillis)
                }
            }
        }
    }

    override fun startAccountFeed(basicAuthentication: BasicAuthentication, presenterCallback: NetworkCompletionCallback, exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate) {
        startAccountBalanceFeed(
                Observable
                        .defer<AuthenticationDetails> {
                            Observable.just(
                                    generateAuthenticationDetails(basicAuthentication))
                        }
                        .flatMap<Any> { details ->
                            service.getBalances(
                                    details.key,
                                    details.signature,
                                    details.timestamp,
                                    details.passphrase)
                        }, basicAuthentication,
                presenterCallback,
                exchangeNetworkDataUpdate)
    }

    override fun validateApiKeys(basicAuthentication: BasicAuthentication, presenterCallback: ValidationCallback, exchangeNetworkDataUpdate: ExchangeNetworkDataUpdate) {
        try {
            val details = generateAuthenticationDetails(basicAuthentication)

            validateAPiKeys(service.getBalances(
                    details.key,
                    details.signature,
                    details.timestamp,
                    details.passphrase)
                    , basicAuthentication,
                    presenterCallback,
                    exchangeNetworkDataUpdate)

        } catch (exception: IllegalArgumentException) {
            presenterCallback.validationError("Error using Secret Key.  Verify all details are correct")
            return
        }
    }

    override fun generateAuthenticationDetails(basicAuthentication: BasicAuthentication): AuthenticationDetails {
        val timestamp = (System.currentTimeMillis() / 1000).toString()
        val method = "GET"
        val api = "/accounts"
        val secret = basicAuthentication.apiSecret
        val decodedSecret = Base64.decode(secret, Base64.NO_WRAP)

        val message = timestamp + method + api + ""

        val signatureBytes = HashGenerator.generateHmac(message.toByteArray(), decodedSecret,
                HashingAlgorithms.HmacSHA256)

        val signature = Base64.encodeToString(signatureBytes, Base64.NO_WRAP)

        return AuthenticationDetails(basicAuthentication.apiKey, signature, timestamp, basicAuthentication.password)

    }
}
