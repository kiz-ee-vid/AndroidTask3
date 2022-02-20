package com.example.task3.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.task3.model.Contact
import com.example.task3.databinding.DialogBinding

class DialogAdapter(var roomContactsList: List<Contact>, val itemClick: (Contact) -> Unit) :
    RecyclerView.Adapter<DialogAdapter.DialogHolder>() {

    inner class DialogHolder(val binding: DialogBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogHolder {
        val binding = DialogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DialogHolder(binding)
    }

    override fun onBindViewHolder(holder: DialogHolder, position: Int) {
        with(holder) {
            if (roomContactsList[position].number != null) {
                binding.dialogNumber.visibility = View.VISIBLE
                binding.dialogNumber.text = roomContactsList[position].number ?: "null"
                itemView.setOnClickListener { itemClick(roomContactsList[position]) }
            } else
                binding.dialogNumber.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return roomContactsList.size
    }
}