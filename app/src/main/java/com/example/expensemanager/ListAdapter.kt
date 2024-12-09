package com.example.expensemanager

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.text.DateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class ListAdapter(context: Context, resource: Int, objects: MutableList<Data>) :
    ArrayAdapter<Data>(context, resource, objects) {
    @SuppressLint("ResourceAsColor")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val data : Data? = getItem(position)
        val view: View = convertView ?: LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)

        val icon = view.findViewById<ImageView>(R.id.icon)
        val type = view.findViewById<TextView>(R.id.category)
        val quantity = view.findViewById<TextView>(R.id.quantity)
        val note = view.findViewById<TextView>(R.id.note)
        val date = view.findViewById<TextView>(R.id.date)

        if (data != null) {
            type.text = data.type
            // Suponiendo que 'data.date' es un String con formato de fecha como "d MMM. yyyy"
            val formatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale("en"))
            val parsedDate = LocalDate.parse(data.date, formatter)

            // Obtén la fecha actual como LocalDate
            val today = LocalDate.now()

            // Compara las fechas
            if (parsedDate.isEqual(today)) {
                date.text = "Today"
            } else {
                date.text = data.date
            }

            quantity.text = data.amount.toString()
            note.text = data.note
            when(data.type){
                "Food" -> {
                    icon!!.setImageResource(R.drawable.food)
                    quantity.setTextColor(ContextCompat.getColor(context, R.color.totalExpenses))
                }
                "House"-> {
                    icon!!.setImageResource(R.drawable.house)
                    quantity.setTextColor(ContextCompat.getColor(context, R.color.totalExpenses))
                }
                "Entertainment"->{
                    icon!!.setImageResource(R.drawable.entertainment)
                    quantity.setTextColor(ContextCompat.getColor(context, R.color.totalExpenses))
                }
                "Personal expenses"->{
                    // TODO
                    quantity.setTextColor(ContextCompat.getColor(context, R.color.totalExpenses))
                }
                "Health care"->{
                    icon!!.setImageResource(R.drawable.healthcare)
                    quantity.setTextColor(ContextCompat.getColor(context, R.color.totalExpenses))
                }
                "Transportation"->{
                    icon!!.setImageResource(R.drawable.transport)
                    quantity.setTextColor(ContextCompat.getColor(context, R.color.totalExpenses))
                }
                "Debt / Student Loan"->{
                    // TODO
                    quantity.setTextColor(ContextCompat.getColor(context, R.color.income))
                }
                "Payckeck"->{
                    // TODO
                    quantity.setTextColor(ContextCompat.getColor(context, R.color.income))
                }
                "Intellectual Propperty"->{
                    // TODO
                    quantity.setTextColor(ContextCompat.getColor(context, R.color.income))
                }
                "Stocks"->{
                    // TODO
                    quantity.setTextColor(ContextCompat.getColor(context, R.color.income))
                }
                "Business"->{
                    // TODO
                    quantity.setTextColor(ContextCompat.getColor(context, R.color.income))
                }
                "Savings, bonds or lending"->{
                    // TODO
                    quantity.setTextColor(ContextCompat.getColor(context, R.color.income))
                }
                "others"->{
                    // TODO
                    quantity.setTextColor(ContextCompat.getColor(context, R.color.income))
                }
            }
        }
        return view
    }
}