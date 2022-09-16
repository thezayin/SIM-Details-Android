package com.example.simdetails.data.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.simdetails.R
import com.example.simdetails.data.model.Title

class CustomAdapter(private val title: ArrayList<Title>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val binding =
            LayoutInflater.from(parent.context).inflate(R.layout.cards_component, parent, false)

        return ViewHolder(binding)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(title[position])

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return title.size
    }

    // Holds the views for adding it to image and text
    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        fun bind(item: Title) {
            val tvTitle = itemView.findViewById(R.id.tvTitle) as TextView
            tvTitle.text = item.title
        }
    }
}