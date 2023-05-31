package com.example.kodeapp.navigation

import com.example.kodeapp.data.model.User
import com.github.terrakok.cicerone.Router

interface HostScreenRouter {
    fun routeToDetailScreen(user: User)
    fun routeToErrorScreen()
}

class HostScreenRouterImpl(
    private val router: Router
) : HostScreenRouter {

    override fun routeToDetailScreen(user: User) {
        router.navigateTo(Screens.userDetailScreen(user))
    }

    override fun routeToErrorScreen() {
        router.navigateTo(Screens.errorScreen())
    }
}