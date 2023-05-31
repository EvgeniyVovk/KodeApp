package com.example.kodeapp.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kodeapp.R
import com.example.kodeapp.appComponent
import com.example.kodeapp.data.model.User
import com.example.kodeapp.databinding.FragmentUserListBinding
import com.example.kodeapp.ui.adapters.UserAdapter
import com.example.kodeapp.ui.adapters.UserClickListener
import com.example.kodeapp.utils.Constants.ARG_POS
import com.example.kodeapp.viewmodel.ListViewModel
import com.example.kodeapp.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserListFragment : Fragment(R.layout.fragment_user_list) {

    private lateinit var binding: FragmentUserListBinding
    private lateinit var adapter: UserAdapter

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
        binding = FragmentUserListBinding.inflate(inflater, container, false)

        setupRecyclerView()

        return binding.root
    }

    private fun setupRecyclerView(){
        adapter = UserAdapter(viewModel.userClickInterfaceImpl.value)

        val layoutManager = LinearLayoutManager(context)
        binding.usersList.layoutManager = layoutManager
        binding.usersList.adapter = adapter

        val decoration = DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL)
        binding.usersList.addItemDecoration(decoration)

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.usersListToShow.collect { result ->
                    adapter.users = result
                }
            }
        }
        binding.usersList.scrollToPosition(0)
    }
}