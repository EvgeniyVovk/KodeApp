package com.example.kodeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kodeapp.data.Repository
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Provider

class ViewModelFactory @AssistedInject constructor(
    private val repository: Provider<Repository>) : ViewModelProvider.Factory{

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = when (modelClass) {
            ListViewModel::class.java -> {
                ListViewModel(repository.get())
            }
            UserDetailViewModel::class.java -> {
                UserDetailViewModel()
            }
            else -> {
                throw IllegalStateException("Unknown view model class")
            }
        }
        return viewModel as T
    }

    @AssistedFactory
    interface Factory {
        fun create(): ViewModelFactory
    }
}