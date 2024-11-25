package com.example.expensemanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class StatsFragment : Fragment() {
    //Firebase
    private lateinit var auth : FirebaseAuth

    //RecyclerViews
    private lateinit var recyclerViewIncome: RecyclerView
    private lateinit var recyclerViewExpense: RecyclerView

    private lateinit var incomeDatabase : DatabaseReference
    private lateinit var expenseDatabase : DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val myView : View = inflater.inflate(R.layout.fragment_stats, container,false)

        auth = FirebaseAuth.getInstance()
        val user : FirebaseUser = auth.currentUser!!
        val uid:String = user.uid
        incomeDatabase =FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid)
        expenseDatabase =FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid)



        //Income data
        recyclerViewIncome = myView.findViewById(R.id.recycler_id_income)

        //Expense data
        recyclerViewExpense = myView.findViewById(R.id.recycler_id_expense)

        return myView

    }
}