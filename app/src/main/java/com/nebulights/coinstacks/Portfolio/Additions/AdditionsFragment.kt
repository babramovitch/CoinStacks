package com.nebulights.coinstacks.Portfolio.Additions

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton
import butterknife.BindView
import butterknife.ButterKnife
import com.jakewharton.rxbinding2.widget.RxTextView
import com.nebulights.coinstacks.Network.exchanges.Models.BasicAuthentication
import com.nebulights.coinstacks.R
import com.nebulights.coinstacks.Types.CryptoPairs
import com.nebulights.coinstacks.Types.CryptoTypes
import com.nebulights.coinstacks.Types.RecordTypes
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import com.nebulights.coinstacks.Network.BlockExplorers.Model.WatchAddress
import io.reactivex.disposables.CompositeDisposable


class AdditionsFragment : Fragment(), AdditionsContract.View {

    private lateinit var presenter: AdditionsContract.Presenter

    @BindView(R.id.spinner_crypto_layout)
    lateinit var spinnerCrytpoLayout: LinearLayout
    @BindView(R.id.coins_layout)
    lateinit var coinLayout: LinearLayout
    @BindView(R.id.api_layout)
    lateinit var apiLayout: LinearLayout
    @BindView(R.id.watch_layout)
    lateinit var watchLayout: LinearLayout
    @BindView(R.id.spinner_exchange_header_text)
    lateinit var spinnerHeader: TextView
    @BindView(R.id.spinner_crypto)
    lateinit var spinnerCryptos: Spinner

    @BindView(R.id.spinner_exchange)
    lateinit var spinnerExchanges: Spinner

    @BindView(R.id.save_button)
    lateinit var saveButton: CircularProgressButton

    @BindView(R.id.crypto_price)
    lateinit var price: EditText
    @BindView(R.id.crypto_quantity)
    lateinit var quantity: EditText

    @BindView(R.id.api_secret_layout)
    lateinit var apiSecretLayout: LinearLayout
    @BindView(R.id.api_secret_text)
    lateinit var apiSecret: EditText

    @BindView(R.id.api_key_layout)
    lateinit var apiKeyLayout: LinearLayout
    @BindView(R.id.api_key_text)
    lateinit var apiKey: EditText

    @BindView(R.id.api_username_layout)
    lateinit var apiUserNameLayout: LinearLayout
    @BindView(R.id.api_username_text)
    lateinit var userName: EditText

    @BindView(R.id.api_password_layout)
    lateinit var apiPasswordLayout: LinearLayout
    @BindView(R.id.api_password_text)
    lateinit var apiPassword: EditText

    @BindView(R.id.api_pair_layout)
    lateinit var apiPairLayout: LinearLayout

    @BindView(R.id.watch_text)
    lateinit var watchText: EditText

    @BindView(R.id.watch_nickname_text)
    lateinit var watchNickNameText: EditText

    private var dialog: AlertDialog? = null
    private val spinnerList: MutableList<Spinner> = mutableListOf()
    private var isInitialSpinner = true

    var editing = false
    private var disposable: CompositeDisposable = CompositeDisposable()

    companion object {
        fun newInstance(extras: Bundle?): AdditionsFragment {
            val fragment = AdditionsFragment()
            fragment.arguments = extras ?: Bundle()
            return fragment
        }
    }

    override fun setPresenter(presenter: AdditionsContract.Presenter) {
        this.presenter = presenter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_additions, container, false)
        ButterKnife.bind(this, rootView)

        val recordType = arguments?.getString("type", "") ?: ""
        val exchange = arguments?.getString("exchange", "") ?: ""
        val ticker = arguments?.getString("ticker", "") ?: ""
        val address = arguments?.getString("address", "") ?: ""
        editing = arguments?.getBoolean("editing", false) ?: false

        val exchangeList = resources.getStringArray(R.array.exchanges)
        spinnerExchanges.adapter = ArrayAdapter(activity, R.layout.spinner_item, exchangeList)

        val quantityObservable = RxTextView.textChanges(quantity)
        val watchAddressObservable = RxTextView.textChanges(watchText)
        val watchAddressNickName = RxTextView.textChanges(watchNickNameText)
        val userNameObservable = RxTextView.textChanges(userName)
        val apiKeyObservable = RxTextView.textChanges(apiKey)
        val apiSecretObservable = RxTextView.textChanges(apiSecret)
        val apiPasswordObservable = RxTextView.textChanges(apiPassword)


