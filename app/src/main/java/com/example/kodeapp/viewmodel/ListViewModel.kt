package com.example.kodeapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kodeapp.data.*
import com.example.kodeapp.utils.Constants.departments
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class ListViewModel(private val repository: Repository) : ViewModel() {

    private val _users = MutableSharedFlow<Result<List<User>>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_LATEST)
    val users: SharedFlow<Result<List<User>>> = _users.asSharedFlow()

    private val _sortingFlag = MutableSharedFlow<Boolean>(replay = 1, onBufferOverflow = BufferOverflow.DROP_LATEST)
    val sortingFlag: SharedFlow<Boolean> = _sortingFlag.asSharedFlow()

    private var originalList: List<User> = listOf()
    private var currentList: List<User> = listOf()

    init {
        loadData()
    }

    fun loadData(position: Int = 0) {
        viewModelScope.launch {
            updateFlag(false)
            _users.emit(Result.Loading)
            val result = repository.getData()
            if (result is Result.Success){
                originalList = result.data
                tabsFilterList(position)
            } else {
                _users.emit(result)
            }
        }
    }

    private fun updateFlag(flag: Boolean){
        _sortingFlag.tryEmit(flag)
    }

    fun sortByAlphabet(){
        updateFlag(false)
        val nameComparator = compareBy<User>(
            { it.firstName },
            { it.lastName }
        )
        if (currentList.isNotEmpty()) {
            val filteredUsers = currentList.sortedWith(nameComparator)
            _users.tryEmit(Result.Success(filteredUsers))
        }
    }

    fun sortByBirthday(){
        if (currentList.isNotEmpty()) {
            val filteredUsers = currentList.sortedBy { it.birthday }
            _users.tryEmit(Result.Success(filteredUsers))
            updateFlag(true)
        }
    }

    fun searchFilterList(query: String){
        if (currentList.isNotEmpty()) {
            val filteredUsers = if (query.isBlank()) {
                currentList
            } else {
                currentList.filter { it.firstName.contains(query, ignoreCase = true) }
            }
            _users.tryEmit(Result.Success(filteredUsers))
        }
    }

    fun tabsFilterList(position: Int){
        if (originalList.isNotEmpty()) {
            when (position){
                0 -> {
                    currentList = originalList
                    _users.tryEmit(Result.Success(originalList))
                }
                else -> {
                    currentList = originalList.filter {
                        it.department.contains(
                            departments[position]!!,
                            ignoreCase = true
                        )}
                    _users.tryEmit(Result.Success(currentList)
                    )
                }
            }
        }
    }
}