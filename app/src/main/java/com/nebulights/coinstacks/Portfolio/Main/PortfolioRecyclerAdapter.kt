package com.nebulights.coinstacks.Portfolio.Main

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.nebulights.coinstacks.R
import com.nebulights.coinstacks.Extensions.inflate

/**
 * Created by babramovitch on 10/26/2017.
 */

class PortfolioRecyclerAdapter(private val presenter: PortfolioContract.Presenter) : RecyclerView.Adapter<PortfolioRecyclerAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        presenter.onBindRepositoryRowViewAtPosition(holder.adapterPosition, holder)
        holder.itemView.setOnClickListener { presenter.showCreateAssetDialog(holder.adapterPosition) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = parent.inflate(R.layout.recycler_list_item, false)
        return ViewHolder(inflatedView)
    }

    override fun getItemCount(): Int = presenter.tickerCount()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view), PortfolioContract.ViewRow {

        @BindView(R.id.recycler_ticker)
        lateinit var ticker: TextView

        @BindView(R.id.recycler_exchange)
        lateinit var exchange: TextView

        @BindView(R.id.recycler_last_price)
        lateinit var lastPrice: TextView

        @BindView(R.id.recycler_holdings)
        lateinit var holdings: TextView

        @BindView(R.id.recycler_holdings_layout)
        lateinit var holdingsLayout: LinearLayout

        @BindView(R.id.recycler_net_value)
        lateinit var netValue: TextView

        @BindView(R.id.recycler_net_value_layout)
        lateinit var netValueLayout: LinearLayout

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

        override fun showQuantities(visible: Boolean) {
            holdingsLayout.visibility = if (visible) View.VISIBLE else View.GONE
            netValueLayout.visibility = if (visible) View.VISIBLE else View.GONE
        }
    }
}
