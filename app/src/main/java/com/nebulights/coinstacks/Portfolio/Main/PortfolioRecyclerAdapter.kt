package com.nebulights.coinstacks.Portfolio.Main

import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.nebulights.coinstacks.Extensions.dp
import com.nebulights.coinstacks.R
import com.nebulights.coinstacks.Types.CurrencyTypes

/**
 * Created by babramovitch on 10/26/2017.
 */

class PortfolioRecyclerAdapter(private val presenter: PortfolioContract.Presenter) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder.itemViewType == 0 || holder.itemViewType == 1) {
            holder as ViewHolderHeader
            holder.exchange.text = presenter.getHeader(position)
        } else {
            holder as ViewHolderCoins
            presenter.onBindRepositoryRowViewAtPosition(holder.adapterPosition, holder)
        }

        holder.itemView.setOnClickListener { presenter.rowItemClicked(holder.adapterPosition) }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            0 -> {
                val inflatedView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycler_item_header, parent, false)
                ViewHolderHeader(inflatedView)
            }
            1 -> {
                val inflatedView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycler_item_sub_header, parent, false)
                ViewHolderHeader(inflatedView)
            }
            2 -> {
                val inflatedView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycler_list_item, parent, false)
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

        @BindView(R.id.recycler_item_card)
        lateinit var cardView: CardView

        @BindView(R.id.recycler_address_nick_name_layout)
        lateinit var addressNickNameLayout: LinearLayout

        @BindView(R.id.recycler_ticker)
        lateinit var ticker: TextView

        @BindView(R.id.recycler_last_price_layout)
        lateinit var lastPriceLayout: LinearLayout

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

        @BindView(R.id.recycler_net_value_title)
        lateinit var netValueTitle: TextView

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
            if (holdings == "-1") {
                this.holdings.text = "error"
            } else {
                this.holdings.text = holdings
            }
        }

        override fun setNetValue(netValue: String) {
            this.netValue.text = netValue
        }

        override fun setWatchAddressNickName(addressNickName: String?) {
            this.addressNickName.text = addressNickName
        }

        override fun showQuantities(visible: Boolean) {
            holdingsLayout.visibility = if (visible) View.VISIBLE else View.GONE
        }

        override fun showNetvalue(visible: Boolean) {
            netValueLayout.visibility = if (visible) View.VISIBLE else View.GONE
        }

        override fun setCryptoCurrency() {
            netValueTitle.text = netValueTitle.resources.getString(R.string.net_value_crypto)
            showQuantities(true)
            showNetvalue(true)
            lastPriceLayout.visibility = View.VISIBLE
        }

        override fun setFiatCurrency(currencyTypes: CurrencyTypes) {
            netValueTitle.text = netValueTitle.resources.getString(R.string.net_value_cash, currencyTypes.name)
            showQuantities(false)
            lastPriceLayout.visibility = View.GONE
        }

        override fun showAddressNickName(visible: Boolean) {
            addressNickNameLayout.visibility = if (visible) View.VISIBLE else View.GONE
        }

        override fun adjustRowBottomMargin(amount: Int) {
            val param = netValueLayout.layoutParams as LinearLayout.LayoutParams
            param.setMargins(0, 0, 0, amount);
            netValueLayout.layoutParams = param

            if (amount == 0) {
                val layoutParams = cardView.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.setMargins(0, 0, 0, 0)
                cardView.requestLayout()
            } else {
                val layoutParams = cardView.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.setMargins(0, 0, 0, 5.dp)
                cardView.requestLayout()
            }
        }

        override fun setRowAsStale(isStale: Boolean) {
            if (isStale) {
                cardView.setCardBackgroundColor(
                    ContextCompat.getColor(
                        cardView.context,
                        R.color.card_color_stale_data
                    )
                )
            } else {
                cardView.setCardBackgroundColor(
                    ContextCompat.getColor(
                        cardView.context,
                        R.color.card_color
                    )
                )
            }
        }
    }

    class ViewHolderHeader(view: View) : RecyclerView.ViewHolder(view) {
        @BindView(R.id.recycler_exchange)
        lateinit var exchange: TextView

        @BindView(R.id.card_view_header)
        lateinit var cardView: CardView

        init {
            ButterKnife.bind(this, view)
        }
    }
}
