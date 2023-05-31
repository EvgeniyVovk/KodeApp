package com.example.kodeapp.ui.adapters

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kodeapp.R
import com.example.kodeapp.data.model.User
import com.example.kodeapp.databinding.ViewHolderUserBinding
import com.example.kodeapp.utils.toDayMonthString
import com.example.kodeapp.utils.toLocalDate

class UserViewHolder(
    private val binding: ViewHolderUserBinding,
    private val actionLister: UserClickListener?,
    private val showDate: Boolean = false
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    private var user: User?= null


    @SuppressLint("SetTextI18n")
    fun bind(user: User) {

        binding.root.setOnClickListener(this)

        this.user = user
        binding.userName.text = "${user.firstName} ${user.lastName}"
        binding.userPosition.text = user.position
        binding.userTag.text = user.userTag

        if(showDate){
            val date = user.birthday.toLocalDate()
            date?.let { d ->
                val str = d.toDayMonthString()
                binding.userDate.text = str
                binding.userDate.visibility = View.VISIBLE
            }
        }

        if (user.avatarUrl.isNotBlank()) {
            Glide.with(binding.avatar.context)
                .load(user.avatarUrl)
                .circleCrop()
                .placeholder(R.drawable.icon_avatar)
                .error(R.drawable.icon_avatar)
                .into(binding.avatar)
        } else {
            binding.avatar.setImageResource(R.drawable.icon_avatar)
        }
    }

    override fun onClick(p0: View?) {
        user?.let { actionLister?.onUserDetails(it) }
    }
}