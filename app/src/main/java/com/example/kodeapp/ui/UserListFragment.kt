package com.example.kodeapp.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.example.kodeapp.R
import com.example.kodeapp.appComponent
import com.example.kodeapp.data.*
import com.example.kodeapp.databinding.FragmentUserListBinding
import com.example.kodeapp.utils.Constants.ARG_POS
import com.example.kodeapp.viewmodel.ListViewModel
import com.example.kodeapp.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserListFragment : Fragment(), OnRefreshListener {

    private lateinit var binding: FragmentUserListBinding
    private lateinit var adapter: UserAdapter
    private lateinit var snackbar: Snackbar

    private val viewModel: ListViewModel by activityViewModels {
        factory.create()
    }

    @Inject
    lateinit var factory: ViewModelFactory.Factory

    private var tabPosition: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tabPosition = it.getInt(ARG_POS)
        }
    }

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserListBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupSwipeRefreshLayout()
        setupSnackBar()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Log.d("CREATED_VIEW_H","LIST_FRAGMENT")
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
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.sortingFlag.collect { flag ->
                    adapter.sortingFlag = flag
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onRefresh() {
        tabPosition?.let { viewModel.loadData(it) }
    }

    private fun showLoading() {
        snackbar.show()
        if (binding.shimmerViewContainer.visibility == View.GONE) {
            binding.shimmerViewContainer.visibility = View.VISIBLE
            binding.shimmerViewContainer.startShimmer()
        }
        // отобразить индикатор загрузки или другие элементы UI для обратной связи о загрузке данных
    }

    private fun showData(data: List<User>) {
        snackbar.dismiss()
        binding.shimmerViewContainer.stopShimmer()
        binding.shimmerViewContainer.visibility = View.GONE
        adapter.users = data
        binding.usersList.scrollToPosition(0)
        binding.refreshLayout.isRefreshing = false
        if (data.isEmpty()){
            binding.nothingFoundLayout.visibility = View.VISIBLE
        } else {
            binding.nothingFoundLayout.visibility = View.INVISIBLE
        }
        // отобразить данные в UI
    }

    private fun showError(message: String?, e: Exception?) {
        binding.refreshLayout.isRefreshing = false
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_container, ErrorFragment())
            .commit()
        // отобразить сообщение об ошибке в UI
    }

    private fun setupSnackBar(){
        snackbar = Snackbar.make(binding.root, R.string.snackbar_loading_text, Snackbar.LENGTH_INDEFINITE)
        snackbar.setBackgroundTint(Color.parseColor("#6534FF"))
        snackbar.setTextColor(Color.WHITE)
    }

    private fun setupRecyclerView(){
        adapter = UserAdapter(object : UserClickLister{
            override fun onUserDetails(user: User) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_container, UserDetailFragment.newInstance(user))
                    .addToBackStack(null)
                    .commit()
            }
        })

        val layoutManager = LinearLayoutManager(context)
        binding.usersList.layoutManager = layoutManager
        binding.usersList.adapter = adapter
    }

    private fun setupSwipeRefreshLayout(){
        binding.refreshLayout.setOnRefreshListener(this)
        binding.refreshLayout.setColorSchemeResources(R.color.purple_500)
    }

    companion object {
        @JvmStatic
        fun newInstance(tabPosition: Int) =
            UserListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_POS, tabPosition)
                }
            }
    }
}