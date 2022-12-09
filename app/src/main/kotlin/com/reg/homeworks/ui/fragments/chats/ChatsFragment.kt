package com.reg.homeworks.ui.fragments.chats

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.reg.homeworks.R
import com.reg.homeworks.data.models.Users
import com.reg.homeworks.databinding.FragmentChatsBinding
import com.reg.homeworks.ui.fragments.chats.adapter.ChatsAdapter
import com.google.firebase.firestore.FirebaseFirestore

class ChatsFragment : Fragment(R.layout.fragment_chats) {
    private val binding by viewBinding(FragmentChatsBinding::bind)

    private val data = arrayListOf<Users>()
    private lateinit var adapter: ChatsAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ref = FirebaseFirestore.getInstance().collection("Users")
        adapter = ChatsAdapter()
        binding.rvChat.adapter = adapter

        ref.get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (item in it.result.documents) {
                    val user = item.toObject(Users::class.java)
                   if (user!=null) {
                       data.add(user)
                   }
                }

                adapter.addUsers(data)

            }
        }
    }
}
