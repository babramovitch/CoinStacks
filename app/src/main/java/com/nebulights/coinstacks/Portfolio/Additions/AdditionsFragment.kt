package com.nebulights.coinstacks.Portfolio.Additions

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import com.nebulights.coinstacks.Network.exchanges.Models.BasicAuthentication
import com.nebulights.coinstacks.R
import com.nebulights.coinstacks.Types.CryptoPairs
import com.nebulights.coinstacks.Types.CryptoTypes
import com.nebulights.coinstacks.Types.RecordTypes


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
    lateinit var saveButton: Button

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

    private val spinnerList: MutableList<Spinner> = mutableListOf()
    private var isInitialSpinner = true
    private lateinit var menu: Menu
    var editing = false

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
        editing = arguments?.getBoolean("editing", false) ?: false

        val exchangeList = resources.getStringArray(R.array.exchanges)
        spinnerExchanges.adapter = ArrayAdapter(activity, R.layout.spinner_item, exchangeList)

        presenter.setInitialScreenAndMode(recordType, exchange, ticker, editing, exchangeList)

        spinnerExchanges.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (!isInitialSpinner) {
                    presenter.updateExchangeSpinnerSelection(exchangeList[position])
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
                }
            }


        }

        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        this.menu = menu
        inflater.inflate(R.menu.additions_menu, menu)

        menu.findItem(R.id.delete_item).isVisible = editing

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_item -> {
                if (spinnerCryptos.selectedItem == null) {
                    presenter.deleteRecord(spinnerExchanges.selectedItem.toString(), "")
                } else {
                    presenter.deleteRecord(spinnerExchanges.selectedItem.toString(), spinnerCryptos.selectedItem.toString())
                }
            }
            R.id.save_record -> {
                val exchangeList = resources.getStringArray(R.array.exchanges)
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
                    }
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
        spinner.setOnTouchListener(View.OnTouchListener { view, motionEvent -> true })
    }

    override fun setEditModeWatch() {
    }

    override fun showCoinAddition() {
        coinLayout.visibility = View.VISIBLE
        spinnerCrytpoLayout.visibility = View.VISIBLE
        apiLayout.visibility = View.GONE
        watchLayout.visibility = View.GONE
        saveButton.text = "Save Coins"
    }

    override fun showAPIAddition() {
        coinLayout.visibility = View.GONE
        spinnerCrytpoLayout.visibility = View.GONE
        apiLayout.visibility = View.VISIBLE
        watchLayout.visibility = View.GONE
        saveButton.text = "Save API Keys"

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
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
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


    override fun onDestroy() {
        presenter.onDetach()
        super.onDestroy()
    }
}
