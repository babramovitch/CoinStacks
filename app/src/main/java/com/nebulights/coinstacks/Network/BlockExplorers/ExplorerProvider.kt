package com.nebulights.coinstacks.Network.BlockExplorers

import com.nebulights.coinstacks.Network.BlockExplorers.BlockExplorer.BlockExplorerRepository
import com.nebulights.coinstacks.Network.BlockExplorers.BlockExplorer.BlockExplorerService

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

    val BLOCKEXPLORER_URL = "https://blockexplorer.com/"

    private fun setupOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY // .BODY for full log output
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
        return BlockExplorerRepository(create(BlockExplorerService::class.java, BLOCKEXPLORER_URL))
    }


    fun getAllRepositories(): List<Explorer> {
        val repositories: MutableList<Explorer> = mutableListOf()
        repositories.add(provideBlockExplorerRepository())
        return repositories
    }
}
