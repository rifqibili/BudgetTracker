package com.example.budgettracker.plans

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.budgettracker.R
import com.example.budgettracker.ViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class NotificationsAdapter(val list: List<PlannedOperation>, val findNavController: NavController, val viewModel: ViewModel)
    : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

        /*
    interface OnItemLongClickListener {
        fun onItemLongClick(position: Int)
    }

    private var onItemLongClickListener: OnItemLongClickListener? = null

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        this.onItemLongClickListener = listener
    }

         */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date : TextView = itemView.findViewById(R.id.date)
        val image : ImageView = itemView.findViewById(R.id.image)
        val amount : TextView = itemView.findViewById(R.id.balance)
        val category : TextView = itemView.findViewById(R.id.category)
        val account : TextView = itemView.findViewById(R.id.account)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.planned_operation, parent, false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy HH mm", Locale.getDefault())
        holder.date.text = dateFormat.format(list[position].date)
        holder.image.setImageResource(list[position].icon)
        when(list[position].type){
            "Expense" -> {
                holder.amount.text = "-" + list[position].amount
                holder.amount.setTextColor(Color.RED)
            }
            "Income" -> holder.amount.text = "+" + list[position].amount
            "Transfer" -> holder.amount.text = list[position].amount
        }

        holder.category.text = list[position].category
        holder.account.text = list[position].account
        /*
        holder.itemView.setOnLongClickListener {
            onItemLongClickListener?.onItemLongClick(position)
            true
        }

         */
        holder.itemView.setOnClickListener {
            viewModel.selectedPlannedOperationIndex = position
            findNavController.navigate(R.id.notificationInformation)
        }
    }
}