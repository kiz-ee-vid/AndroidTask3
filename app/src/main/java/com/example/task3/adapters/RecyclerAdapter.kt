package com.example.task3.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.task3.model.Contact
import com.example.task3.databinding.ListItemBinding

class RecyclerAdapter(var contactsList: List<Contact>, val itemClick: (Int) -> Unit) :
    RecyclerView.Adapter<RecyclerAdapter.ContactHolder>() {

    inner class ContactHolder(val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        with(holder) {
            with(contactsList[position]) {
                val name = "${this.firstName?.plus(" ") ?: ""}${this.lastName ?: ""}"
                binding.name.text = name
                binding.number.text = this.number ?: "No information"
                binding.email.text = this.email ?: "No information"
                itemView.setOnClickListener { itemClick(position) }
            }
        }
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }

}