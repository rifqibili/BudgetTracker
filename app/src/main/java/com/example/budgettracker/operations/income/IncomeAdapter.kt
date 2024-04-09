package com.example.budgettracker.operations.income

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgettracker.R
import com.example.budgettracker.MainViewModel
import com.example.budgettracker.operations.expense.AddData


class IncomeAdapter(val list : List<AddData>, val viewModel: MainViewModel) : RecyclerView.Adapter<IncomeAdapter.ViewHolder>() {

    private var isNewRadioButtonChecked = false
    private var lastCheckedPosition = -1

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val button : RadioButton = itemView.findViewById(R.id.categoryButton)
        val text : TextView = itemView.findViewById(R.id.categoryText)

        init {
            button.setOnClickListener {
                handleRadioButtonChecks(adapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.add_item, parent, false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val resIdName = list[position].categoryIcon
        val resId = holder.itemView.resources.getIdentifier(resIdName, "drawable", holder.itemView.context.packageName)
        holder.button.setButtonDrawable(resId)
        holder.text.text = list[position].categoryName

        val element = list[position]
        if (isNewRadioButtonChecked) {
            holder.button.isChecked = element.isSelected
        }
        else {
            if (holder.adapterPosition == 0) {
                holder.button.isChecked = true
                lastCheckedPosition = 0
            }
        }

        if (holder.button.isChecked){
            val incomeInfo = AddData(list[position].categoryIcon, list[position].categoryName, true)
            viewModel.addIncome(incomeInfo)
        }

    }

    private fun handleRadioButtonChecks(adapterPosition: Int) {
        isNewRadioButtonChecked = true
        list[lastCheckedPosition].isSelected = false
        list[adapterPosition].isSelected = true
        lastCheckedPosition = adapterPosition
        notifyDataSetChanged()
    }
}