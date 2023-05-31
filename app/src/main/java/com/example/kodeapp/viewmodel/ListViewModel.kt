package com.example.kodeapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kodeapp.data.*
import com.example.kodeapp.data.model.User
import com.example.kodeapp.ui.adapters.UserClickListener
import com.example.kodeapp.utils.Constants.departments
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ListViewModel(private val repository: Repository) : ViewModel() {

    private val _users = MutableSharedFlow<Result<List<User>>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_LATEST)
    val users: SharedFlow<Result<List<User>>> = _users.asSharedFlow()

    private val _sortingFlag = MutableStateFlow(false)
    val sortingFlag: MutableStateFlow<Boolean> = _sortingFlag

    private val _usersListToShow = MutableStateFlow<List<User>>(listOf())
    val usersListToShow: MutableStateFlow<List<User>> = _usersListToShow

    private val _selectedPositionTabIndex = MutableStateFlow(0)
    val selectedPositionTabIndex:MutableStateFlow<Int> = _selectedPositionTabIndex

    private val _userClickInterfaceImpl: MutableLiveData<UserClickListener?> = MutableLiveData(null)
    val userClickInterfaceImpl: LiveData<UserClickListener?> = _userClickInterfaceImpl
    fun setClickInterface(clickInterface: UserClickListener){
        _userClickInterfaceImpl.value= clickInterface
    }

    private var originalList: List<User> = listOf()
    private var currentList: List<User> = listOf()

    init {
        loadData()
    }

    fun loadData(position: Int = 0) {
        viewModelScope.launch {
            _users.emit(Result.Loading)
            val result = repository.getData()
            if (result is Result.Success){
                originalList = result.data
                tabsFilterList(position)
            } else {
                if (currentList.isNotEmpty()) _users.emit(Result.Success(currentList))
                else _users.emit(result)
            }
        }
    }

    private fun updateFlag(flag: Boolean){
        _sortingFlag.tryEmit(flag)
    }

    fun saveTabIndex(position: Int){
        viewModelScope.launch {
            _selectedPositionTabIndex.emit(position)
        }
    }

    fun sortByAlphabet(){
        updateFlag(false)
        val nameComparator = compareBy<User>(
            { it.firstName },
            { it.lastName }
        )
        if (currentList.isNotEmpty()) {
            val filteredUsers = currentList.sortedWith(nameComparator)
            currentList = filteredUsers
            _usersListToShow.tryEmit(filteredUsers)
            _users.tryEmit(Result.Success(filteredUsers))
        }
    }

    fun sortByBirthday(){
        if (currentList.isNotEmpty()) {
            updateFlag(true)
        }
    }

    fun searchFilterList(query: String){
        if (currentList.isNotEmpty()) {
            val filteredUsers = if (query.isBlank()) {
                currentList
            } else {
                currentList.filter {
                    it.firstName.contains(query, ignoreCase = true)
                            || it.lastName.contains(query, ignoreCase = true)
                            || it.userTag.contains(query, ignoreCase = true)
                }
            }
            _users.tryEmit(Result.Success(filteredUsers))
            _usersListToShow.tryEmit(filteredUsers)
        }
    }

    fun tabsFilterList(position: Int){
        if (originalList.isNotEmpty()) {
            when (position){
                0 -> {
                    currentList = originalList
                    _usersListToShow.value = originalList
                    _users.tryEmit(Result.Success(originalList))
                }
                else -> {
                    currentList = originalList.filter {
                        it.department.contains(
                            departments[position]!!,
                            ignoreCase = true
                        )}
                    _usersListToShow.value = currentList
                    _users.tryEmit(Result.Success(currentList))
                }
            }
        }
    }
}