package com.nebulights.coinstacks.Portfolio.Main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.jakewharton.rxbinding2.widget.RxTextView
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialOverlayLayout
import com.leinardi.android.speeddial.SpeedDialView
import com.nebulights.coinstacks.Extensions.dp
import com.nebulights.coinstacks.R
import com.nebulights.coinstacks.Types.RecordTypes
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class PortfolioFragment : Fragment(), PortfolioContract.View {

    @BindView(R.id.net_worth_amount)
    lateinit var netWorth: TextView
    @BindView(R.id.net_worth_amount_layout)
    lateinit var netWorthLayout: CardView
    @BindView(R.id.recycler_view)
    lateinit var recyclerView: RecyclerView
    @BindView(R.id.speedDial)
    lateinit var floatingActionbutton: SpeedDialView
    @BindView(R.id.overlay)
    lateinit var speedDialOverlay: SpeedDialOverlayLayout
    @BindView(R.id.emptyRecyclerViewImage)
    lateinit var emptyRecyclerViewImage: ImageView

    private lateinit var presenter: PortfolioContract.Presenter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var menu: Menu? = null

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_crypto_list, container, false)
        ButterKnife.bind(this, rootView)

        setupRecyclerView()
        setupSpeeDialFab()

        netWorth.text = presenter.getNetWorthDisplayString()

        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        this.menu = menu
        inflater.inflate(R.menu.menu, menu)
        presenter.setAssetLockedState()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onStart() {
        presenter.startFeed()
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    fun setupRecyclerView() {
        linearLayoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = linearLayoutManager
        //recyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        //recyclerView.addItemDecoration(HeaderItemDecoration(recyclerView, this))

        recyclerView.addItemDecoration(BottomOffsetDecoration(60.dp))
        recyclerView.adapter = PortfolioRecyclerAdapter(presenter)
    }

    fun setupSpeeDialFab() {
        floatingActionbutton.overlayLayout = speedDialOverlay

        floatingActionbutton.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_add_coins, R.drawable.ic_attach_money_white_24dp)
                .setLabel(getString(R.string.manual_entry_fab)).create()
        )

        floatingActionbutton.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_add_exchange, R.drawable.ic_vpn_key_white_24dp)
                .setLabel(getString(R.string.exchange_apis_fab)).create()
        )

        floatingActionbutton.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_add_watch, R.drawable.ic_remove_red_eye_white_24dp)
                .setLabel(getString(R.string.watch_address_fab)).create()
        )

        floatingActionbutton.setOnActionSelectedListener({ speedDialActionItem ->
            when (speedDialActionItem.id) {
                R.id.fab_add_coins -> presenter.addNew(RecordTypes.COINS)
                R.id.fab_add_exchange -> presenter.addNew(RecordTypes.API)
                R.id.fab_add_watch -> presenter.addNew(RecordTypes.WATCH)
                else -> { /*do nothing*/
                }
            }
            false
        })

        emptyRecyclerViewImage.setOnClickListener {
            floatingActionbutton.open(true)
        }
    }


    override fun showAssetQuantites(isVisible: Boolean) {
        menu?.findItem(R.id.locked_data)?.isVisible = !isVisible
        menu?.findItem(R.id.unlocked_data)?.isVisible = isVisible

        recyclerView.adapter.notifyDataSetChanged()

        netWorth.text = presenter.getNetWorthDisplayString()
        netWorthLayout.visibility = if (isVisible) View.VISIBLE else View.GONE
        floatingActionbutton.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.locked_data -> {
                presenter.unlockDataPressed()
            }

            R.id.unlocked_data -> {
                presenter.lockDataPressed()
            }
            R.id.warning -> {
                presenter.warningPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showWarningdialog() {
        if(context != null) {
            val builder = AlertDialog.Builder(context!!)
            builder.setTitle(getString(R.string.dialog_stale_data_title))
            builder.setMessage(getString(R.string.dialog_stale_data_message))
            builder.setPositiveButton(getString(R.string.dialog_ok), { dialog, which ->
               dialog.dismiss()
            })

            showDialog(builder.create(), true)
        }
    }

    override fun showForgotPasswordlDialog() {
        if (context != null) {
            val builder = AlertDialog.Builder(context!!)
            builder.setTitle(getString(R.string.remove_assets_title))
            builder.setMessage(getString(R.string.remove_all_assets_message))
            builder.setPositiveButton(
                getString(R.string.dialog_confirm_delete_all_data),
                { dialog, which ->
                    presenter.clearAssets()
                })

            builder.setNegativeButton(
                getString(R.string.dialog_cancel),
                { dialog, which -> dialog.cancel() })

            showDialog(builder.create(), false)
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

        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(if (firstAttempt) getString(R.string.enter_password) else getString(R.string.invalid_password))
        builder.setView(input)
        builder.setPositiveButton(getString(R.string.dialog_ok), { dialog, which ->
            presenter.unlockDataPressed(password.text.toString())
        })

        builder.setNegativeButton(
            getString(R.string.dialog_cancel),
            { dialog, which -> dialog.cancel() })

        builder.setNeutralButton(getString(R.string.forgot_password), { dialog, which ->
            presenter.forgotPasswordPressed()
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

        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(getString(R.string.add_password_dialog_title))
        builder.setView(input)
        builder.setPositiveButton(getString(R.string.dialog_ok), { dialog, which ->
            presenter.savePassword(password.text.toString())
            activity!!.recreate() //recreating to force the secure screen which requires a restart
        })

        builder.setNegativeButton(
            getString(R.string.dialog_cancel),
            { dialog, which -> dialog.cancel() })

        showDialog(builder.create(), true)
        dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = false
    }

    private fun showDialog(dialog: AlertDialog, raiseKeyboard: Boolean) {
        this.dialog = dialog
        if (raiseKeyboard) {
            dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        }
        dialog.show()
    }

    override fun updateUi() {
        if (this::netWorth.isInitialized) {

            if(recyclerView.adapter.itemCount == 0){
                emptyRecyclerViewImage.visibility = View.VISIBLE
            }else{
                emptyRecyclerViewImage.visibility = View.GONE
            }

            netWorth.text = presenter.getNetWorthDisplayString()
            recyclerView.adapter.notifyDataSetChanged()
        }
    }

    override fun hasStaleData(hasStaleData: Boolean) {
        val color =
            if (hasStaleData) ContextCompat.getColor(activity!!, R.color.card_color_stale_data)
            else ContextCompat.getColor(netWorthLayout.context, R.color.card_color)
        netWorthLayout.setCardBackgroundColor(color)
        menu?.findItem(R.id.warning)?.isVisible = hasStaleData
    }

    override fun resetUi() {
        netWorth.text = getString(R.string.zero_dollar)
        recyclerView.adapter.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        presenter.stopFeed()
        super.onStop()
    }

    override fun onDestroy() {
        presenter.onDetach()
        dialog?.dismiss()
        super.onDestroy()
    }
}
