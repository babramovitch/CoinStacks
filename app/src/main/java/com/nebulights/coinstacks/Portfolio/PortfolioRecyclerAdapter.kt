package com.nebulights.coinstacks.Portfolio

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.nebulights.coinstacks.R
import com.nebulights.coinstacks.inflate

/**
* Created by babramovitch on 10/26/2017.
*/

class PortfolioRecyclerAdapter(private val presenter: PortfolioContract.Presenter) : RecyclerView.Adapter<PortfolioRecyclerAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        presenter.onBindRepositoryRowViewAtPosition(position, holder)
        holder.itemView.setOnClickListener { presenter.showCreateAssetDialog(position) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = parent.inflate(R.layout.recycler_list_item, false)
        return ViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return presenter.tickerCount()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view), PortfolioContract.ViewRow {


        @BindView(R.id.recycler_ticker)
        lateinit var ticker: TextView

        @BindView(R.id.recycler_exchange)
        lateinit var exchange: TextView

        @BindView(R.id.recycler_last_price)
        lateinit var lastPrice: TextView

        @BindView(R.id.recycler_holdings)
        lateinit var holdings: TextView

        @BindView(R.id.recycler_net_value)
        lateinit var netValue: TextView

        init {
            ButterKnife.bind(this, view)
        }

        override fun setExchange(exchange: String) {
            this.exchange.text = exchange
        }

        override fun setTicker(ticker: String) {
            this.ticker.text = ticker
        }

        override fun setLastPrice(lastPrice: String) {
            this.lastPrice.text = lastPrice
        }

        override fun setHoldings(holdings: String) {
            this.holdings.text = holdings
        }

        override fun setNetValue(netValue: String) {
            this.netValue.text = netValue
        }
    }
}
