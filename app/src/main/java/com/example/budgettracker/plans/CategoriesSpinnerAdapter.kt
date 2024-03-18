package com.example.budgettracker.plans

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.budgettracker.R
import com.example.budgettracker.operations.expense.AddData

class CategoriesSpinnerAdapter(context : Context, val categories : List<AddData>) :
    ArrayAdapter<AddData>(context, 0, categories) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        var row = convertView
        val holder: ViewHolder

        if (row == null) {
            val inflater = LayoutInflater.from(context)
            row = inflater.inflate(R.layout.category_spinner_layout, parent, false)

            holder = ViewHolder()
            holder.categoryName = row.findViewById(R.id.categoryName)
            holder.categoryIcon = row.findViewById(R.id.categoryIcon)
            row.tag = holder
        } else {
            holder = row.tag as ViewHolder
        }

        val category = getItem(position) as AddData
        holder.categoryName.text = category.categoryName

        // Установка иконки в зависимости от категории
        val resourceId = context.resources.getIdentifier(category.categoryIcon, "drawable", context.packageName)
        holder.categoryIcon.setImageResource(resourceId)

        return row!!
    }

    private class ViewHolder {
        lateinit var categoryName: TextView
        lateinit var categoryIcon: ImageView
    }
}