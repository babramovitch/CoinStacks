package com.nebulights.coinstacks.Portfolio.Main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.view.*
import com.nebulights.coinstacks.CryptoPairs
import com.nebulights.coinstacks.R
import com.nebulights.coinstacks.Extensions.notNull
import android.widget.*
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class PortfolioFragment : Fragment(), PortfolioContract.View {

    @BindView(R.id.net_worth_amount) lateinit var netWorth: TextView
    @BindView(R.id.net_worth_amount_layout) lateinit var netWorthLayout: LinearLayout
    @BindView(R.id.recycler_view) lateinit var recyclerView: RecyclerView

    private lateinit var presenter: PortfolioContract.Presenter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var menu: Menu

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

        netWorth.text = presenter.getNetWorthDisplayString()

        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        this.menu = menu
        inflater.inflate(R.menu.menu, menu)
        presenter.setAssetLockedState()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onResume() {
        presenter.startFeed()
        super.onResume()
    }

    override fun showAssetQuantites(isVisible: Boolean) {
        menu.findItem(R.id.locked_data).isVisible = !isVisible
        menu.findItem(R.id.add_ticker).isVisible = isVisible
        menu.findItem(R.id.unlocked_data).isVisible = isVisible
        menu.findItem(R.id.clear).isVisible = isVisible

        recyclerView.adapter.notifyDataSetChanged()

        netWorth.text = presenter.getNetWorthDisplayString()
        netWorthLayout.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.clear -> {
                presenter.showConfirmDeleteAllDialog()
            }

            R.id.add_ticker -> {
                presenter.showAddNewAssetDialog()
            }

            R.id.locked_data -> {
                presenter.unlockData()
            }

            R.id.unlocked_data -> {
                presenter.lockData()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showConfirmDeleteAllDialog() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(getString(R.string.remove_assets_title))
        builder.setMessage(getString(R.string.remove_all_assets_message))
        builder.setPositiveButton(getString(R.string.dialog_ok), { dialog, which ->
            presenter.clearAssets()
        })

        builder.setNegativeButton(getString(R.string.dialog_cancel), { dialog, which -> dialog.cancel() })

        showDialog(builder.create(), false)
    }

    override fun showCreateAssetDialog(cryptoPair: CryptoPairs?, currentQuantity: String) {
        if (cryptoPair == null) {
            showErrorDialogCouldNotFindCrypto()
        } else {
            val input = View.inflate(activity, R.layout.add_asset_dialog, null)
            val quantity = input.findViewById<EditText>(R.id.crypto_quantity)
            val price = input.findViewById<EditText>(R.id.crypto_price)

            input.findViewById<LinearLayout>(R.id.spinner_exchange_layout).visibility = View.GONE
            input.findViewById<LinearLayout>(R.id.spinner_crypto_layout).visibility = View.GONE

            quantity.setText(if (currentQuantity == getString(R.string.zero_quantity)) "" else currentQuantity)
            quantity.setSelection(quantity.text.length)

            val builder = AlertDialog.Builder(activity)
            builder.setTitle(getString(R.string.dialog_title, cryptoPair.exchange, (cryptoPair.cryptoType.name + " : " + cryptoPair.currencyType.name)))
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

    override fun showUnlockDialog(firstAttempt: Boolean) {
        val input = View.inflate(activity, R.layout.password_dialog, null)
        input.findViewById<LinearLayout>(R.id.new_password_confirm_layout).visibility = View.GONE

        val password = input.findViewById<EditText>(R.id.password)
        val passwordObservable = RxTextView.textChanges(password).skip(1)

        passwordObservable.subscribe({ text ->
            dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = text.length == 4
        })

        val builder = AlertDialog.Builder(activity)
        builder.setTitle(if (firstAttempt) getString(R.string.enter_password) else getString(R.string.invalid_password))
        builder.setView(input)
        builder.setPositiveButton(getString(R.string.dialog_ok), { dialog, which ->
            presenter.unlockData(password.text.toString())
        })

        builder.setNegativeButton(getString(R.string.dialog_cancel), { dialog, which -> dialog.cancel() })

        builder.setNeutralButton(getString(R.string.forgot_password), { dialog, which ->
            presenter.clearAssets()
        })

        showDialog(builder.create(), true)
        dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = false
    }

    override fun showAddNewPasswordDialog() {

        val input = View.inflate(activity, R.layout.password_dialog, null)
        val password = input.findViewById<EditText>(R.id.password)
        val passwordConfirm = input.findViewById<EditText>(R.id.new_password_confirm)

        val passwordObservable = RxTextView.textChanges(password).skip(1)
        val confirmPasswordObservable = RxTextView.textChanges(passwordConfirm).skip(1)

        val isPasswordValid: Observable<Boolean> = Observable.combineLatest(
                passwordObservable,
                confirmPasswordObservable,
                BiFunction { password, confirmPassword -> password.length == 4 && password.toString() == confirmPassword.toString() })

        isPasswordValid.subscribe { isValid ->
            dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = isValid
        }

        val builder = AlertDialog.Builder(activity)
        builder.setTitle(getString(R.string.add_password_dialog_title))
        builder.setView(input)
        builder.setPositiveButton(getString(R.string.dialog_ok), { dialog, which ->
            presenter.savePassword(password.text.toString())
            activity.recreate() //recreating to force the secure screen which requires a restart
        })

        builder.setNegativeButton(getString(R.string.dialog_cancel), { dialog, which -> dialog.cancel() })

        showDialog(builder.create(), true)
        dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = false
    }

    override fun showAddNewAssetDialog() {
        val input = View.inflate(activity, R.layout.add_asset_dialog, null)
        val quantity = input.findViewById<EditText>(R.id.crypto_quantity)
        val price = input.findViewById<EditText>(R.id.crypto_price)

        val spinnerExchanges = input.findViewById<Spinner>(R.id.spinner_exchange)
        val spinnerCryptos = input.findViewById<Spinner>(R.id.spinner_crypto)

        val exchangeList = resources.getStringArray(R.array.exchanges)
        var cryptoList: List<String> = listOf()

        spinnerExchanges.adapter = ArrayAdapter(activity, R.layout.spinner_item, exchangeList)
        spinnerExchanges.setSelection(presenter.lastUsedExchange(exchangeList))

        spinnerExchanges.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                cryptoList = presenter.getTickersForExchange(exchangeList[position])
                spinnerCryptos.adapter = ArrayAdapter(activity, R.layout.spinner_item, cryptoList)
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
        if (raiseKeyboard) {
            dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        }
        dialog.show()
    }

    override fun updateUi(position: Int) {
        netWorth.text = presenter.getNetWorthDisplayString()
        recyclerView.adapter.notifyItemChanged(position)
    }

    override fun removeItem(position: Int) {
        netWorth.text = presenter.getNetWorthDisplayString()
        recyclerView.adapter.notifyItemRemoved(position)
    }

    override fun resetUi() {
        netWorth.text = getString(R.string.zero_dollar)
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
