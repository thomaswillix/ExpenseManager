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
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.expensemanager.databinding.FragmentHomeBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.DateFormat
import java.util.Date


class HomeFragment : Fragment() {

    //Flag
    private var isOpen: Boolean = false

    //Animation
    private lateinit var fadeOpen: Animation
    private lateinit var fadeClose: Animation

    //Firebase
    private lateinit var auth : FirebaseAuth
    private lateinit var incomeDatabase : DatabaseReference
    private lateinit var expenseDatabase : DatabaseReference

    //Binding
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        val myView : View = binding.root
        auth = FirebaseAuth.getInstance()
        val user : FirebaseUser = auth.currentUser!!
        val uid:String = user.uid
        incomeDatabase =FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid)
        expenseDatabase =FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid)

        insertArrayIntoDropdown()
        //Animation
        fadeOpen = AnimationUtils.loadAnimation(activity, R.anim.fade_open)
        fadeClose = AnimationUtils.loadAnimation(activity, R.anim.fade_close)

        binding.fabMainBtn.setOnClickListener {

            addData()

            if (isOpen){
                binding.fabIncomeBtn.startAnimation(fadeClose)
                binding.fabExpenseBtn.startAnimation(fadeClose)
                binding.fabIncomeBtn.isClickable = false
                binding.fabExpenseBtn.isClickable = false

                binding.tvIncome.startAnimation(fadeClose)
                binding.tvExpense.startAnimation(fadeClose)
                binding.tvIncome.isClickable = false
                binding.tvExpense.isClickable = false
                isOpen = false
            } else{
                binding.fabIncomeBtn.startAnimation(fadeOpen)
                binding.fabExpenseBtn.startAnimation(fadeOpen)
                binding.fabIncomeBtn.isClickable = true
                binding.fabExpenseBtn.isClickable = true

                binding.tvIncome.startAnimation(fadeOpen)
                binding.tvExpense.startAnimation(fadeOpen)
                binding.tvIncome.isClickable = true
                binding.tvExpense.isClickable = true
                isOpen = true
            }
        }
        return  myView
    }
    private fun ftAnimation(){
        if (isOpen){
            binding.fabIncomeBtn.startAnimation(fadeClose)
            binding.fabExpenseBtn.startAnimation(fadeClose)
            binding.fabIncomeBtn.isClickable = false
            binding.fabExpenseBtn.isClickable = false

            binding.tvIncome.startAnimation(fadeClose)
            binding.tvExpense.startAnimation(fadeClose)
            binding.tvIncome.isClickable = false
            binding.tvExpense.isClickable = false
            isOpen = false
        } else{
            binding.fabIncomeBtn.startAnimation(fadeOpen)
            binding.fabExpenseBtn.startAnimation(fadeOpen)
            binding.fabIncomeBtn.isClickable = true
            binding.fabExpenseBtn.isClickable = true

            binding.tvIncome.startAnimation(fadeOpen)
            binding.tvExpense.startAnimation(fadeOpen)
            binding.tvIncome.isClickable = true
            binding.tvExpense.isClickable = true
            isOpen = true
        }
    }

    private fun addData(){
/*        */
        //Fab button income

        binding.fabIncomeBtn.setOnClickListener {
            incomeDataInsert()
            ftAnimation()
        }
        binding.fabExpenseBtn.setOnClickListener {
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
        val editAmount = myView.findViewById<EditText>(R.id.amountEdt)
        val editNote = myView.findViewById<EditText>(R.id.noteEdt)

        val typeOfIncome = myView.findViewById<TextInputLayout>(R.id.textInputLayout)
        val selectedValue: Editable? = (typeOfIncome.editText as AutoCompleteTextView).text

        val btnCancel = myView.findViewById<Button>(R.id.btnCancelData)
        val btnSave = myView.findViewById<Button>(R.id.btnSave)

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
        //TODO: Change layout and create a new one for expenses
        val myView = inflater.inflate(R.layout.custom_layout_for_insertdata,null)
        myDialog.setView(myView)
        val dialog:AlertDialog = myDialog.create()
        dialog.setCancelable(false)

        //Get id's from view
        val editAmount = myView.findViewById<EditText>(R.id.amountEdt)
        val editNote = myView.findViewById<EditText>(R.id.noteEdt)

        val typeOfIncome = myView.findViewById<TextInputLayout>(R.id.textInputLayout)
        val selectedValue: Editable? = (typeOfIncome.editText as AutoCompleteTextView).text

        val btnCancel = myView.findViewById<Button>(R.id.btnCancelData)
        val btnSave = myView.findViewById<Button>(R.id.btnSave)

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

