package com.example.kodeapp.navigation

import com.github.terrakok.cicerone.Router

interface UserDetailsScreenRouter {
    fun routeToHostScreen()
}

class UserDetailsScreenRouterImpl(
    private val router: Router
) : UserDetailsScreenRouter {

    override fun routeToHostScreen() {
        router.exit()
    }
}