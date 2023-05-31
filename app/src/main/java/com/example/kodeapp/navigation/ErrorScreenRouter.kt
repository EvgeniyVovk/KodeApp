package com.example.kodeapp.navigation

import com.github.terrakok.cicerone.Router

interface ErrorScreenRouter {
    fun routeToHostScreen()
}

class ErrorScreenRouterImpl(
    private val router: Router
) : ErrorScreenRouter {

    override fun routeToHostScreen() {
        router.navigateTo(Screens.hostScreen())
    }
}