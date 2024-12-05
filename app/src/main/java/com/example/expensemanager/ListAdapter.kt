package com.example.expensemanager

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class ListAdapter(context: Context, resource: Int, objects: MutableList<Data>) :
    ArrayAdapter<Data>(context, resource, objects) {
    @SuppressLint("ResourceAsColor")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val data : Data? = getItem(position)
        var view: View? = convertView

        if (view == null) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        }
        val icon = view?.findViewById<ImageView>(R.id.icon)
        val type = view?.findViewById<TextView>(R.id.category)
        val quantity = view?.findViewById<TextView>(R.id.quantity)
        val date = view?.findViewById<TextView>(R.id.date)
        type!!.text = data!!.type
        date!!.text = data.date
        quantity!!.text = data.amount.toString()
        when(data.type){
            "Food" -> {
                icon!!.setImageResource(R.drawable.food)
                quantity.setTextColor(R.color.totalExpenses)
            }
            "House"-> {
                icon!!.setImageResource(R.drawable.house)
                quantity.setTextColor(R.color.totalExpenses)
            }
            "Entertainment"->{
                icon!!.setImageResource(R.drawable.entertainment)
                quantity.setTextColor(R.color.totalExpenses)
            }
            "Personal expenses"->{
            // TODO
            }
            "Health care"->{
                icon!!.setImageResource(R.drawable.healthcare)
                quantity.setTextColor(R.color.totalExpenses)
            }
            "Transportation"->{
                icon!!.setImageResource(R.drawable.transport)
                quantity.setTextColor(R.color.totalExpenses)
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
        return view!!
    }
}