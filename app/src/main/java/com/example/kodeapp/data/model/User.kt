package com.example.kodeapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.ArrayList

data class UsersList(val items: ArrayList<User>)
@Parcelize
data class User(
    val id: String,
    val avatarUrl: String,
    val firstName: String,
    val lastName: String,
    val userTag: String,
    val department: String,
    val position: String,
    val birthday: String,
    val phone: String
): Parcelable {
    companion object{
        val DEFAULT = User(id = "", avatarUrl = "", firstName = "", lastName = "",
            userTag = "", department = "", position = "", birthday = "", phone = "")
    }
}