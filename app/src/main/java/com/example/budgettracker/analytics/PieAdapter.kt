package com.example.budgettracker.analytics

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgettracker.R
import com.example.budgettracker.operations.OperationsData

class PieAdapter(val list: List<OperationsData>) : RecyclerView.Adapter<PieAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image : ImageView = itemView.findViewById(R.id.image)
        val amount : TextView = itemView.findViewById(R.id.balance)
        val category : TextView = itemView.findViewById(R.id.category)
        val account : TextView = itemView.findViewById(R.id.account)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.operation_item, parent, false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.image.setImageResource(list[position].icon)
        holder.image.setColorFilter(list[position].color)
        holder.amount.text = list[position].amount
        holder.category.text = list[position].category
        holder.account.text = list[position].account
    }

}