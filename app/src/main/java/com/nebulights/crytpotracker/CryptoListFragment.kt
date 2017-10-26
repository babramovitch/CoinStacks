package com.nebulights.crytpotracker

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.nebulights.crytpotracker.Quadriga.CurrentTradingInfo


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CryptoListFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CryptoListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CryptoListFragment : Fragment(), PortfolioContract.View {

    @BindView(R.id.btc_cad) lateinit var btcCad: TextView
    @BindView(R.id.bch_cad) lateinit var bchCad: TextView
    @BindView(R.id.eth_cad) lateinit var ethCad: TextView
    @BindView(R.id.net_worth) lateinit var netWorth: TextView

    @BindView(R.id.button_start) lateinit var startButton: TextView
    @BindView(R.id.button_stop) lateinit var stopButton: TextView

    @BindView(R.id.recycler_view) lateinit var recyclerView: RecyclerView

    private var mParam1: String? = null
    private var mParam2: String? = null

    private var presenter: PortfolioContract.Presenter? = null

    private lateinit var linearLayoutManager: LinearLayoutManager


    override fun setPresenter(presenter: PortfolioContract.Presenter) {
        this.presenter = presenter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments.getString(ARG_PARAM1)
            mParam2 = arguments.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater!!.inflate(R.layout.fragment_crypto_list, container, false)
        ButterKnife.bind(this, rootView)

        linearLayoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = linearLayoutManager

        recyclerView.adapter = CryptoAdapter(presenter!!.getCurrentTradingData())

        startButton.setOnClickListener { presenter.notNull { presenter!!.startFeed() } }
        stopButton.setOnClickListener { presenter.notNull { presenter!!.stopFeed() } }

        return rootView
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun updateUi(ticker: String, result: CurrentTradingInfo) {
        when (ticker) {
            "BTC_CAD" -> btcCad.text = "BTC: " + result.last
            "BCH_CAD" -> bchCad.text = "BCH: " + result.last
            "ETH_CAD" -> ethCad.text = "ETH: " + result.last
        }

        netWorth.text = "Net Worth: " + presenter!!.getNetWorth()

        recyclerView.adapter = CryptoAdapter(presenter!!.getCurrentTradingData())

    }

    override fun onResume() {
        presenter.notNull { presenter!!.startFeed() }
        super.onResume()
    }

    override fun onPause() {
        presenter.notNull { presenter!!.stopFeed() }
        super.onPause()
    }


    companion object {
        private val TAG = "CrytpoListFragment"

        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        fun newInstance(param1: String, param2: String): CryptoListFragment {
            val fragment = CryptoListFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

}
