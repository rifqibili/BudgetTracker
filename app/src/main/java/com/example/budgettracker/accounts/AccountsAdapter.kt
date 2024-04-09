package com.example.budgettracker.accounts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.budgettracker.MainViewModel
import com.example.budgettracker.R

class AccountsAdapter(val list: List<AccountsData>, val findNavController: NavController, val viewModel: MainViewModel) :
    RecyclerView.Adapter<AccountsAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val balance : TextView = itemView.findViewById(R.id.balance)
        val account : TextView = itemView.findViewById(R.id.accountItem)
        val image : ImageView = itemView.findViewById(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.account_item, parent, false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when(list[position].accountType) {
            "Cash" -> holder.image.setImageResource(R.drawable.money_icon)
            "Card" -> holder.image.setImageResource(R.drawable.card_icon)
            "Bank account" -> holder.image.setImageResource(R.drawable.bank_icon)
        }
        holder.balance.text = list[position].balance
        holder.account.text = list[position].name
        holder.itemView.setOnClickListener {
            viewModel.isSavingsSelected = list[position].isSavings
            viewModel.selectedAccountIndex = position
            findNavController.navigate(R.id.action_accounts_to_accountInformationFragment)
        }

    }
}