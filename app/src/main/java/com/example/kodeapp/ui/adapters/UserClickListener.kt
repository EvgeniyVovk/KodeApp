package com.example.kodeapp.ui.adapters

import com.example.kodeapp.data.model.User

interface UserClickListener {
    fun onUserDetails(user: User)
}