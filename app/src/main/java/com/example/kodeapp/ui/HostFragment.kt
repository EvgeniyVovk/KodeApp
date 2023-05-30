package com.example.kodeapp.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.kodeapp.R
import com.example.kodeapp.appComponent
import com.example.kodeapp.databinding.FragmentHostBinding
import com.example.kodeapp.utils.Constants
import com.example.kodeapp.viewmodel.ListViewModel
import com.example.kodeapp.viewmodel.ViewModelFactory
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Job
import javax.inject.Inject

class HostFragment : Fragment() {

    private lateinit var binding: FragmentHostBinding
    private lateinit var pagerAdapter: ViewPagerAdapter
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
        binding = FragmentHostBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment

        setupViewPagerWithTabs()
        setupSearchView()
        setupClearButton()
        setupFilterButton()

        return binding.root
    }

    private fun setupClearButton(){
        binding.cancelButton.setOnClickListener {
            binding.searchView.setQuery("",false)
            binding.searchView.clearFocus()
            //binding.cancelButton.visibility = View.GONE
        }
    }

    private fun setupFilterButton(){
        binding.filterButton.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog(){
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottom_sheet_layout)
        val radioGroup = dialog.findViewById<RadioGroup>(R.id.dialog_radio_group)
        radioGroup.setOnCheckedChangeListener { _radioGroup, checkedId ->
            when (checkedId){
                R.id.radio_button_1 -> {
                    viewModel.sortByAlphabet()
                    dialog.dismiss()
                }
                R.id.radio_button_2 -> {
                    viewModel.sortByBirthday()
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

    private fun setupViewPagerWithTabs(){
        pagerAdapter = ViewPagerAdapter(parentFragmentManager, lifecycle)
        binding.pagerLayout.adapter = pagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.pagerLayout){ tab, position->
            tab.text = Constants.tabNames[position]
        }.attach()
        binding.pagerLayout.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.tabsFilterList(position)
            }
        })
    }

    private fun setupSearchView(){
        //binding.searchView.clearFocus()
        val searchIcon = binding.searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        binding.searchView.setOnQueryTextFocusChangeListener (object : View.OnFocusChangeListener {
            override fun onFocusChange(p0: View?, p1: Boolean) {
                if (binding.cancelButton.visibility == View.GONE) {
                    binding.filterButton.visibility = View.INVISIBLE
                    binding.cancelButton.visibility = View.VISIBLE
                    searchIcon.setImageResource(R.drawable.search_icon_focusable)
                } else {
                    searchIcon.setImageResource(R.drawable.search_icon)
                    binding.cancelButton.visibility = View.GONE
                    binding.filterButton.visibility = View.VISIBLE
                }
            }
        })
        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchFilterList(newText.orEmpty())
                return true
            }
        })
    }
}