package com.nebulights.coinstacks.Portfolio.Additions

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
import info.hoang8f.android.segmented.SegmentedGroup
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import android.widget.Toast


class AdditionsFragment : Fragment(), AdditionsContract.View, RadioGroup.OnCheckedChangeListener {


    private lateinit var presenter: AdditionsContract.Presenter
    private lateinit var menu: Menu

    @BindView(R.id.addition_type) lateinit var additionType: SegmentedGroup
    @BindView(R.id.spinner_crypto_layout) lateinit var spinnerCrytpoLayout: LinearLayout
    @BindView(R.id.coins_layout) lateinit var coinLayout: LinearLayout
    @BindView(R.id.api_layout) lateinit var apiLayout: LinearLayout
    @BindView(R.id.watch_layout) lateinit var watchLayout: LinearLayout
    @BindView(R.id.spinner_exchange_header_text) lateinit var spinnerHeader: TextView

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

        additionType.check(R.id.button21);
        showCoinAddition()

        additionType.setOnCheckedChangeListener(this)

        return rootView
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCheckedChanged(radioGroup: RadioGroup, checkedId: Int) {

        var selection = 0

        when (checkedId) {
            R.id.button21 -> selection = 0
            R.id.button22 -> selection = 1
            R.id.button23 -> selection = 2
        }

        presenter.showCorrectCoinTypeDetails(selection)
    }

    override fun showCoinAddition(){
        coinLayout.visibility = View.VISIBLE
        spinnerCrytpoLayout.visibility = View.VISIBLE
        apiLayout.visibility = View.GONE
        watchLayout.visibility = View.GONE
        spinnerHeader.text = "Exchange"
    }

    override fun showAPIAddition(){
        coinLayout.visibility = View.GONE
        spinnerCrytpoLayout.visibility = View.GONE
        apiLayout.visibility = View.VISIBLE
        watchLayout.visibility = View.GONE
        spinnerHeader.text = "Exchange"
    }

    override fun showWatchAddition(){
        coinLayout.visibility = View.GONE
        apiLayout.visibility = View.GONE
        spinnerCrytpoLayout.visibility = View.VISIBLE
        watchLayout.visibility = View.VISIBLE
        spinnerHeader.text = "Exchange Price"
    }


    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        presenter.onDetach()
        super.onDestroy()
    }
}
