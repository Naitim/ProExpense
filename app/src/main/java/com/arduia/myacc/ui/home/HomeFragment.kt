package com.arduia.myacc.ui.home

import android.graphics.Color
import android.os.Bundle
import android.util.Log.d
import android.view.*
import android.view.animation.BounceInterpolator
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arduia.core.extension.px
import com.arduia.graph.SpendPoint
import com.arduia.myacc.R
import com.arduia.myacc.databinding.FragHomeBinding
import com.arduia.myacc.ui.BaseFragment
import com.arduia.myacc.ui.common.MarginItemDecoration
import kotlinx.android.synthetic.main.frag_home.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.random.nextInt


class HomeFragment : BaseFragment(){

    private val viewBinding by lazy {  createViewBinding() }

    private val viewModel by viewModels<HomeViewModel>()

    private val entryNavOption by lazy {
        createDropUpNagOption()
    }

    private val moreRecentNavOption by lazy {
        createMoreRecentNavOption()
    }

    private val recentAdapter by lazy {
        RecentListAdapter( layoutInflater )
    }

    private val linearLayoutManager by lazy {
        LinearLayoutManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = viewBinding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycle.addObserver(viewModel)
        setupView()
        setupViewModel()
    }

    //Setup View
    private fun setupView(){

        viewBinding.fbAdd.setColorFilter(Color.WHITE)

        viewBinding.fbAdd.setOnClickListener {
            findNavController().navigate(R.id.dest_expense_entry, null, entryNavOption )
        }

        viewBinding.btnMenuOpen.setOnClickListener { openDrawer() }

        viewBinding.btnMoreTransaction.setOnClickListener {
            findNavController().navigate(R.id.dest_transaction, null, moreRecentNavOption)
        }

        viewBinding.imgGraph.spendPoints = getSamplePoints()

        recentAdapter.setItemInsertionListener {
            //Item inserted
            viewBinding.rvRecent.smoothScrollToPosition(0)
        }
    }

    private fun setupViewModel(){
        viewModel.recentData.observe(viewLifecycleOwner, Observer {
            recentAdapter.submitList(it)
        })
    }


    private fun getSamplePoints() =
        mutableListOf<SpendPoint>().apply {
        add(SpendPoint(1, randomRate()))
        add(SpendPoint(2, randomRate()))
        add(SpendPoint(3, randomRate()))
        add(SpendPoint(4, randomRate()))
//        add(SpendPoint(5, randomRate()))
//        add(SpendPoint(6, randomRate()))
//        add(SpendPoint(7, randomRate()))
    }

    private fun randomRate() = (Random.nextInt(0..100).toFloat() / 100)

    private fun createViewBinding() =
        FragHomeBinding.inflate(layoutInflater).apply {
            //Once Configuration
        rvRecent.adapter = recentAdapter
        rvRecent.layoutManager = linearLayoutManager
        rvRecent.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen.spacing_list_item).toInt(),
                resources.getDimension(R.dimen.margin_list_item).toInt()
            ))
    }

    private fun createDropUpNagOption() =
        NavOptions.Builder()
                //For Entry Fragment
            .setEnterAnim(R.anim.pop_down_up)
            .setPopExitAnim(R.anim.pop_up_down)
                //For Home Fragment
            .setExitAnim(android.R.anim.fade_out)
            .setPopEnterAnim(R.anim.nav_default_enter_anim)

            .build()

    private fun createMoreRecentNavOption() =
        NavOptions.Builder()
                //For Transaction Fragment
            .setEnterAnim(R.anim.expense_enter_left)
            .setPopExitAnim(R.anim.expense_exit_right)
                //For Home Fragment
            .setExitAnim(R.anim.nav_default_exit_anim)
            .setPopEnterAnim(R.anim.nav_default_enter_anim)
            .setLaunchSingleTop(true)
            .build()

    companion object{
        private const val TAG = "MY_HomeFragment"
    }

}
