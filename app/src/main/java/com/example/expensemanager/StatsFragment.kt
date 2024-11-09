package com.example.expensemanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

class StatsFragment : Fragment() {
    //Firebase
    private lateinit var auth : FirebaseAuth

    //RecyclerViews
    private lateinit var recyclerViewIncome: RecyclerView
    private lateinit var recyclerViewExpense: RecyclerView
    private lateinit var incomeQuery: Query
    private lateinit var expenseQuery: Query

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myView : View = inflater.inflate(R.layout.fragment_stats, container,false)
        auth = FirebaseAuth.getInstance()
        val user : FirebaseUser = auth.currentUser!!
        val uid:String = user.uid
        incomeQuery = FirebaseDatabase.getInstance()
            .reference
            .child("IncomeData")
            .limitToLast(50)
        expenseQuery = FirebaseDatabase.getInstance()
            .reference
            .child("ExpenseData")
            .child(uid)
            .limitToLast(50)

        //Income data
        recyclerViewIncome = myView.findViewById(R.id.recycler_id_income)
        var incomeLinearLayoutManager = LinearLayoutManager(activity)
        incomeLinearLayoutManager.reverseLayout = true
        incomeLinearLayoutManager.stackFromEnd = true

        recyclerViewIncome.setHasFixedSize(true)
        recyclerViewIncome.layoutManager = incomeLinearLayoutManager

        //Expense data
        recyclerViewExpense = myView.findViewById(R.id.recycler_id_expense)
        var expenseLinearLayoutManager = LinearLayoutManager(activity)
        expenseLinearLayoutManager.reverseLayout = true
        expenseLinearLayoutManager.stackFromEnd = true

        recyclerViewExpense.setHasFixedSize(true)
        recyclerViewExpense.layoutManager = expenseLinearLayoutManager
        return myView

    }

    override fun onStart() {
        super.onStart()
        //FirebaseRecyclerAdapter
        val incomeHolder : MyViewHolder = MyViewHolder(recyclerViewIncome, "income")
        val incomeEventListener: ChildEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        incomeQuery.addChildEventListener(incomeEventListener)
        val expenseEventListener: ChildEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        expenseQuery.addChildEventListener(expenseEventListener)
    }

    class MyViewHolder : RecyclerView.ViewHolder {

        private lateinit var mView: View
        private var str: String

        constructor(itemView: View, s: String) : super(itemView){
            str = s
        }

        fun setType(type: String){
            var mType : TextView
            if (str == "income"){
                mType = mView.findViewById(R.id.type_text_income)
                mType.text = type
            } else if (str == "expense"){
                mType = mView.findViewById(R.id.type_text_expense)
                mType.text = type
            }
        }
        fun setNote(note: String){
            var mNote : TextView
            if (str == "income"){
                mNote = mView.findViewById(R.id.note_text_income)
                mNote.text = note
            } else if (str == "expense"){
                mNote = mView.findViewById(R.id.note_text_expense)
                mNote.text = note
            }
        }
        fun setDate(date: String){
            var mDate : TextView
            if (str == "income"){
                mDate = mView.findViewById(R.id.date_text_income)
                mDate.text = date
            } else if (str == "expense"){
                mDate = mView.findViewById(R.id.date_text_expense)
                mDate.text = date
            }
        }
        fun setAmount(amount: Int){
            var mAmount : TextView
            val stAmount: String = amount.toString()
            if (str == "income"){
                mAmount = mView.findViewById(R.id.amount_text_income)
                mAmount.text = stAmount
            } else if (str == "expense"){
                mAmount = mView.findViewById(R.id.amount_text_expense)
                mAmount.text = stAmount
            }
        }
    }
}