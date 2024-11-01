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
import com.example.expensemanager.Model.Data
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
    private lateinit var fab_main_btn: FloatingActionButton
    private lateinit var fab_income_btn: FloatingActionButton
    private lateinit var fab_expense_btn: FloatingActionButton

    //Floating button textview
    private lateinit var fab_income_txt: TextView
    private lateinit var fab_expense_txt: TextView

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
    ): View? {
        val myView : View = inflater.inflate(R.layout.fragment_home, container,false)
        auth = FirebaseAuth.getInstance()
        val user : FirebaseUser = auth.currentUser!!
        val uid:String = user.uid
        incomeDatabase =FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid)
        expenseDatabase =FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid)

        insertArrayIntoDropdown()
        //Connect floating button to layout
        fab_main_btn = myView.findViewById(R.id.fb_main_plus_btn)
        fab_income_btn =myView.findViewById(R.id.income_Ft_btn)
        fab_expense_btn =myView.findViewById(R.id.expense_Ft_btn)

        //Connect floating text to layout
        fab_income_txt = myView.findViewById(R.id.income_ft_text)
        fab_expense_txt = myView.findViewById(R.id.expense_ft_text)

        //Animation
        fadeOpen = AnimationUtils.loadAnimation(activity, R.anim.fade_open)
        fadeClose = AnimationUtils.loadAnimation(activity, R.anim.fade_close)

        fab_main_btn.setOnClickListener {

            addData()

            if (isOpen){
                fab_income_btn.startAnimation(fadeClose)
                fab_expense_btn.startAnimation(fadeClose)
                fab_income_btn.isClickable = false
                fab_expense_btn.isClickable = false

                fab_income_txt.startAnimation(fadeClose)
                fab_expense_txt.startAnimation(fadeClose)
                fab_income_txt.isClickable = false
                fab_expense_txt.isClickable = false
                isOpen = false
            } else{
                fab_income_btn.startAnimation(fadeOpen)
                fab_expense_btn.startAnimation(fadeOpen)
                fab_income_btn.isClickable = true
                fab_expense_btn.isClickable = true

                fab_income_txt.startAnimation(fadeOpen)
                fab_expense_txt.startAnimation(fadeOpen)
                fab_income_txt.isClickable = true
                fab_expense_txt.isClickable = true
                isOpen = true
            }
        }
        return  myView
    }

    private fun addData(){
/*        */
        //Fab button income

        fab_income_btn.setOnClickListener {
            incomeDataInsert()
        }
        fab_expense_btn.setOnClickListener {

        }
    }

    private fun insertArrayIntoDropdown(){
        //Create Dialog
        val inflater = LayoutInflater.from(activity)
        val myViewm = inflater.inflate(R.layout.custom_layout_for_insertdata,null)

        //Insert array of types in the dropdown
        val types = resources.getStringArray(R.array.types)
        val stuff = myViewm.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        val arrayAdapter = ArrayAdapter(myViewm.context, R.layout.dropdown_item, types)

        stuff.setAdapter(arrayAdapter)
    }
    private fun incomeDataInsert(){
        //Create Dialog
        val myDialog =  (AlertDialog.Builder(activity))
        val inflater = LayoutInflater.from(activity)
        val myViewm = inflater.inflate(R.layout.custom_layout_for_insertdata,null)
        myDialog.setView(myViewm)
        val dialog:AlertDialog = myDialog.create()

        //Get id's from view
        val editAmount = myViewm.findViewById<EditText>(R.id.amount_edt)
        val editNote = myViewm.findViewById<EditText>(R.id.note_edt)

        val typeOfIncome = myViewm.findViewById<TextInputLayout>(R.id.textInputLayout)
        val selectedValue: Editable? = (typeOfIncome.editText as AutoCompleteTextView).text

        val btnCancel = myViewm.findViewById<Button>(R.id.btn_cancel)
        val btnSave = myViewm.findViewById<Button>(R.id.btn_save)

        dialog.show()

        btnSave.setOnClickListener {
            val typeStr :String = selectedValue.toString()
            val amountStr = editAmount.text.toString()
            val noteStr = editNote.text.toString()
            if (TextUtils.isEmpty(typeStr)){
                typeOfIncome.setError("Required field...")
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(amountStr)){
                editAmount.setError("Required field...")
                return@setOnClickListener
            }
            val ourAmount : Int = Integer.parseInt(amountStr)

            if (TextUtils.isEmpty(noteStr)){
                editNote.setError("Required field...")
                return@setOnClickListener
            }
            val id : String = incomeDatabase.push().key!!
            val mDate : String = DateFormat.getDateInstance().format(Date())
            val data : Data = Data(ourAmount, typeStr, noteStr, id, mDate)

            incomeDatabase.child(id).setValue(data)
            Toast.makeText(activity, "Data added", Toast.LENGTH_SHORT).show()

            dialog.dismiss()
        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

    }
}

