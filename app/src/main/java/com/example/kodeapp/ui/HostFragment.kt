package com.example.kodeapp.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.kodeapp.R
import com.example.kodeapp.appComponent
import com.example.kodeapp.data.Result
import com.example.kodeapp.data.model.User
import com.example.kodeapp.databinding.FragmentHostBinding
import com.example.kodeapp.navigation.HostScreenRouter
import com.example.kodeapp.navigation.HostScreenRouterImpl
import com.example.kodeapp.navigation.UserDetailsScreenRouterImpl
import com.example.kodeapp.ui.adapters.UserClickListener
import com.example.kodeapp.utils.Constants.tabNames
import com.example.kodeapp.viewmodel.ListViewModel
import com.example.kodeapp.viewmodel.ViewModelFactory
import com.github.terrakok.cicerone.Router
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
@AndroidEntryPoint
class HostFragment : Fragment(R.layout.fragment_host), SwipeRefreshLayout.OnRefreshListener, UserClickListener {

    private lateinit var binding: FragmentHostBinding

    private lateinit var snackbar: Snackbar

    private val viewModel: ListViewModel by activityViewModels {
        factory.create()
    }

    @Inject
    lateinit var router: Router

    private val hostScreenRouter: HostScreenRouter by lazy {
        HostScreenRouterImpl(router)
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

        setupTabs()
        setupSearchView()
        setupClearButton()
        setupFilterButton()
        setupSwipeRefreshLayout()

        if (viewModel.userClickInterfaceImpl.value == null) viewModel.setClickInterface(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupSnackBar()
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.users.collect { result ->
                    when (result) {
                        is Result.Success -> showData(result.data)
                        is Result.Loading -> showLoading()
                        is Result.Error -> showError(result.message,result.exception)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sortingFlag.collect {
                    if (it) setFragment(UserListByGroupFragment())
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.tabLayout.getTabAt(viewModel.selectedPositionTabIndex.value)?.select()
    }

    private fun showLoading() {
        setFragment(ShimmerFragment())
        snackbar.show()
        // отобразить индикатор загрузки или другие элементы UI для обратной связи о загрузке данных
    }

    private fun setupSnackBar(){
        snackbar = Snackbar.make(this.requireView(), R.string.snackbar_loading_text, Snackbar.LENGTH_INDEFINITE)
        snackbar.setBackgroundTint(Color.parseColor("#6534FF"))
        snackbar.setTextColor(Color.WHITE)
    }

    private fun showData(data: List<User>) {
        setFragment(UserListFragment())
        snackbar.dismiss()
        binding.refreshLayout.isRefreshing = false
        if (data.isEmpty()) {
            setFragment(NothingFoundFragment())
        } else {
            if (viewModel.sortingFlag.value) {
                setFragment(UserListByGroupFragment())
            } else {
                setFragment(UserListFragment())
            }
        }
        // отобразить данные в UI
    }

    private fun setFragment(fragment: Fragment){
        childFragmentManager.beginTransaction()
            .replace(R.id.list_container, fragment)
            .commit()
    }

    private fun showError(message: String?, e: Exception?) {
        snackbar.dismiss()
        binding.refreshLayout.isRefreshing = false
        hostScreenRouter.routeToErrorScreen()
        // отобразить сообщение об ошибке в UI
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

    override fun onRefresh() {
        viewModel.loadData(binding.tabLayout.selectedTabPosition)
    }

    private fun setupTabs(){
        val tabs = binding.tabLayout
        tabNames.forEach { tabName ->
            tabs.newTab().let {
                it.text = tabName
                tabs.addTab(it)
            }
        }
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewModel.saveTabIndex(tab?.position ?: 0)
                tab?.position?.let { viewModel.tabsFilterList(it) }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupSwipeRefreshLayout(){
        binding.refreshLayout.setOnRefreshListener(this)
        binding.refreshLayout.setColorSchemeResources(R.color.purple_500)
    }

    private fun setupSearchView(){
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
                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchFilterList(newText.orEmpty())
                return true
            }
        })
    }

    override fun onUserDetails(user: User) {
        hostScreenRouter.routeToDetailScreen(user)
    }
}