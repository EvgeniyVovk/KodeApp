package com.example.kodeapp.ui.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kodeapp.R
import com.example.kodeapp.data.model.User
import com.example.kodeapp.databinding.ViewHolderUserBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class MovieDiffCallback(
    private val oldUserList: List<User>,
    private val newUserList: List<User>
): DiffUtil.Callback(){
    override fun getOldListSize(): Int = oldUserList.size
    override fun getNewListSize(): Int = newUserList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldUser = oldUserList[oldItemPosition]
        val newUser = newUserList[newItemPosition]
        return oldUser.id == newUser.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldUser = oldUserList[oldItemPosition]
        val newUser = newUserList[newItemPosition]
        return oldUser == newUser
    }
}

class UserAdapter(
    private val actionLister: UserClickListener?
) : RecyclerView.Adapter<UserViewHolder>() {
    var users: List<User> = emptyList()
        set(newValue){
            val diffResult = DiffUtil.calculateDiff(MovieDiffCallback(field, newValue))
            field = newValue
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ViewHolderUserBinding.inflate(inflater, parent, false)

        return UserViewHolder(binding, actionLister)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int = users.size

    private fun getItem(position: Int): User = users[position]
}