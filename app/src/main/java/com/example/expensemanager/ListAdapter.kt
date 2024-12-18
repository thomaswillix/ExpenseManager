package com.example.expensemanager

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.example.expensemanager.databinding.ListItemBinding
import java.text.DateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class ListAdapter(
    context: Context,
    resource: Int,
    private val dataList: MutableList<Data> // Guarda la lista en una propiedad
) : ArrayAdapter<Data>(context, resource, dataList) {

    fun getData(): MutableList<Data> {
        return dataList
    }

    @SuppressLint("ResourceAsColor")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val data: Data? = getItem(position)
        val binding = if (convertView == null) {
            // Inflar usando View Binding
            val inflater = LayoutInflater.from(parent.context)
            ListItemBinding.inflate(inflater, parent, false)
        } else {
            ListItemBinding.bind(convertView)
        }

        data?.let { item ->
            binding.category.text = item.type
            binding.quantity.text = item.amount.toString()
            binding.note.text = item.note

            val formatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale("en"))
            val parsedDate = LocalDate.parse(item.date, formatter)
            val today = LocalDate.now()

            binding.date.text = if (parsedDate.isEqual(today)) "Today" else item.date

            when (item.type) {
                "Food" -> {
                    binding.icon.setImageResource(R.drawable.food)
                    binding.quantity.setTextColor(ContextCompat.getColor(context, R.color.totalExpenses))
                }
                "House" -> {
                    binding.icon.setImageResource(R.drawable.house)
                    binding.quantity.setTextColor(ContextCompat.getColor(context, R.color.totalExpenses))
                }
                "Entertainment" -> {
                    binding.icon.setImageResource(R.drawable.entertainment)
                    binding.quantity.setTextColor(ContextCompat.getColor(context, R.color.totalExpenses))
                }
                "Personal expenses" -> {
                    binding.quantity.setTextColor(ContextCompat.getColor(context, R.color.totalExpenses))
                }
                "Health care" -> {
                    binding.icon.setImageResource(R.drawable.healthcare)
                    binding.quantity.setTextColor(ContextCompat.getColor(context, R.color.totalExpenses))
                }
                "Transportation" -> {
                    binding.icon.setImageResource(R.drawable.transport)
                    binding.quantity.setTextColor(ContextCompat.getColor(context, R.color.totalExpenses))
                }
                "Debt / Student Loan", "Payckeck", "Intellectual Propperty", "Stocks", "Business", "Savings, bonds or lending", "others" -> {
                    binding.quantity.setTextColor(ContextCompat.getColor(context, R.color.income))
                }
            }
        }
        return binding.root
    }

}
