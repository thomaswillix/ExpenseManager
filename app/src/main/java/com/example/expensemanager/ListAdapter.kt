package com.example.expensemanager

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.example.expensemanager.databinding.ListItemBinding
import java.time.LocalDateTime
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

            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM yyyy HH:mm:ss", Locale("en"))
            val parsedDate = LocalDateTime.parse(item.date, formatter)
            val today = LocalDateTime.now()

            binding.date.text = if (parsedDate.toLocalDate().isEqual(today.toLocalDate())) "Today" else item.date

            when (item.type) {
                "Food" -> {
                    //icon by freepik
                    binding.icon.setImageResource(R.drawable.food)
                    binding.quantity.setTextColor(ContextCompat.getColor(context, R.color.expense))
                }
                "House" -> {
                    // icon by imaginationlol
                    binding.icon.setImageResource(R.drawable.house)
                    binding.quantity.setTextColor(ContextCompat.getColor(context, R.color.expense))
                }
                "Entertainment" -> {
                    // icon by https://www.flaticon.com/authors/wanicon
                    binding.icon.setImageResource(R.drawable.entertainment)
                    binding.quantity.setTextColor(ContextCompat.getColor(context, R.color.expense))
                }
                "Personal expenses" -> {
                    //icon by freepik
                    binding.icon.setImageResource(R.drawable.personal_expenses)
                    binding.quantity.setTextColor(ContextCompat.getColor(context, R.color.expense))
                }
                "Health care" -> {
                    //icon by freepik
                    binding.icon.setImageResource(R.drawable.healthcare)
                    binding.quantity.setTextColor(ContextCompat.getColor(context, R.color.expense))
                }
                "Transportation" -> {
                    //icon by freepik
                    binding.icon.setImageResource(R.drawable.transport)
                    binding.quantity.setTextColor(ContextCompat.getColor(context, R.color.expense))
                }
                "Debt / Student Loan"->{
                    //icon by https://www.flaticon.com/authors/afian-rochmah-afif
                    binding.icon.setImageResource(R.drawable.debt)
                    binding.quantity.setTextColor(ContextCompat.getColor(context, R.color.expense))
                }
                "others (expense)" -> {
                    //icon by https://www.flaticon.com/authors/surang
                    binding.icon.setImageResource(R.drawable.others_expense)
                    binding.quantity.setTextColor(ContextCompat.getColor(context, R.color.expense))
                }
                "Payckeck"->{
                    //icon by https://www.flaticon.com/authors/juicy-fish
                    binding.icon.setImageResource(R.drawable.paycheck)
                    binding.quantity.setTextColor(ContextCompat.getColor(context, R.color.income))
                }
                "Intellectual Propperty"->{
                    //icon by freepik https://www.flaticon.com/free-icons/intellectual-property
                    binding.icon.setImageResource(R.drawable.intellectual_property)
                    binding.quantity.setTextColor(ContextCompat.getColor(context, R.color.income))
                }
                "Stocks"->{
                    //icon by https://www.flaticon.com/authors/adriansyah
                    binding.icon.setImageResource(R.drawable.stocks)
                    binding.quantity.setTextColor(ContextCompat.getColor(context, R.color.income))
                }
                "Business"->{
                    //icon by https://www.flaticon.com/authors/pixel-perfect
                    binding.icon.setImageResource(R.drawable.business)
                    binding.quantity.setTextColor(ContextCompat.getColor(context, R.color.income))
                }
                "Savings, bonds or lending"->{
                    //icon by https://www.flaticon.com/authors/karyative
                    binding.icon.setImageResource(R.drawable.savings)
                    binding.quantity.setTextColor(ContextCompat.getColor(context, R.color.income))
                } "others (income)" -> {
                    //icon by freepik
                    binding.icon.setImageResource(R.drawable.others_income)
                    binding.quantity.setTextColor(ContextCompat.getColor(context, R.color.income))
                }
            }
        }
        return binding.root
    }

}
