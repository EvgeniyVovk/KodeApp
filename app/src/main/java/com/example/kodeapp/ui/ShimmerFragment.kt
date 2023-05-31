package com.example.kodeapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kodeapp.R
import com.example.kodeapp.databinding.FragmentShimmerBinding

class ShimmerFragment : Fragment(R.layout.fragment_shimmer) {

    private var _binding: FragmentShimmerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding= FragmentShimmerBinding.inflate(inflater, container, false)
        _binding?.shimmerViewContainer?.startShimmer()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding?.shimmerViewContainer?.stopShimmer()
        _binding = null
    }
}