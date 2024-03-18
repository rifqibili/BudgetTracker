package com.example.budgettracker.plans


import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.budgettracker.R

class LimitsAdapter(val list : List<LimitsData>, val findNavController: NavController, val viewModel : ViewModel) :
    RecyclerView.Adapter<LimitsAdapter.ViewHolder>() {
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
        holder.amount.text = list[position].value.toString()

        holder.progressBar.max = 150

        // Установка текущего значения прогресса
        holder.progressBar.progress = 101 // Измените значение в соответствии с вашими потребностями


    }
}