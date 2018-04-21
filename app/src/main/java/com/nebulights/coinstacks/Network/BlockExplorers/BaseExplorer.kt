package com.nebulights.coinstacks.Network.BlockExplorers

import com.nebulights.coinstacks.Network.BlockExplorers.Model.WatchAddress
import com.nebulights.coinstacks.Network.BlockExplorers.Model.WatchAddressBalance
import com.nebulights.coinstacks.Network.ValidationCallback
import com.nebulights.coinstacks.Network.exchanges.NetworkCompletionCallback
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.TimeUnit

/**
 * Created by babramovitch on 10/25/2017.
 */

abstract class BaseExplorer : Explorer {

    private var balanceDisposables: CompositeDisposable = CompositeDisposable()

    fun clearBalanceDisposables() {
        if (balanceDisposables.size() != 0) {
            balanceDisposables.clear()
        }
    }

    fun <T> startFeed(observable: Observable<T>, watchAddress: WatchAddress, presenterCallback: NetworkCompletionCallback, explorerNetworkDataUpdate: ExplorerNetworkDataUpdate) {
        val disposable = observable.observeOn(AndroidSchedulers.mainThread())
                .repeatWhen { result -> result.delay(60, TimeUnit.SECONDS) }
                .retryWhen { error -> error.delay(60, TimeUnit.SECONDS) }
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->

                    val watchAddressBalance: WatchAddressBalance
                    var amount: BigDecimal

                    if (result is String) {
                        amount = (BigDecimal(result).setScale(8) / BigDecimal("100000000").setScale(8)).stripTrailingZeros()
                    } else if (result is NormalizedEthereumBalanceData) {
                        amount = result.getAddressBalance()
                    } else {
                        amount = BigDecimal.ZERO
                    }

                    amount = amount.setScale(4, RoundingMode.DOWN)

                    watchAddressBalance = WatchAddressBalance(watchAddress.exchange, watchAddress.address, watchAddress.nickName, watchAddress.type, amount.toPlainString())

                    explorerNetworkDataUpdate.updateWatchAddressData(watchAddress.address, watchAddressBalance)
                    presenterCallback.updateUi(watchAddressBalance)
                }, { error ->
                    error.printStackTrace()
                })

        balanceDisposables.add(disposable)
    }

    fun <T> validateAddress(observable: Observable<T>, presenterCallback: ValidationCallback) {
        val disposable = observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    presenterCallback.validationSuccess()
                }, { error ->
                    presenterCallback.validationError("explorer", "Error Validating Address")
                    error.printStackTrace()
                })

        balanceDisposables.add(disposable)
    }

    override fun stopFeed() {
        balanceDisposables.clear()
    }
}