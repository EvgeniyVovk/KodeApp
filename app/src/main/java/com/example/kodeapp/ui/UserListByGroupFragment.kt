package com.example.kodeapp.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kodeapp.R
import com.example.kodeapp.appComponent
import com.example.kodeapp.data.model.User
import com.example.kodeapp.data.model.UserGroup
import com.example.kodeapp.databinding.FragmentUserListBinding
import com.example.kodeapp.ui.adapters.UserAdapter
import com.example.kodeapp.ui.adapters.UsersListGroupAdapter
import com.example.kodeapp.utils.Constants
import com.example.kodeapp.utils.toLocalDate
import com.example.kodeapp.viewmodel.ListViewModel
import com.example.kodeapp.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

class UserListByGroupFragment: Fragment(R.layout.fragment_user_list) {
    private lateinit var binding: FragmentUserListBinding
    private lateinit var adapter: UsersListGroupAdapter

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
    ): View {
        binding= FragmentUserListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
    }

    private fun setupRecyclerView(){
        adapter = UsersListGroupAdapter(viewModel.userClickInterfaceImpl.value)
        val layoutManager = LinearLayoutManager(context)
        binding.usersList.layoutManager = layoutManager
        binding.usersList.adapter = adapter

        val decoration = DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL)
        binding.usersList.addItemDecoration(decoration)

        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.usersListToShow.collect { result ->
                    adapter.usersGroup = setupYearGroups(result)
                }
            }
        }
        binding.usersList.scrollToPosition(0)
    }

    private fun setupYearGroups(list: List<User>): List<UserGroup> {
        var usersThisYear= mutableListOf<User>()
        var usersNextYear= mutableListOf<User>()
        list.forEach { user ->
            val date = user.birthday.toLocalDate()
            date?.let { d ->
                val day = d.dayOfMonth
                val month = d.month
                val year = LocalDate.now().year
                val newDate = LocalDate.of(year, month, day)

                if (newDate.isAfter(LocalDate.now())) {
                    usersThisYear.add(user)
                } else {
                    usersNextYear.add(user)
                }
            }
        }

        usersThisYear= usersThisYear.sortedWith(
            compareBy(
                { it.birthday.toLocalDate()?.month },
                { it.birthday.toLocalDate()?.dayOfMonth },
            )
        ).toMutableList()

        usersNextYear= usersNextYear.sortedWith(
            compareBy(
                { it.birthday.toLocalDate()?.month },
                { it.birthday.toLocalDate()?.dayOfMonth },
            )
        ).toMutableList()

        val userAdapted= mutableListOf<UserGroup>()
        usersThisYear.forEach { userYearGrouped ->
            userAdapted.add(UserGroup.Users(userYearGrouped))
        }
        userAdapted.add(UserGroup.Header(Constants.NEXT_YEAR))
        usersNextYear.forEach { userYearGrouped ->
            userAdapted.add(UserGroup.Users(userYearGrouped))
        }

        return userAdapted
    }

}