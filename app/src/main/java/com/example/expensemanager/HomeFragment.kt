package com.example.expensemanager

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.DateFormat
import java.util.Date


class HomeFragment : Fragment() {

    //Floating button
    private lateinit var fabMainBtn: FloatingActionButton
    private lateinit var fabIncomeBtn: FloatingActionButton
    private lateinit var fabExpenseBtn: FloatingActionButton

    //Floating button textview
    private lateinit var fabIncomeTxt: TextView
    private lateinit var fabExpenseTxt: TextView

    //Flag
    private var isOpen: Boolean = false

    //Animation
    private lateinit var fadeOpen: Animation
    private lateinit var fadeClose: Animation

    //Firebase
    private lateinit var auth : FirebaseAuth
    private lateinit var incomeDatabase : DatabaseReference
    private lateinit var expenseDatabase : DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val myView : View = inflater.inflate(R.layout.fragment_home, container,false)
        auth = FirebaseAuth.getInstance()
        val user : FirebaseUser = auth.currentUser!!
        val uid:String = user.uid
        incomeDatabase =FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid)
        expenseDatabase =FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid)

        insertArrayIntoDropdown()
        //Connect floating button to layout
        fabMainBtn = myView.findViewById(R.id.fb_main_plus_btn)
        fabIncomeBtn =myView.findViewById(R.id.income_Ft_btn)
        fabExpenseBtn =myView.findViewById(R.id.expense_Ft_btn)

        //Connect floating text to layout
        fabIncomeTxt = myView.findViewById(R.id.income_ft_text)
        fabExpenseTxt = myView.findViewById(R.id.expense_ft_text)

        //Animation
        fadeOpen = AnimationUtils.loadAnimation(activity, R.anim.fade_open)
        fadeClose = AnimationUtils.loadAnimation(activity, R.anim.fade_close)

        fabMainBtn.setOnClickListener {

            addData()

            if (isOpen){
                fabIncomeBtn.startAnimation(fadeClose)
                fabExpenseBtn.startAnimation(fadeClose)
                fabIncomeBtn.isClickable = false
                fabExpenseBtn.isClickable = false

                fabIncomeTxt.startAnimation(fadeClose)
                fabExpenseTxt.startAnimation(fadeClose)
                fabIncomeTxt.isClickable = false
                fabExpenseTxt.isClickable = false
                isOpen = false
            } else{
                fabIncomeBtn.startAnimation(fadeOpen)
                fabExpenseBtn.startAnimation(fadeOpen)
                fabIncomeBtn.isClickable = true
                fabExpenseBtn.isClickable = true

                fabIncomeTxt.startAnimation(fadeOpen)
                fabExpenseTxt.startAnimation(fadeOpen)
                fabIncomeTxt.isClickable = true
                fabExpenseTxt.isClickable = true
                isOpen = true
            }
        }
        return  myView
    }
    private fun ftAnimation(){
        if (isOpen){
            fabIncomeBtn.startAnimation(fadeClose)
            fabExpenseBtn.startAnimation(fadeClose)
            fabIncomeBtn.isClickable = false
            fabExpenseBtn.isClickable = false

            fabIncomeTxt.startAnimation(fadeClose)
            fabExpenseTxt.startAnimation(fadeClose)
            fabIncomeTxt.isClickable = false
            fabExpenseTxt.isClickable = false
            isOpen = false
        } else{
            fabIncomeBtn.startAnimation(fadeOpen)
            fabExpenseBtn.startAnimation(fadeOpen)
            fabIncomeBtn.isClickable = true
            fabExpenseBtn.isClickable = true

            fabIncomeTxt.startAnimation(fadeOpen)
            fabExpenseTxt.startAnimation(fadeOpen)
            fabIncomeTxt.isClickable = true
            fabExpenseTxt.isClickable = true
            isOpen = true
        }
    }

    private fun addData(){
/*        */
        //Fab button income

        fabIncomeBtn.setOnClickListener {
            incomeDataInsert()
            ftAnimation()
        }
        fabExpenseBtn.setOnClickListener {
            expenseDataInsert()
            ftAnimation()
        }
    }

    private fun insertArrayIntoDropdown(){
        val inflater = LayoutInflater.from(activity) ?: return // Verify that activity is not null
        val myView = inflater.inflate(R.layout.custom_layout_for_insertdata, null)

        val list = mutableListOf<String>()
        list.addAll(listOf("House", "Food", "Entertainment", "Personal expenses", "Health care", "Transportation", "Debt / Student Loan"))

        val stuff = myView.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        val arrayAdapter = ArrayAdapter(myView.context, R.layout.dropdown_item, list)

        stuff.setAdapter(arrayAdapter)
    }
    private fun incomeDataInsert(){
        //Create Dialog
        val myDialog =  (AlertDialog.Builder(activity))
        val inflater = LayoutInflater.from(activity)
        val myView = inflater.inflate(R.layout.custom_layout_for_insertdata,null)
        myDialog.setView(myView)
        val dialog:AlertDialog = myDialog.create()
        dialog.setCancelable(false)

        //Get id's from view
        val editAmount = myView.findViewById<EditText>(R.id.amount_edt)
        val editNote = myView.findViewById<EditText>(R.id.note_edt)

        val typeOfIncome = myView.findViewById<TextInputLayout>(R.id.textInputLayout)
        val selectedValue: Editable? = (typeOfIncome.editText as AutoCompleteTextView).text

        val btnCancel = myView.findViewById<Button>(R.id.btn_cancel)
        val btnSave = myView.findViewById<Button>(R.id.btn_save)

        dialog.show()

        btnSave.setOnClickListener {
            val typeStr :String = selectedValue.toString()
            val amountStr = editAmount.text.toString().trim()
            val noteStr = editNote.text.toString().trim()
            if (TextUtils.isEmpty(typeStr)){
                typeOfIncome.setError("Required field...")
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(amountStr)){
                editAmount.error = "Required field..."
                return@setOnClickListener
            }
            val ourAmount : Int = Integer.parseInt(amountStr)

            if (TextUtils.isEmpty(noteStr)){
                editNote.error = "Required field..."
                return@setOnClickListener
            }
            val id : String = incomeDatabase.push().key!!
            val mDate : String = DateFormat.getDateInstance().format(Date())
            val data = Data(ourAmount, typeStr, noteStr, id, mDate)

            incomeDatabase.child(id).setValue(data)
            Toast.makeText(activity, "Data added", Toast.LENGTH_SHORT).show()

            dialog.dismiss()
        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

    }
    private fun expenseDataInsert(){
        val myDialog =  (AlertDialog.Builder(activity))
        val inflater = LayoutInflater.from(activity)
        val myView = inflater.inflate(R.layout.custom_layout_for_insertdata,null)
        myDialog.setView(myView)
        val dialog:AlertDialog = myDialog.create()
        dialog.setCancelable(false)

        //Get id's from view
        val editAmount = myView.findViewById<EditText>(R.id.amount_edt)
        val editNote = myView.findViewById<EditText>(R.id.note_edt)

        val typeOfIncome = myView.findViewById<TextInputLayout>(R.id.textInputLayout)
        val selectedValue: Editable? = (typeOfIncome.editText as AutoCompleteTextView).text

        val btnCancel = myView.findViewById<Button>(R.id.btn_cancel)
        val btnSave = myView.findViewById<Button>(R.id.btn_save)

        dialog.show()

        btnSave.setOnClickListener {
            val typeStr :String = selectedValue.toString()
            val amountStr = editAmount.text.toString().trim()
            val noteStr = editNote.text.toString().trim()
            if (TextUtils.isEmpty(typeStr)){
                typeOfIncome.setError("Required field...")
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(amountStr)){
                editAmount.error = "Required field..."
                return@setOnClickListener
            }
            val ourAmount : Int = Integer.parseInt(amountStr)

            if (TextUtils.isEmpty(noteStr)){
                editNote.error = "Required field..."
                return@setOnClickListener
            }
            val id : String = expenseDatabase.push().key!!
            val mDate : String = DateFormat.getDateInstance().format(Date())
            val data = Data(ourAmount, typeStr, noteStr, id, mDate)

            expenseDatabase.child(id).setValue(data)
            Toast.makeText(activity, "Data added", Toast.LENGTH_SHORT).show()

            dialog.dismiss()
        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
    }
}

