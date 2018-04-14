package com.nebulights.coinstacks.Network.BlockExplorers.Model

import com.nebulights.coinstacks.Types.CryptoPairs


/**
 * Created by babramovitch on 4/11/2018.
 *
 */
class WatchAddressBalance(val exchange: String, val address: String, val nickName: String, val type: CryptoPairs, val balance: String)