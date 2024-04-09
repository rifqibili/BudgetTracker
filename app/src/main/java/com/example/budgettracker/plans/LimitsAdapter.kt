package com.example.budgettracker.plans


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.budgettracker.R

class LimitsAdapter(val list : List<LimitsData>, val findNavController: NavController, val viewModel : com.example.budgettracker.MainViewModel) :
    RecyclerView.Adapter<LimitsAdapter.ViewHolder>() {
    interface OnItemLongClickListener {
        fun onItemLongClick(position: Int)
    }

    private var onItemLongClickListener: OnItemLongClickListener? = null

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        this.onItemLongClickListener = listener
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val image : ImageView = itemView.findViewById(R.id.icon)
        val progressBar : DualColorProgressBar = itemView.findViewById(R.id.progressBar)
        val categoryName : TextView = itemView.findViewById(R.id.categoryName)
        val amount : TextView = itemView.findViewById(R.id.amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.limits_item, parent, false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val resIdName = list[position].categoryIcon
        val resId = holder.itemView.resources.getIdentifier(resIdName, "drawable", holder.itemView.context.packageName)
        holder.image.setImageResource(resId)
        holder.categoryName.text = list[position].categoryName


        holder.progressBar.max = 150


        var progress = 0.0

        val expenses = viewModel.divideOperationsByMonth(viewModel.allExpenses)
        Log.d("TAG", "${viewModel.allExpenses.size}")
        if (expenses.isNotEmpty()){
            for (element in expenses[0]) {
                if (element.category == list[position].categoryName) {
                    progress += element.amount.toDouble()
                }
            }
        }

        holder.amount.text = "Spended $progress of ${list[position].value}"

        progress = (progress / list[position].value) * 100
        Log.d("TAG", "$progress")

        holder.progressBar.progress = progress.toInt()

        holder.itemView.setOnLongClickListener {
            onItemLongClickListener?.onItemLongClick(position)
            true
        }


    }
}

