package com.example.budgettracker.operations

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgettracker.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.microseconds

class OperationsAdapter(val list: List<OperationsData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemViewType(position: Int): Int {
        if (position == 0){
            return 0
        }
        val firstDate : Date = list[position].date
        val secondDate : Date = list[position - 1].date
        val firstCal = Calendar.getInstance()
        val secondCal = Calendar.getInstance()
        firstCal.time = firstDate
        secondCal.time = secondDate
        if (firstCal.get(Calendar.YEAR) == secondCal.get(Calendar.YEAR) &&
            firstCal.get(Calendar.DAY_OF_MONTH) == secondCal.get(Calendar.DAY_OF_MONTH) &&
            firstCal.get(Calendar.MONTH) == secondCal.get(Calendar.MONTH)){
            return 1
        }
        return 0
    }
    internal inner class ViewHolderDate(itemView : View) : RecyclerView.ViewHolder(itemView){
        val date : TextView = itemView.findViewById(R.id.date)
        val image : ImageView = itemView.findViewById(R.id.image)
        val amount : TextView = itemView.findViewById(R.id.balance)
        val category : TextView = itemView.findViewById(R.id.category)
        val account : TextView = itemView.findViewById(R.id.account)
    }

    internal inner class ViewHolderRegular(itemView : View) : RecyclerView.ViewHolder(itemView){
        val image : ImageView = itemView.findViewById(R.id.image)
        val amount : TextView = itemView.findViewById(R.id.balance)
        val category : TextView = itemView.findViewById(R.id.category)
        val account : TextView = itemView.findViewById(R.id.account)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0)
            return ViewHolderDate(LayoutInflater.from(parent.context).inflate(R.layout.operation_date, parent, false))
        return ViewHolderRegular(LayoutInflater.from(parent.context).inflate(R.layout.operation_item, parent, false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        if (holder is ViewHolderDate){
            val operationDate = dateFormat.format(list[position].date)
            holder.date.text = operationDate
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
        }

        if (holder is ViewHolderRegular){
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
        }


    }
}