package com.example.expensemanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanager.databinding.ExpenseRecyclerDataBinding
import com.example.expensemanager.databinding.ListItemBinding

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
        LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
    ) {
        private val binding = ListItemBinding.bind(itemView)
        fun onBind(data: Data){
            binding.date.text = data.date
            binding.category.text = data.type
            binding.quantity.text = data.amount.toString()
            when(data.type){
                "Food" -> {
                    binding.icon.setImageResource(R.drawable.food)
                }
                "House"-> {
                    binding.icon.setImageResource(R.drawable.house)
                }
                "Entertainment"->{
                    binding.icon.setImageResource(R.drawable.entertainment)
                }
                "Personal expenses"->{
                    // TODO
                }
                "Health care"->{
                    binding.icon.setImageResource(R.drawable.healthcare)
                }
                "Transportation"->{
                    binding.icon.setImageResource(R.drawable.transport)
                }
                "Debt / Student Loan"->{
                    // TODO
                }
                "Payckeck"->{
                    // TODO
                }
                "Intellectual Propperty"->{
                    // TODO
                }
                "Stocks"->{
                    // TODO
                }
                "Business"->{
                    // TODO
                }
                "Savings, bonds or lending"->{
                    // TODO
                }
                "others"->{
                    // TODO
                }
            }
        }
    }
}