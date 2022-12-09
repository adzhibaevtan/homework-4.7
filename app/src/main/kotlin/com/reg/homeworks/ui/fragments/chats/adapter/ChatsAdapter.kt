package com.reg.homeworks.ui.fragments.chats.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.reg.homeworks.data.models.Users
import com.reg.homeworks.databinding.ItemUserBinding

class ChatsAdapter: RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder>() {

    private val list: ArrayList<Users> = arrayListOf()

    fun addUsers(newData:ArrayList<Users>) {
        list.clear()
        list.addAll(newData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ChatsViewHolder(
        ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )


    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class ChatsViewHolder(private val binding: ItemUserBinding) : ViewHolder(binding.root) {
        fun bind(userInfo: Users) {
            binding.tvPhone.text = userInfo.phone
        }
    }
}