        presenter.setInitialScreenAndMode(recordType, exchange, ticker, address, editing, exchangeList,
                AdditionsFormValidator(
                        quantityObservable, watchAddressObservable, watchAddressNickName, userNameObservable, apiKeyObservable, apiSecretObservable, apiPasswordObservable)
        )

        spinnerExchanges.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (!isInitialSpinner) {
                    presenter.updateViewsForExchangeSpinnerSelection(exchangeList[position])
                    apiPassword.setText("j2wosi9g7x7")
                    apiKey.setText("e9582aba5f3d49c2ebdb0ee9a0200c78")
                    apiSecret.setText("eSsSokvRjfQhsqYrCRLJdURGHyrbcaQl4eNcWxOf+scz5yK7/4D/oYO/+bjEfKwl2UV6HwUu9GildYvknycVrA==")
                    userName.setText("")
                } else {
                    isInitialSpinner = false
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        saveButton.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.seperatorGray))

        saveButton.setOnClickListener {
            when (presenter.getRecordType()) {

                RecordTypes.COINS -> presenter.createAsset(exchangeList[spinnerExchanges.selectedItemPosition],
                        spinnerCryptos.selectedItemPosition,
                        quantity.text.toString(),
                        price.text.toString())

                RecordTypes.API -> {

                    val cryptoPairs: MutableList<CryptoPairs> = mutableListOf()

                    spinnerList.forEach {
                        cryptoPairs.addAll(presenter.getTickerForExchangeAndPair(
                                exchangeList[spinnerExchanges.selectedItemPosition],
                                it.selectedItem.toString()))
                    }

                    presenter.createAPIKey(exchangeList[spinnerExchanges.selectedItemPosition],
                            userName.text.toString(),
                            apiPassword.text.toString(),
                            apiKey.text.toString(),
                            apiSecret.text.toString(),
                            cryptoPairs)

                }

                RecordTypes.WATCH -> {
                    presenter.createWatchAddress(exchangeList[spinnerExchanges.selectedItemPosition], spinnerCryptos.selectedItemPosition, watchText.text.toString(), watchNickNameText.text.toString())
                }
            }
        }

        return rootView
    }

    override fun enableSaveButton(enabled: Boolean) {
        val enabledColor = ContextCompat.getColor(activity!!, R.color.colorPrimary)
        val disabledCOlor = ContextCompat.getColor(activity!!, R.color.seperatorGray)
        if (enabled && !saveButton.isEnabled) {
            animateButtonEnabledStateChange(saveButton, enabledColor, disabledCOlor)
        } else if (!enabled && saveButton.isEnabled) {
            animateButtonEnabledStateChange(saveButton, disabledCOlor, enabledColor)
        }

        saveButton.isEnabled = enabled
    }

    private fun animateButtonEnabledStateChange(saveButton: Button, toColor: Int, fromColor: Int) {
        ObjectAnimator.ofObject(saveButton, "backgroundColor", ArgbEvaluator(), fromColor, toColor)
                .setDuration(375)
                .start()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.additions_menu, menu)
        menu.findItem(R.id.delete_item).isVisible = editing
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.delete_item -> {
                if (spinnerCryptos.selectedItem == null) {
                    presenter.deleteRecord(spinnerExchanges.selectedItem.toString(), "", "")
                } else {
                    presenter.deleteRecord(spinnerExchanges.selectedItem.toString(), spinnerCryptos.selectedItem.toString(), watchText.text.toString())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setExchange(position: Int) {
        spinnerExchanges.setSelection(position)
    }

    override fun setCryptoPair(cryptoPairIndex: Int) {
        spinnerCryptos.setSelection(cryptoPairIndex)
    }

    override fun setCryptoQuantity(amount: String) {
        quantity.setText(amount)
        quantity.requestFocus()
    }

    override fun setEditModeCoinsAndApi() {
        saveButton.text = "Save Changes"
        disableSpinner(spinnerExchanges)
        disableSpinner(spinnerCryptos)
    }

    private fun disableSpinner(spinner: Spinner) {
        spinner.background = null
        spinner.setOnTouchListener({ view, motionEvent -> true })
    }

    override fun setEditModeWatch(watchAddress: WatchAddress) {
        watchText.setText(watchAddress.address)
        watchNickNameText.setText(watchAddress.nickName)
    }

    override fun showCoinAddition() {
        coinLayout.visibility = View.VISIBLE
        spinnerCrytpoLayout.visibility = View.VISIBLE
        apiLayout.visibility = View.GONE
        watchLayout.visibility = View.GONE
        saveButton.text = "Save Coins"
    }

    override fun showAPIAddition() {
        //TODO play around with this configuration
        if (resources.displayMetrics.density <= 2) {
            activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        }
        coinLayout.visibility = View.GONE
        spinnerCrytpoLayout.visibility = View.GONE
        apiLayout.visibility = View.VISIBLE
        watchLayout.visibility = View.GONE
        saveButton.text = "Verify & Save API Keys"
    }

    override fun showAuthenticationRequirements(userName: Boolean, password: Boolean) {
        apiUserNameLayout.visibility = if (userName) View.VISIBLE else View.GONE
        apiPasswordLayout.visibility = if (password) {
            apiSecret.nextFocusDownId = R.id.api_password_text
            View.VISIBLE
        } else {
            apiSecret.nextFocusDownId = EditorInfo.IME_ACTION_DONE
            View.GONE
        }
    }

    override fun showWatchAddition() {
        coinLayout.visibility = View.GONE
        apiLayout.visibility = View.GONE
        spinnerCrytpoLayout.visibility = View.VISIBLE
        watchLayout.visibility = View.VISIBLE
        saveButton.text = "Save Watch Address"
    }

    override fun setupCryptoPairSpinner(cryptoList: List<String>) {
        spinnerCryptos.adapter = ArrayAdapter(activity, R.layout.spinner_item, cryptoList)
        spinnerCryptos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                presenter.setCryptoQuantity(spinnerExchanges.selectedItem.toString(), spinnerCryptos.selectedItem.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    /**
     * Dynamically generate a list of layouts with a header/spinner based on the submitted list of trading pairs
     */
    override fun setupApiSpinners(basicAuthentication: BasicAuthentication, cryptosForExchange: List<CryptoTypes>, cryptoList: List<String>) {

        apiPairLayout.removeAllViews()
        spinnerList.clear()

        val layoutInflater = LayoutInflater.from(context)

        for (index in cryptosForExchange.indices) {

            val layout = layoutInflater.inflate(R.layout.fragment_addition_api_cryptos, null, false)

            val spinnerHeaderText = layout.findViewById<TextView>(R.id.spinner_crypto_text)
            spinnerHeaderText.text = cryptosForExchange[index].name

            val spinner = layout.findViewById<Spinner>(R.id.spinner_crypto)
            val spinnerPairs = cryptoList.filter { it.contains(cryptosForExchange[index].name) }

            spinner.adapter = ArrayAdapter(activity, R.layout.spinner_item, spinnerPairs)

            val userTickers = basicAuthentication.getUserTickers()

            spinnerPairs.indices
                    .filter { userTickers.contains(spinnerPairs[it]) }
                    .forEach { spinner.setSelection(it) }

            apiPairLayout.addView(layout)
            spinnerList.add(spinner)
        }
    }

    override fun setAuthenticationDetails(basicAuthentication: BasicAuthentication) {
        userName.setText(basicAuthentication.userName)
        apiKey.setText(basicAuthentication.apiKey)
        apiSecret.setText(basicAuthentication.apiSecret)
        apiPassword.setText(basicAuthentication.password)
    }

    override fun showVerificationDialog() {
        saveButton.startAnimation()
    }

    override fun closeVerificationDialog() {
        val fillColor = ContextCompat.getColor(activity!!, R.color.colorAccent)
        val drawable = BitmapFactory.decodeResource(resources, R.drawable.ic_check_white_24dp)
        saveButton.doneLoadingAnimation(fillColor, drawable)
    }

    override fun showValidationErrorDialog(message: String?) {
        saveButton.revertAnimation()
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("Error")
        builder.setMessage(message ?: "Unknown Error")
        builder.setPositiveButton(getString(R.string.dialog_ok), { dialog, which ->
        }).create().show()
    }

    override fun onDestroy() {
        disposable.dispose()
        dialog?.dismiss()
        presenter.onDetach()
        saveButton.dispose()
        super.onDestroy()
    }
}
