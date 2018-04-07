package com.nebulights.coinstacks.Portfolio.Main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.jakewharton.rxbinding2.widget.RxTextView
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialOverlayLayout
import com.leinardi.android.speeddial.SpeedDialView
import com.nebulights.coinstacks.Extensions.dp
import com.nebulights.coinstacks.Extensions.notNull
import com.nebulights.coinstacks.R
import com.nebulights.coinstacks.Types.RecordTypes
import io.reactivex.Observable
import io.reactivex.functions.BiFunction


@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class PortfolioFragment : Fragment(), PortfolioContract.View {

    @BindView(R.id.net_worth_amount)
    lateinit var netWorth: TextView
    @BindView(R.id.net_worth_amount_layout)
    lateinit var netWorthLayout: LinearLayout
    @BindView(R.id.recycler_view)
    lateinit var recyclerView: RecyclerView
    @BindView(R.id.speedDial)
    lateinit var floatingActionbutton: SpeedDialView
    @BindView(R.id.overlay)
    lateinit var speedDialOverlay: SpeedDialOverlayLayout

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_crypto_list, container, false)
        ButterKnife.bind(this, rootView)

        linearLayoutManager = LinearLayoutManager(activity)

        recyclerView.layoutManager = linearLayoutManager
        //recyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        recyclerView.addItemDecoration(BottomOffsetDecoration(50.dp))
        recyclerView.adapter = PortfolioRecyclerAdapter(presenter)

        netWorth.text = presenter.getNetWorthDisplayString()

        floatingActionbutton.speedDialOverlayLayout = speedDialOverlay;

        floatingActionbutton.setMainFabOnClickListener {
            if (floatingActionbutton.isFabMenuOpen) {
                floatingActionbutton.closeOptionsMenu()
            }
        }

        floatingActionbutton.addFabOptionItem(
                SpeedDialActionItem.Builder(R.id.fab_add_coins, R.drawable.ic_attach_money_white_24dp)
                        .setLabel(getString(R.string.manual_entry_fab)).create())

        floatingActionbutton.addFabOptionItem(
                SpeedDialActionItem.Builder(R.id.fab_add_exchange, R.drawable.ic_vpn_key_white_24dp)
                        .setLabel(getString(R.string.exchange_apis_fab)).create())

        floatingActionbutton.addFabOptionItem(
                SpeedDialActionItem.Builder(R.id.fab_add_watch, R.drawable.ic_remove_red_eye_white_24dp)
                        .setLabel(getString(R.string.watch_address_fab)).create())

        floatingActionbutton.setOptionFabSelectedListener({ speedDialActionItem ->

            when (speedDialActionItem.id) {
                R.id.fab_add_coins -> presenter.addNew(RecordTypes.COINS)
                R.id.fab_add_exchange -> presenter.addNew(RecordTypes.API)
                R.id.fab_add_watch -> presenter.addNew(RecordTypes.WATCH)
                else -> {
                }
            }
            floatingActionbutton.closeOptionsMenu()
        })


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
        menu.findItem(R.id.unlocked_data).isVisible = isVisible

        recyclerView.adapter.notifyDataSetChanged()

        netWorth.text = presenter.getNetWorthDisplayString()
        netWorthLayout.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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
        if (context != null) {
            val builder = AlertDialog.Builder(context!!)
            builder.setTitle(getString(R.string.remove_assets_title))
            builder.setMessage(getString(R.string.remove_all_assets_message))
            builder.setPositiveButton(getString(R.string.dialog_ok), { dialog, which ->
                presenter.clearAssets()
            })

            builder.setNegativeButton(getString(R.string.dialog_cancel), { dialog, which -> dialog.cancel() })

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

        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(getString(R.string.add_password_dialog_title))
        builder.setView(input)
        builder.setPositiveButton(getString(R.string.dialog_ok), { dialog, which ->
            presenter.savePassword(password.text.toString())
            activity!!.recreate() //recreating to force the secure screen which requires a restart
        })

        builder.setNegativeButton(getString(R.string.dialog_cancel), { dialog, which -> dialog.cancel() })

        showDialog(builder.create(), true)
        dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = false
    }

    private fun showErrorDialogCouldNotFindCrypto() {
        val builder = AlertDialog.Builder(context!!)
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
        if (this::netWorth.isInitialized) {
            netWorth.text = presenter.getNetWorthDisplayString()
            //recyclerView.adapter.notifyItemChanged(position)
            recyclerView.adapter.notifyDataSetChanged()
        }
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
