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

        val currencyRecyclerView = binding.currencyRecyclerView
        currencyRecyclerView.setHasFixedSize(true)
        val currencyAdapter = CurrencyAdapter()
        currencyRecyclerView.adapter = currencyAdapter
        viewModel.cryptoList.observe(viewLifecycleOwner) {
            it?.let {
                currencyAdapter.submitList(it)

                Log.e("Ob", "Observing cryptoList")

            }
        }

        viewModel.liveRateMap.observe(viewLifecycleOwner){
            it?.let {
                currencyAdapter.updateLiveRating(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}