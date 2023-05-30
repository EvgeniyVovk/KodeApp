package com.example.kodeapp.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.kodeapp.R
import com.example.kodeapp.appComponent
import com.example.kodeapp.databinding.FragmentErrorBinding
import com.example.kodeapp.viewmodel.ListViewModel
import com.example.kodeapp.viewmodel.ViewModelFactory
import javax.inject.Inject

class ErrorFragment : Fragment() {

    private lateinit var binding: FragmentErrorBinding
    private val viewModel: ListViewModel by activityViewModels {
        factory.create()
    }

    @Inject
    lateinit var factory: ViewModelFactory.Factory

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentErrorBinding.inflate(inflater, container, false)

        setupTryAgainButton()

        return binding.root
    }

    private fun setupTryAgainButton(){
        binding.tryAgainButton.setOnClickListener {
            viewModel.loadData()
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_container, HostFragment())
                .commit()
        }
    }
}