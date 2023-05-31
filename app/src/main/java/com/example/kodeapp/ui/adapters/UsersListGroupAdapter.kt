package com.example.kodeapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kodeapp.data.model.ItemType
import com.example.kodeapp.data.model.UserGroup
import com.example.kodeapp.databinding.ViewHolderUserBinding
import com.example.kodeapp.databinding.YearHeaderBinding

class UsersListGroupAdapter(
    private val actionLister: UserClickListener?
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var usersGroup: List<UserGroup> = emptyList()
        set(newValue){
            field = newValue
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (ItemType.values()[viewType]) {
            ItemType.Item -> {
                val binding =
                    ViewHolderUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                UserViewHolder(binding, actionLister, true)
            }
            ItemType.Header -> {
                val binding =
                    YearHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HeaderViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int = usersGroup.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        when (val groups = usersGroup[position]) {
            is UserGroup.Users -> (holder as UserViewHolder).bind(groups.user)
            is UserGroup.Header -> (holder as HeaderViewHolder).bind(groups)
        }

    override fun getItemViewType(position: Int): Int =
        usersGroup[position].itemType.ordinal

    class HeaderViewHolder(
        private val binding: YearHeaderBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(header: UserGroup.Header) {
            binding.yearHeaderText.text = header.name
        }
    }
}