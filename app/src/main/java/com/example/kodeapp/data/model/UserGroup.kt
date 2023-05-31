package com.example.kodeapp.data.model

sealed class UserGroup(val itemType: ItemType) {

    data class Users(val user: User) :
        UserGroup(ItemType.Item)

    data class Header(val name: String) : UserGroup(ItemType.Header)
}

enum class ItemType {
    Item,
    Header
}