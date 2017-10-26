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

class CryptoAdapter(private val tradingData: MutableMap<String, CurrentTradingInfo>) : RecyclerView.Adapter<CryptoAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: CryptoAdapter.ViewHolder, position: Int) {
        val currentTradingInfo = tradingData[getOrderedTicker(position)]

        holder.ticker.text = getOrderedTicker(position).replace("_",":")

        currentTradingInfo.notNull {
            holder.lastPrice.text = currentTradingInfo!!.last
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoAdapter.ViewHolder {
        val inflatedView = parent.inflate(R.layout.recycler_list_item, false)
        return ViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return 4 //determined by how many pairs I want to display - should be derived from a constant list
    }

    fun getOrderedTicker(position: Int): String {

        var ticker: String = ""

        when (position) {
            0 -> return "BTC_CAD"
            1 -> return "BCH_CAD"
            2 -> return "ETH_CAD"
            3 -> return "LTC_CAD"
        }

        return ticker

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        @BindView(R.id.recycler_ticker)
        lateinit var ticker: TextView

        @BindView(R.id.recycler_last_price)
        lateinit var lastPrice: TextView

        init {
            ButterKnife.bind(this, view)
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            Log.d("RecyclerView", "CLICK!")
        }

        companion object {
            private val VIEW_HOLDER_KEY = "ASSET"
        }
    }

}



