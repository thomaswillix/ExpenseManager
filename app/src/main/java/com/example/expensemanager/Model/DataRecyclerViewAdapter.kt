package com.example.expensemanager.Model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanager.R
import com.example.expensemanager.databinding.ExpenseRecyclerDataBinding

class DataRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val dataFeedItems = mutableListOf<Data>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ExpenseItemViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ExpenseItemViewHolder).onBind(dataFeedItems[position])
    }

    override fun getItemCount(): Int {
        return dataFeedItems.size
    }
    fun setItems(dataFeedItems: List<Data>){
        this.dataFeedItems.clear()
        this.dataFeedItems.addAll(dataFeedItems)
        notifyDataSetChanged()
    }
    inner class ExpenseItemViewHolder(parent:ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.expense_recycler_data, parent, false)
    ) {
        private lateinit var mView: View
        private val binding = ExpenseRecyclerDataBinding.bind(itemView)
        fun onBind(data:Data){
            binding.dateTextExpense.text = data.date
            binding.typeTextExpense.text = data.type
            binding.noteTextExpense.text = data.note
            binding.amountTextExpense.text = data.amount.toString()
        }
    }
}