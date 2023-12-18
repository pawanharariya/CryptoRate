package com.psh.project.cryptorate.ui.currency

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.psh.project.cryptorate.R
import com.psh.project.cryptorate.databinding.FragmentCurrencyBinding
import com.psh.project.cryptorate.utils.snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CurrencyFragment : Fragment() {

    private var _binding: FragmentCurrencyBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CurrencyViewModel by viewModels()

    private var snackbar: Snackbar? = null

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
            viewModel.forceRefreshLiveRates()
            binding.swipeRefreshLayout.isRefreshing = false
            // dismiss existing snackbar if any
            snackbar?.dismiss()
        }
        val currencyRecyclerView = binding.currencyRecyclerView
        currencyRecyclerView.setHasFixedSize(true)
        val currencyAdapter = CurrencyAdapter()
        currencyRecyclerView.adapter = currencyAdapter

        viewModel.currencyList.observe(viewLifecycleOwner) {
            it?.let {
                currencyAdapter.submitList(it)
            }
        }

        viewModel.liveRateMap.observe(viewLifecycleOwner) {
            it?.let {
                currencyAdapter.updateLiveRating()
            }
        }

        viewModel.snackbarEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                snackbar = view.snackbar(it)
            }
        }

        viewModel.lastRefreshTime.observe(viewLifecycleOwner) {
            if (it.isNotEmpty())
                binding.lastUpdateText.text = getString(R.string.last_refreshed_text, it)
        }
    }

    // live rates will only be fetched when fragment is visible
    override fun onStart() {
        super.onStart()
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