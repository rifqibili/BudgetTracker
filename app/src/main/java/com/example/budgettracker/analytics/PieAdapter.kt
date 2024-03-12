package com.example.budgettracker.analytics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.budgettracker.ViewModel
import com.example.budgettracker.R
import com.example.budgettracker.operations.OperationsData

class PieAdapter(val list: List<OperationsData>, val findNavController: NavController, val viewModel: ViewModel) : RecyclerView.Adapter<PieAdapter.ViewHolder>() {
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
        holder.itemView.setOnClickListener {
            findNavController.navigate(R.id.action_analytics_to_categoryAnalyticsFragment)
            when (viewModel.typeOfAnalyzedOperation) {
                0 -> viewModel.analyzedCategoryIndexExpense = position
                1 -> viewModel.analyzedCategoryIndexIncome = position
            }

        }

    }

}