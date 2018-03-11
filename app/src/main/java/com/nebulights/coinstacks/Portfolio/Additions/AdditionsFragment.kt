package com.nebulights.coinstacks.Portfolio.Additions

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import android.view.*
import com.nebulights.coinstacks.R
import com.nebulights.coinstacks.Extensions.notNull
import android.widget.*
import android.view.LayoutInflater
import com.nebulights.coinstacks.CryptoPairs
import com.nebulights.coinstacks.CryptoTypes
import com.nebulights.coinstacks.Network.exchanges.BasicAuthentication


class AdditionsFragment : Fragment(), AdditionsContract.View, TabLayout.OnTabSelectedListener {

    private lateinit var presenter: AdditionsContract.Presenter

    @BindView(R.id.spinner_crypto_layout) lateinit var spinnerCrytpoLayout: LinearLayout
    @BindView(R.id.coins_layout) lateinit var coinLayout: LinearLayout
    @BindView(R.id.api_layout) lateinit var apiLayout: LinearLayout
    @BindView(R.id.watch_layout) lateinit var watchLayout: LinearLayout
    @BindView(R.id.spinner_exchange_header_text) lateinit var spinnerHeader: TextView
    @BindView(R.id.spinner_crypto) lateinit var spinnerCryptos: Spinner

    @BindView(R.id.tab_layout) lateinit var tabLayout: TabLayout

    @BindView(R.id.spinner_exchange) lateinit var spinnerExchanges: Spinner

    @BindView(R.id.save_button) lateinit var saveButton: Button

    @BindView(R.id.crypto_price) lateinit var price: EditText
    @BindView(R.id.crypto_quantity) lateinit var quantity: EditText

    @BindView(R.id.api_secret_layout) lateinit var apiSecretLayout: LinearLayout
    @BindView(R.id.api_secret_text) lateinit var apiSecret: EditText

    @BindView(R.id.api_key_layout) lateinit var apiKeyLayout: LinearLayout
    @BindView(R.id.api_key_text) lateinit var apiKey: EditText

    @BindView(R.id.api_username_layout) lateinit var apiUserNameLayout: LinearLayout
    @BindView(R.id.api_username_text) lateinit var userName: EditText

    @BindView(R.id.api_password_layout) lateinit var apiPasswordLayout: LinearLayout
    @BindView(R.id.api_password_text) lateinit var apiPassword: EditText

    @BindView(R.id.api_pair_layout) lateinit var apiPairLayout: LinearLayout

    private val spinnerList: MutableList<Spinner> = mutableListOf()

    companion object {
        fun newInstance(): AdditionsFragment {
            return AdditionsFragment()
        }
    }

    override fun setPresenter(presenter: AdditionsContract.Presenter) {
        this.presenter = presenter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater!!.inflate(R.layout.fragment_additions, container, false)
        ButterKnife.bind(this, rootView)

        showCoinAddition()

        tabLayout.addOnTabSelectedListener(this)

        val cryptoList: List<String> = listOf()

        val exchangeList = resources.getStringArray(R.array.exchanges)

        spinnerExchanges.adapter = ArrayAdapter(activity, R.layout.spinner_item, exchangeList)
        spinnerExchanges.setSelection(presenter.lastUsedExchange(exchangeList))

        spinnerExchanges.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                presenter.updateExchangeSpinnerSelection(exchangeList[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        saveButton.setOnClickListener {
            when (tabLayout.selectedTabPosition) {
                0 -> presenter.createAsset(exchangeList[spinnerExchanges.selectedItemPosition],
                        cryptoList[spinnerCryptos.selectedItemPosition],
                        quantity.text.toString(),
                        price.text.toString())

                1 -> {

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
            }


        }

        return rootView
    }


    override fun onResume() {
        super.onResume()
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }


    override fun onTabSelected(tab: TabLayout.Tab?) {
        tab.notNull {
            presenter.showCorrectCoinTypeDetails(tab!!.position)
        }
    }


    override fun showCoinAddition() {
        coinLayout.visibility = View.VISIBLE
        spinnerCrytpoLayout.visibility = View.VISIBLE
        apiLayout.visibility = View.GONE
        watchLayout.visibility = View.GONE
        saveButton.text = "Add Coins"
    }

    override fun showAPIAddition() {
        coinLayout.visibility = View.GONE
        spinnerCrytpoLayout.visibility = View.GONE
        apiLayout.visibility = View.VISIBLE
        watchLayout.visibility = View.GONE
        saveButton.text = "Add API Keys"

    }

    override fun showAuthenticationRequirements(userName: Boolean, password: Boolean) {
        apiUserNameLayout.visibility = if (userName) View.VISIBLE else View.GONE
        apiPasswordLayout.visibility = if (password) View.VISIBLE else View.GONE
    }

    override fun showWatchAddition() {
        coinLayout.visibility = View.GONE
        apiLayout.visibility = View.GONE
        spinnerCrytpoLayout.visibility = View.VISIBLE
        watchLayout.visibility = View.VISIBLE
        saveButton.text = "Add Watch Address"
    }

    override fun setupCryptoPairSpinner(cryptoList: List<String>) {
        spinnerCryptos.adapter = ArrayAdapter(activity, R.layout.spinner_item, cryptoList)
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


    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        presenter.onDetach()
        super.onDestroy()
    }
}
