package com.example.expensemanager

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.expensemanager.databinding.CustomLayoutForInsertdataBinding
import com.example.expensemanager.databinding.DialogConfirmDeleteBinding
import com.example.expensemanager.databinding.FragmentHomeBinding
import com.example.expensemanager.databinding.TransactionDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


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
    private val incomeValues = mutableListOf<Data>()
    private val expenseValues = mutableListOf<Data>()
    private val combinedValues = mutableListOf<Data>()
    // Formateador de fecha
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale("en"))

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
        incomeDatabase =FirebaseDatabase.getInstance().reference.child("IncomeData").child(uid)
        expenseDatabase =FirebaseDatabase.getInstance().reference.child("ExpenseData").child(uid)

        val incomeListener = object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                incomeValues.clear()
                binding.totalIncome.text = "0.00"
                for (ds in dataSnapshot.children) {
                    val data = ds.getValue(Data::class.java)
                    if (data != null) {
                        incomeValues.add(data)
                        val value : Double = binding.totalIncome.text.toString().toDouble() + data.amount
                        binding.totalIncome.text = value.toString()
                    }
                }
                val text : String
                text = "+" + binding.totalIncome.text
                binding.totalIncome.text = text
                // Actualizar lista combinada
                updateCombinedList(formatter)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("loadIncome:onCancelled", databaseError.toException())
            }
        }

        val expenseListener = object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                expenseValues.clear()
                binding.totalExpenses.text = "0.00"
                for (ds in dataSnapshot.children) {
                    val data = ds.getValue(Data::class.java)
                    if (data != null) {
                        expenseValues.add(data)
                        val value : Double = binding.totalExpenses.text.toString().toDouble() + data.amount
                        binding.totalExpenses.text = value.toString()
                    }
                }
                val text : String
                text = "-" + binding.totalExpenses.text
                binding.totalExpenses.text = text
                // Actualizar lista combinada
                updateCombinedList(formatter)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("loadExpense:onCancelled", databaseError.toException())
            }
        }

        // Vincula los listeners si el Fragment está añadido
        if (isAdded) {
            incomeDatabase.addValueEventListener(incomeListener)
            expenseDatabase.addValueEventListener(expenseListener)
        }
        //Detail view of a transaction
        binding.listCombined.setOnItemClickListener { parent, view, position, id ->
            transactioDetailView(parent, position)
        }
        //Animation
        fadeOpen = AnimationUtils.loadAnimation(activity, R.anim.fade_open)
        fadeClose = AnimationUtils.loadAnimation(activity, R.anim.fade_close)

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

    private fun transactioDetailView(parent: AdapterView<*>, position: Int) {
        val binding = TransactionDetailBinding.inflate(layoutInflater)
        val myDialog = AlertDialog.Builder(activity)
            .setView(binding.root)
            .create()
        myDialog.setCancelable(false)

        val selectedItem = parent.getItemAtPosition(position) as? Data

        selectedItem?.let { item ->
            binding.noteEditText.setText(item.note)
            binding.quantityEditText.setText(item.amount.toString())
        }

        binding.deleteButton.setOnClickListener {
            val confirmBinding = DialogConfirmDeleteBinding.inflate(layoutInflater)
            val confirmDialog = AlertDialog.Builder(activity)
                .setView(confirmBinding.root)
                .create()

            confirmBinding.btnYes.setOnClickListener {
                (parent.adapter as? BaseAdapter)?.let { adapter ->
                    if (adapter is ListAdapter) {
                        val data = adapter.getData()
                        data.removeAt(position)
                        adapter.notifyDataSetChanged()
                    }
                }
                confirmDialog.dismiss()
                myDialog.dismiss()
            }

            confirmBinding.btnNo.setOnClickListener {
                confirmDialog.dismiss()
            }

            confirmDialog.show()
        }

        binding.closeButton.setOnClickListener {
            myDialog.dismiss()
        }

        binding.trConfirm.setOnClickListener {
            selectedItem?.let { item ->
                item.note = binding.noteEditText.text.toString()
                item.amount = binding.quantityEditText.text.toString().toDoubleOrNull() ?: 0.0
            }
            (parent.adapter as? BaseAdapter)?.notifyDataSetChanged()
            myDialog.dismiss()
        }

        myDialog.show()
    }


    // Función para actualizar la lista combinada y ordenarla
    @SuppressLint("SetTextI18n")
    private fun updateCombinedList(formatter: DateTimeFormatter) {
        combinedValues.clear()
        combinedValues.addAll(incomeValues)
        combinedValues.addAll(expenseValues)
        val income  = binding.totalIncome.text.toString().toDouble()
        val expenses  = binding.totalExpenses.text.toString().toDouble()
        val balance = income + expenses
        if (balance > 0){
            binding.totalBalance.text = "+$balance €"
            context?.let { safeContext ->
                binding.totalBalance.setTextColor(ContextCompat.getColor(safeContext, R.color.income))
            }
        } else{
            binding.totalBalance.text = "$balance €"
            context?.let { safeContext ->
                binding.totalBalance.setTextColor(ContextCompat.getColor(safeContext, R.color.totalExpenses))
            }
        }
        if(combinedValues.isEmpty()){
            // TODO: SI ESTÁ VACÍO MOSTRAR UNA IMAGEN
        } else {
            // Ordenar la lista combinada por fecha
            combinedValues.sortByDescending { data ->
                LocalDate.parse(data.date, formatter) }
            // Verificar si el contexto está disponible
            context?.let { safeContext ->
                // Si el fragmento está adjunto, crear y asignar el adaptador
                val listAdapter = ListAdapter(safeContext, R.layout.list_item, combinedValues)
                binding.listCombined.adapter = listAdapter
                listAdapter.notifyDataSetChanged()
            }
        }
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

    private fun incomeDataInsert() {
        val myDialog = AlertDialog.Builder(activity)
        val binding = CustomLayoutForInsertdataBinding.inflate(layoutInflater) // Usar View Binding aquí
        myDialog.setView(binding.root)
        val dialog: AlertDialog = myDialog.create()
        dialog.setCancelable(false)

        val list = mutableListOf<String>()
        list.addAll(listOf("Payckeck", "Intellectual Propperty", "Stocks", "Business", "Savings, bonds or lending", "others"))

        val arrayAdapter = ArrayAdapter<String>(binding.root.context, R.layout.dropdown_item, list)

        var type = ""
        binding.autoCompleteTextView.setAdapter(arrayAdapter)
        binding.autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val item: String = parent.getItemAtPosition(position).toString()
            toastMessage(item)
            type = item
        }

        dialog.show()

        binding.btnSave.setOnClickListener {
            val amountStr = binding.amountEdt.text.toString().trim()
            val noteStr = binding.noteEdt.text.toString().trim()
            if (TextUtils.isEmpty(type)) {
                binding.autoCompleteTextView.error = "Required field..."
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(amountStr)) {
                binding.amountEdt.error = "Required field..."
                return@setOnClickListener
            }
            val ourAmount: Double = amountStr.toDouble()

            if (TextUtils.isEmpty(noteStr)) {
                binding.noteEdt.error = "Required field..."
                return@setOnClickListener
            }
            val id: String = incomeDatabase.push().key!!
            val mDate: String = LocalDate.now().format(formatter)
            val data = Data(ourAmount, type, noteStr, id, mDate)

            incomeDatabase.child(id).setValue(data)
            toastMessage("Data added")

            dialog.dismiss()
        }

        binding.btnCancelData.setOnClickListener {
            dialog.dismiss()
        }
    }


    private fun expenseDataInsert() {
        val myDialog = AlertDialog.Builder(activity)
        //TODO: Change layout and create a new one for expenses
        val binding = CustomLayoutForInsertdataBinding.inflate(layoutInflater) // Usar View Binding aquí
        myDialog.setView(binding.root)
        val dialog: AlertDialog = myDialog.create()
        dialog.setCancelable(false)

        val list = mutableListOf<String>()
        list.addAll(listOf("House", "Food", "Entertainment", "Personal expenses", "Health care",
            "Transportation", "Debt / Student Loan", "others"))

        val arrayAdapter = ArrayAdapter(binding.root.context, R.layout.dropdown_item, list)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)

        var type = ""
        binding.autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val item: String = parent.getItemAtPosition(position).toString()
            toastMessage(item)
            type = item
        }

        dialog.show()

        binding.btnSave.setOnClickListener {
            val amountStr = binding.amountEdt.text.toString().trim()
            val noteStr = binding.noteEdt.text.toString().trim()
            if (TextUtils.isEmpty(type)) {
                binding.autoCompleteTextView.error = "Required field..."
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(amountStr)) {
                binding.amountEdt.error = "Required field..."
                return@setOnClickListener
            } else if (!TextUtils.isDigitsOnly(amountStr)) {
                binding.noteEdt.error = "Only numeric numbers"
                return@setOnClickListener
            }
            val ourAmount: Double = amountStr.toDouble()

            if (TextUtils.isEmpty(noteStr)) {
                binding.noteEdt.error = "Required field..."
                return@setOnClickListener
            }
            val id: String = expenseDatabase.push().key!!
            val mDate: String = LocalDate.now().format(formatter)
            val data = Data(ourAmount, type, noteStr, id, mDate)

            expenseDatabase.child(id).setValue(data)
            toastMessage("Data added")

            dialog.dismiss()
        }

        binding.btnCancelData.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun toastMessage(message:String){
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}