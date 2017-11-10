package com.nebulights.crytpotracker.Portfolio

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.view.*
import com.nebulights.crytpotracker.CryptoPairs
import com.nebulights.crytpotracker.R
import com.nebulights.crytpotracker.notNull
import android.widget.*

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class PortfolioFragment : Fragment(), PortfolioContract.View {

    @BindView(R.id.net_worth) lateinit var netWorth: TextView
    @BindView(R.id.recycler_view) lateinit var recyclerView: RecyclerView

    private lateinit var presenter: PortfolioContract.Presenter
    private lateinit var linearLayoutManager: LinearLayoutManager

    private var dialog: AlertDialog? = null

    companion object {
        fun newInstance(): PortfolioFragment {
            return PortfolioFragment()
        }
    }

    override fun setPresenter(presenter: PortfolioContract.Presenter) {
        this.presenter = presenter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater!!.inflate(R.layout.fragment_crypto_list, container, false)
        ButterKnife.bind(this, rootView)

        linearLayoutManager = LinearLayoutManager(activity)

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = PortfolioRecyclerAdapter(presenter)

        netWorth.text = getString(R.string.networth, presenter.getNetWorth())

        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onResume() {
        presenter.startFeed()
        super.onResume()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.clear -> {
                presenter.clearAssets()
            }

            R.id.add_ticker -> {
                presenter.showAddNewAssetDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("InflateParams")
    override fun showCreateAssetDialog(cryptoPair: CryptoPairs?, currentQuantity: String) {
        if (cryptoPair == null) {
            showErrorDialogCouldNotFindCrypto()
        } else {
            val input = activity.layoutInflater.inflate(R.layout.add_asset_dialog, null)
            val quantityLayout = input.findViewById<TextInputLayout>(R.id.crypto_layout_quantity)
            val quantity = input.findViewById<EditText>(R.id.crypto_quantity)
            val price = input.findViewById<EditText>(R.id.crypto_price)

            input.findViewById<LinearLayout>(R.id.spinner_exchange_layout).visibility = View.GONE
            input.findViewById<LinearLayout>(R.id.spinner_crypto_layout).visibility = View.GONE

            quantityLayout.isHintAnimationEnabled = currentQuantity == ""
            quantity.setText(if (currentQuantity == getString(R.string.zero_quantity)) "" else currentQuantity)
            quantity.setSelection(quantity.text.length)

            val builder = AlertDialog.Builder(activity)
            builder.setTitle(getString(R.string.dialog_title, (cryptoPair.CryptoType.name + " : " + cryptoPair.currencyType.name)))
            builder.setView(input)
            builder.setPositiveButton(getString(R.string.dialog_ok), { dialog, which ->
                presenter.createAsset(cryptoPair, quantity.text.toString(), price.text.toString())
            })
            builder.setNegativeButton(getString(R.string.dialog_cancel), { dialog, which -> dialog.cancel() })
            builder.setNeutralButton(getString(R.string.dialog_delete), { dialog, which ->
                presenter.removeAsset(cryptoPair)
            })

            showDialog(builder.create(), true)
        }
    }

    override fun showAddNewAssetDialog() {
        val input = View.inflate(activity, R.layout.add_asset_dialog, null)
        val quantity = input.findViewById<EditText>(R.id.crypto_quantity)
        val price = input.findViewById<EditText>(R.id.crypto_price)

        val spinnerExchanges = input.findViewById<Spinner>(R.id.spinner_exchange)
        val exchangeList = resources.getStringArray(R.array.exchanges)
        val spinnerExchangeArrayAdapter = ArrayAdapter(activity, android.R.layout.simple_spinner_item, exchangeList)

        val spinnerCryptos = input.findViewById<Spinner>(R.id.spinner_crypto)
        var cryptoList: List<String> = listOf()

        spinnerExchanges.adapter = spinnerExchangeArrayAdapter
        spinnerExchanges.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                cryptoList = presenter.getTickersForExchange(exchangeList[position])
                spinnerCryptos.adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_item, cryptoList)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        quantity.setSelection(quantity.text.length)

        val builder = AlertDialog.Builder(activity)
        builder.setTitle(getString(R.string.dialog_add_item_title))
        builder.setView(input)
        builder.setPositiveButton(getString(R.string.dialog_ok), { dialog, which ->
            presenter.createAsset(exchangeList[spinnerExchanges.selectedItemPosition],
                    cryptoList[spinnerCryptos.selectedItemPosition],
                    quantity.text.toString(),
                    price.text.toString())
        })
        builder.setNegativeButton(getString(R.string.dialog_cancel), { dialog, which -> dialog.cancel() })

        showDialog(builder.create(), true)
    }

    private fun showErrorDialogCouldNotFindCrypto() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(getString(R.string.dialog_title_error))
        builder.setMessage(getString(R.string.dialog_message_error))
        builder.setPositiveButton(getString(R.string.dialog_ok), { dialog, which ->
            dialog.dismiss()
        })

        showDialog(builder.create(), false)
    }

    private fun showDialog(dialog: AlertDialog, raiseKeyboard: Boolean) {
        this.dialog = dialog
        dialog.notNull {
            if (raiseKeyboard) {
                dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            }
            dialog.show()
        }
    }

    override fun updateUi(position: Int) {
        netWorth.text = getString(R.string.networth, presenter.getNetWorth())
        recyclerView.adapter.notifyItemChanged(position)
    }

    override fun removeItem(position: Int) {
        netWorth.text = getString(R.string.networth, presenter.getNetWorth())
        recyclerView.adapter.notifyItemRemoved(position)
    }

    override fun resetUi() {
        netWorth.text = getString(R.string.networth, getString(R.string.zero_dollar))
        recyclerView.adapter.notifyDataSetChanged()
    }

    override fun onPause() {
        presenter.stopFeed()
        super.onPause()
    }

    override fun onDestroy() {
        presenter.onDetach()
        dialog.notNull { dialog!!.dismiss() }
        super.onDestroy()
    }
}
