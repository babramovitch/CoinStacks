package com.nebulights.crytpotracker

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CryptoListFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CryptoListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CryptoListFragment : Fragment(), PortfolioContract.View {

    private var mParam1: String? = null
    private var mParam2: String? = null

    private var presenter: PortfolioContract.Presenter? = null

    private var button: Button? = null

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

        button = rootView.findViewById<View>(R.id.button) as Button

        button!!.setOnClickListener { presenter!!.dosomething() }

        return rootView
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun updateUi() {
        Log.i(TAG, "I DID SOMETHING IN FRAGMENT")
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
