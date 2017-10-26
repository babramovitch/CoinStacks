package com.nebulights.crytpotracker

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.nebulights.crytpotracker.Quadriga.CurrentTradingInfo

/**
 * Created by babramovitch on 10/26/2017.
 */

class CryptoAdapter(private val holdingData: MutableMap<String, Double>,
                    private val tradingData: MutableMap<String, CurrentTradingInfo>) : RecyclerView.Adapter<CryptoAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: CryptoAdapter.ViewHolder, position: Int) {
        val currentTradingInfo = tradingData[getOrderedTicker(position)]

        holder.ticker.text = getOrderedTicker(position).replace("_", ":")

        currentTradingInfo.notNull {
            holder.lastPrice.text = currentTradingInfo!!.last
            holder.holdings.text = holdingData[getOrderedTicker(position)].toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoAdapter.ViewHolder {
        val inflatedView = parent.inflate(R.layout.recycler_list_item, false)
        return ViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return 4 //determined by how many pairs I want to display - should be derived from a constant list
    }

    private fun getOrderedTicker(position: Int): String {

        return when (position) {
            0 -> "BTC_CAD"
            1 -> "BCH_CAD"
            2 -> "ETH_CAD"
            3 -> "LTC_CAD"
            else -> ""
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

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
    }
}



