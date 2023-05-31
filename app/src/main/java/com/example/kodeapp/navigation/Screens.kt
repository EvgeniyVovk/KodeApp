package com.example.kodeapp.navigation

import com.example.kodeapp.data.model.User
import com.example.kodeapp.ui.ErrorFragment
import com.example.kodeapp.ui.HostFragment
import com.example.kodeapp.ui.UserDetailFragment
import com.github.terrakok.cicerone.androidx.FragmentScreen

object Screens {

    fun hostScreen() = FragmentScreen {
        HostFragment()
    }

    fun errorScreen() = FragmentScreen {
        ErrorFragment()
    }

    fun userDetailScreen(user: User) = FragmentScreen {
        UserDetailFragment.newInstance(user)
    }
}