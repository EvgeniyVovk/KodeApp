package com.example.kodeapp.ui
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kodeapp.R
import com.example.kodeapp.data.User
import com.example.kodeapp.databinding.ViewHolderUserBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

interface UserClickLister {
    fun onUserDetails(user: User)
}

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
    private val actionLister: UserClickLister
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>(), View.OnClickListener {
    var users: List<User> = emptyList()
        set(newValue){
            val diffResult = DiffUtil.calculateDiff(MovieDiffCallback(field, newValue))
            field = newValue
            diffResult.dispatchUpdatesTo(this)
        }

    var sortingFlag: Boolean = false

    class UserViewHolder(
        val binding: ViewHolderUserBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ViewHolderUserBinding.inflate(inflater, parent, false)

        binding.root.setOnClickListener(this)

        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        with(holder.binding) {
            holder.itemView.tag = user
            userName.text = user.firstName + " " + user.lastName
            userPosition.text = user.position
            userTag.text = user.userTag
            userDate.text = formatDate(user.birthday)
            if (sortingFlag) {
                userDate.visibility = View.VISIBLE
            } else {
                userDate.visibility = View.INVISIBLE
            }
            if (user.avatarUrl.isNotBlank()) {
                Glide.with(avatar.context)
                    .load(user.avatarUrl)
                    .circleCrop()
                    .placeholder(R.drawable.icon_avatar)
                    .error(R.drawable.icon_avatar)
                    .into(avatar)
            } else {
                avatar.setImageResource(R.drawable.icon_avatar)
            }
        }
    }

    override fun getItemCount(): Int = users.size

    override fun onClick(p0: View?) {
        val user = p0?.tag as User
        actionLister.onUserDetails(user)
    }

    private fun formatDate(dateString: String): String {
        val inputDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        val outputDateFormat = DateTimeFormatter.ofPattern("dd MMM", Locale("ru"))
        val date = LocalDate.parse(dateString, inputDateFormat)
        return outputDateFormat.format(date)
    }
}