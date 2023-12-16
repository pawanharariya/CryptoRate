package com.psh.project.cryptorate.ui.currency

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.psh.project.cryptorate.R
import com.psh.project.cryptorate.databinding.FragmentCurrencyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CurrencyFragment : Fragment() {

    private var _binding: FragmentCurrencyBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CurrencyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_currency, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshLiveRates()
            binding.swipeRefreshLayout.isRefreshing = false
        }
        val currencyRecyclerView = binding.currencyRecyclerView
        currencyRecyclerView.setHasFixedSize(true)
        val currencyAdapter = CurrencyAdapter()
        currencyRecyclerView.adapter = currencyAdapter
        viewModel.cryptoList.observe(viewLifecycleOwner) {
            it?.let {
                currencyAdapter.submitList(it)
            }
        }

        viewModel.liveRateMap.observe(viewLifecycleOwner) {
            Log.e("API", "live rate observed")
            it?.let {
                currencyAdapter.updateLiveRating(it)
            }
        }
    }

    private fun refreshLiveRates() {
        viewModel.forceRefreshLiveRates()
    }

    // live rates will only be fetched when fragment is visible
    override fun onStart() {
        super.onStart()
        Log.e("API","inside onstart start fetching")
        viewModel.startLiveRateFetching()
    }

    // live rates will not fetched when fragment is not visible
    override fun onStop() {
        super.onStop()
        viewModel.stopLiveRateFetching()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}