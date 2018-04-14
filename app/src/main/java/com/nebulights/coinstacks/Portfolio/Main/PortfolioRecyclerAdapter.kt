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
import android.view.LayoutInflater


/**
 * Created by babramovitch on 10/26/2017.
 */

class PortfolioRecyclerAdapter(private val presenter: PortfolioContract.Presenter) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder.itemViewType == 0) {
            holder as ViewHolderHeader
            holder.exchange.text = presenter.getHeader(position)
        } else {
            holder as ViewHolderCoins
            presenter.onBindRepositoryRowViewAtPosition(holder.adapterPosition, holder)
        }

        //TODO Launch edit fragment or list of items under that exch
        holder.itemView.setOnClickListener { presenter.rowItemClicked(holder.adapterPosition) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            when (viewType) {
                0 -> {
                    val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_header, parent, false)
                    //val inflatedView = parent.inflate(R.layout.recycler_item_header, false)
                    ViewHolderHeader(inflatedView)
                }
                1 -> {
                    val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_list_item, parent, false)
                    // val inflatedView = parent.inflate(R.layout.recycler_list_item, false)
                    // inflatedView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

                    ViewHolderCoins(inflatedView)
                }

                else -> throw IllegalArgumentException("Wrong type of view")
            }


    override fun getItemViewType(position: Int): Int {
        return presenter.recyclerViewType(position)
    }

    override fun getItemCount(): Int = presenter.displayItemCount()

    class ViewHolderCoins(view: View) : RecyclerView.ViewHolder(view), PortfolioContract.ViewRow {

        @BindView(R.id.recycler_address_nick_name)
        lateinit var addressNickName: TextView

        @BindView(R.id.recycler_address_nick_name_layout)
        lateinit var addressNickNameLayout: LinearLayout

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

        override fun setWatchAddressNickName(addressNickName: String?) {
            this.addressNickName.text = addressNickName
        }

        override fun showQuantities(visible: Boolean) {
            holdingsLayout.visibility = if (visible) View.VISIBLE else View.GONE
            netValueLayout.visibility = if (visible) View.VISIBLE else View.GONE
        }

        override fun showAddressNickName(visible: Boolean) {
            addressNickNameLayout.visibility = if (visible) View.VISIBLE else View.GONE
        }
    }

    class ViewHolderHeader(view: View) : RecyclerView.ViewHolder(view) {

        @BindView(R.id.recycler_exchange)
        lateinit var exchange: TextView

        init {
            ButterKnife.bind(this, view)
        }

    }
}
