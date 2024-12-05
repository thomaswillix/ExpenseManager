package com.example.expensemanager

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
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
import androidx.fragment.app.FragmentActivity
import com.example.expensemanager.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

    private fun toggleVisibility(buttons: List<View>, textViews: List<View>,
        fadeAnimation: Animation, isClickable: Boolean) {
        // Aplicar la animación y clicabilidad
        buttons.forEach { button ->
            button.startAnimation(fadeAnimation)
            button.isClickable = isClickable
        }

        textViews.forEach { textView ->
            textView.startAnimation(fadeAnimation)
            textView.isClickable = isClickable
        }
    }

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
        //Animation
        fadeOpen = AnimationUtils.loadAnimation(activity, R.anim.fade_open)
        fadeClose = AnimationUtils.loadAnimation(activity, R.anim.fade_close)

        if (isAdded) {
            // Aquí puedes interactuar con la actividad, por ejemplo:
            val activity = requireActivity() // Esto no lanzará una excepción si el fragmento está adjunto
            // User Liste
            val transactionListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    showData(dataSnapshot, activity)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w("loadPost:onCancelled", databaseError.toException())
                }
            }
            incomeDatabase.addValueEventListener(transactionListener)
            expenseDatabase.addValueEventListener(transactionListener)
        }
        binding.fabMainBtn.setOnClickListener {

            addData()

            if (isOpen){
                toggleVisibility(
                    buttons = listOf(binding.fabIncomeBtn, binding.fabExpenseBtn), textViews = listOf(binding.tvIncome, binding.tvExpense),
                    fadeAnimation = fadeClose, isClickable = false
                )
                isOpen = false
            } else{
                // Si está cerrado, aplicar animaciones de apertura
                toggleVisibility(
                    buttons = listOf(binding.fabIncomeBtn, binding.fabExpenseBtn), textViews = listOf(binding.tvIncome, binding.tvExpense),
                    fadeAnimation = fadeOpen, isClickable = true
                )
                isOpen = true
            }
        }
        return  myView
    }

    private fun showData(dataSnapshot: DataSnapshot, activity: FragmentActivity) {
        val values = arrayListOf<Data>()

        for (ds in dataSnapshot.children){
            val data = ds.getValue(Data::class.java)
            // Verifica si 'data' no es null antes de acceder a sus propiedades
            if (data != null) {
                values.add(data)
                Log.w("Show data", "Id: ${data.id}, Amount: ${data.amount}, Type: ${data.type}")
            } else {
                Log.e("Show data", "Data is null for child: ${ds.key}")
            }
        }
        val listAdapter = ListAdapter(activity, R.layout.list_item, values)
        binding.listTransactios.adapter = listAdapter
        listAdapter.notifyDataSetChanged() // Notifica que los datos han cambiado

    }

    private fun ftAnimation(){
        if (isOpen) {
            // Animaciones de cierre
            toggleVisibility(
                buttons = listOf(binding.fabIncomeBtn, binding.fabExpenseBtn), textViews = listOf(binding.tvIncome, binding.tvExpense),
                fadeAnimation = fadeClose, isClickable = false
            )
            isOpen = false
        } else {
            // Animaciones de apertura
            toggleVisibility(
                buttons = listOf(binding.fabIncomeBtn, binding.fabExpenseBtn), textViews = listOf(binding.tvIncome, binding.tvExpense),
                fadeAnimation = fadeOpen, isClickable = true
            )
            isOpen = true
        }
    }

    private fun addData(){
        binding.fabIncomeBtn.setOnClickListener {
            incomeDataInsert()
            ftAnimation()
        }
        binding.fabExpenseBtn.setOnClickListener {
            expenseDataInsert()
            ftAnimation()
        }
    }

    private fun incomeDataInsert(){
        //Create Dialog
        val myDialog =  (AlertDialog.Builder(activity))
        val myView = layoutInflater.inflate(R.layout.custom_layout_for_insertdata,null)
        myDialog.setView(myView)
        val dialog:AlertDialog = myDialog.create()
        dialog.setCancelable(false)

        // Insert array
        val list = mutableListOf<String>()
        list.addAll(listOf("Payckeck", "Intellectual Propperty", "Stocks", "Business", "Savings, bonds or lending"
            , "others")
        )

        val autoCompleteTextView = myView.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        val arrayAdapter = ArrayAdapter<String>(myView.context, R.layout.dropdown_item, list)

        var type = ""
        autoCompleteTextView.setAdapter(arrayAdapter)
        autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val item : String = parent.getItemAtPosition(position).toString()
            toastMessage(item)
            type = item
        }
        // Get id's from view
        val editAmount = myView.findViewById<EditText>(R.id.amountEdt)
        val editNote = myView.findViewById<EditText>(R.id.noteEdt)

        val btnCancel = myView.findViewById<Button>(R.id.btnCancelData)
        val btnSave = myView.findViewById<Button>(R.id.btnSave)

        dialog.show()

        btnSave.setOnClickListener {
            val amountStr = editAmount.text.toString().trim()
            val noteStr = editNote.text.toString().trim()
            if (TextUtils.isEmpty(type)){
                autoCompleteTextView.setError("Required field...")
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(amountStr)){
                editAmount.error = "Required field..."
                return@setOnClickListener
            }
            val ourAmount : Double = amountStr.toDouble()

            if (TextUtils.isEmpty(noteStr)){
                editNote.error = "Required field..."
                return@setOnClickListener
            }
            val id : String = incomeDatabase.push().key!!
            val mDate : String = DateFormat.getDateInstance().format(Date())
            val data = Data(ourAmount, type, noteStr, id, mDate)

            incomeDatabase.child(id).setValue(data)
            toastMessage("Data added")

            dialog.dismiss()
        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

    }

    private fun expenseDataInsert(){
        val myDialog =  (AlertDialog.Builder(activity))
        //TODO: Change layout and create a new one for expenses
        val myView = layoutInflater.inflate(R.layout.custom_layout_for_insertdata,null)
        myDialog.setView(myView)
        val dialog:AlertDialog = myDialog.create()
        dialog.setCancelable(false)

        // Insert array
        val list = mutableListOf<String>()
        list.addAll(listOf("House", "Food", "Entertainment", "Personal expenses", "Health care",
            "Transportation", "Debt / Student Loan", "others")
        )

        val autoCompleteTextView = myView.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        val arrayAdapter = ArrayAdapter<String>(myView.context, R.layout.dropdown_item, list)

        autoCompleteTextView.setAdapter(arrayAdapter)

        var type = ""
        autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val item : String = parent.getItemAtPosition(position).toString()
            toastMessage(item)
            type = item
        }
        //Get id's from view
        val editAmount = myView.findViewById<EditText>(R.id.amountEdt)
        val editNote = myView.findViewById<EditText>(R.id.noteEdt)

        val btnCancel = myView.findViewById<Button>(R.id.btnCancelData)
        val btnSave = myView.findViewById<Button>(R.id.btnSave)

        dialog.show()

        btnSave.setOnClickListener {
            val amountStr = editAmount.text.toString().trim()
            val noteStr = editNote.text.toString().trim()
            if (TextUtils.isEmpty(type)){
                autoCompleteTextView.setError("Required field...")
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(amountStr)){
                editAmount.error = "Required field..."
                return@setOnClickListener
            } else if (!TextUtils.isDigitsOnly(amountStr)){
                editAmount.error = "Only numeric numbers"
                return@setOnClickListener
            }
            val ourAmount : Double = amountStr.toDouble()

            if (TextUtils.isEmpty(noteStr)){
                editNote.error = "Required field..."
                return@setOnClickListener
            }
            val id : String = expenseDatabase.push().key!!
            val mDate : String = DateFormat.getDateInstance().format(Date())
            val data = Data(ourAmount, type, noteStr, id, mDate)

            expenseDatabase.child(id).setValue(data)
            toastMessage("Data added")

            dialog.dismiss()
        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun toastMessage(message:String){
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}

