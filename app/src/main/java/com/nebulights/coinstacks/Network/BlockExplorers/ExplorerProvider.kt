package com.nebulights.coinstacks.Network.BlockExplorers

import com.nebulights.coinstacks.Network.BlockExplorers.BlockCypher.BlockCypherRepository
import com.nebulights.coinstacks.Network.BlockExplorers.BlockCypher.BlockCypherService
import com.nebulights.coinstacks.Network.BlockExplorers.BlockExplorer.*
import com.nebulights.coinstacks.Network.BlockExplorers.BlockExplorerBCH.BlockDozerRepository
import com.nebulights.coinstacks.Network.BlockExplorers.BlockExplorerBCH.BlockDozerService

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Created by babramovitch on 10/25/2017.
 */

object ExplorerProvider {

    private val client: OkHttpClient = setupOkHttpClient()

    val BLOCKEXPLORER_BTC_URL = "https://blockexplorer.com/"
    val BLOCKCYPHER_URL = "https://api.blockcypher.com/"
    val LITECORE_URL = "https://insight.litecore.io/"
    val BLOCKDOZER_URL = "https://blockdozer.com/"
    val RIPPLE_URL = "https://data.ripple.com/"

    private fun setupOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BASIC // .BODY for full log output
        return OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    private fun <T> create(service: Class<T>, baseUrl: String): T {
        val retrofit = Retrofit.Builder()
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build()

        return retrofit.create(service)
    }

    private fun provideBlockExplorerRepository(): BlockExplorerRepository {
        return BlockExplorerRepository(create(BlockExplorerService::class.java, BLOCKEXPLORER_BTC_URL))
    }

    private fun provideBlockDozerRepository(): BlockDozerRepository {
        return BlockDozerRepository(create(BlockDozerService::class.java, BLOCKDOZER_URL))
    }

    private fun provideLiteCoreRepository(): LiteCoreRepository {
        return LiteCoreRepository(create(LiteCoreService::class.java, LITECORE_URL))
    }

    private fun provideBlockCypherRepository(): BlockCypherRepository {
        return BlockCypherRepository(create(BlockCypherService::class.java, BLOCKCYPHER_URL))
    }

    private fun provideRippleRepository(): RippleRepository {
        return RippleRepository(create(RippleService::class.java, RIPPLE_URL))
    }

    fun getAllRepositories(): List<Explorer> {
        val repositories: MutableList<Explorer> = mutableListOf()
        repositories.add(provideBlockExplorerRepository())
        repositories.add(provideBlockDozerRepository())
        repositories.add(provideBlockCypherRepository())
        repositories.add(provideLiteCoreRepository())
        repositories.add(provideRippleRepository())
        return repositories
    }
}
