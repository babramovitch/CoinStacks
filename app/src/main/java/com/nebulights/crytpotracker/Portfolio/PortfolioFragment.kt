package com.nebulights.crytpotracker.Portfolio

import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.view.*
import android.widget.EditText
import com.nebulights.crytpotracker.CryptoTypes
import com.nebulights.crytpotracker.R
import com.nebulights.crytpotracker.notNull

class PortfolioFragment : Fragment(), PortfolioContract.View {

    @BindView(R.id.net_worth) lateinit var netWorth: TextView
    @BindView(R.id.recycler_view) lateinit var recyclerView: RecyclerView

    private var presenter: PortfolioContract.Presenter? = null
    private var dialog: AlertDialog? = null

    private lateinit var linearLayoutManager: LinearLayoutManager

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
        recyclerView.adapter = PortfolioRecyclerAdapter(presenter!!)

        netWorth.text = getString(R.string.networth, presenter!!.getNetWorth())

        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onResume() {
        presenter.notNull { presenter!!.startFeed() }
        super.onResume()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.clear -> {
                presenter.notNull { presenter!!.clearAssets() }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showCreateAssetDialog(cryptoType: CryptoTypes?, currentQuantity: String) {
        if (cryptoType == null) {
            showErrorDialogCouldNotFindCrypto()
        } else {
            val input = activity.layoutInflater.inflate(R.layout.add_asset_dialog, null)
            val quantityLayout = input.findViewById<TextInputLayout>(R.id.crypto_layout_quantity)
            val quantity = input.findViewById<EditText>(R.id.crypto_quantity)
            val price = input.findViewById<EditText>(R.id.crypto_price)

            quantityLayout.isHintAnimationEnabled = currentQuantity == ""
            quantity.setText(if (currentQuantity == "0.0") "" else currentQuantity)
            quantity.setSelection(quantity.text.length)

            val builder = AlertDialog.Builder(activity)
            builder.setTitle(getString(R.string.dialog_title, cryptoType.name))
            builder.setView(input)
            builder.setPositiveButton(getString(R.string.dialog_ok), { dialog, which ->
                presenter!!.createAsset(cryptoType, quantity.text.toString(), price.text.toString())
            })
            builder.setNegativeButton(getString(R.string.dialog_cancel), { dialog, which -> dialog.cancel() })

            dialog = builder.create()
            dialog.notNull {
                dialog!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
                dialog!!.show()
            }
        }
    }

    fun showErrorDialogCouldNotFindCrypto() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Error")
        builder.setMessage(getString(R.string.dialog_message_error))
        builder.setPositiveButton(getString(R.string.dialog_ok), { dialog, which ->
            dialog.dismiss()
        })

        dialog = builder.create()
        dialog.notNull {
            dialog!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            dialog!!.show()
        }
    }

    override fun updateUi(position: Int) {
        netWorth.text = getString(R.string.networth, presenter!!.getNetWorth())
        if (position != -1) {
            recyclerView.adapter.notifyItemChanged(position)
        } else {
            recyclerView.adapter.notifyDataSetChanged()
        }
    }

    override fun resetUi() {
        netWorth.text = getString(R.string.networth, getString(R.string.zero_dollar))
        recyclerView.adapter.notifyDataSetChanged()
    }

    override fun onPause() {
        presenter.notNull { presenter!!.stopFeed() }
        super.onPause()
    }

    override fun onDestroy() {
        dialog.notNull { dialog!!.dismiss() }
        super.onDestroy()
    }
}
