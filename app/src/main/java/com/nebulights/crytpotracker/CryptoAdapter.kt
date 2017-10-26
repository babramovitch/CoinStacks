package com.nebulights.crytpotracker

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife

/**
 * Created by babramovitch on 10/26/2017.
 */

class CryptoAdapter(private val presenter: PortfolioContract.Presenter) : RecyclerView.Adapter<CryptoAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: CryptoAdapter.ViewHolder, position: Int) {
        presenter.onBindRepositoryRowViewAtPosition(position, holder);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoAdapter.ViewHolder {
        val inflatedView = parent.inflate(R.layout.recycler_list_item, false)
        return ViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return presenter.tickerCount()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener, PortfolioContract.ViewRow {

        @BindView(R.id.recycler_ticker)
        lateinit var ticker: TextView

        @BindView(R.id.recycler_last_price)
        lateinit var lastPrice: TextView

        @BindView(R.id.recycler_holdings)
        lateinit var holdings: TextView

        init {
            ButterKnife.bind(this, view)
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            Log.d("RecyclerView", "CLICK!")
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
    }
}



