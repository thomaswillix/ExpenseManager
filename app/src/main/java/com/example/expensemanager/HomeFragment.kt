package com.example.expensemanager

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
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
import android.widget.AutoCompleteTextView
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.expensemanager.databinding.CustomLayoutForExpenseDataBinding
import com.example.expensemanager.databinding.CustomLayoutForInsertdataBinding
import com.example.expensemanager.databinding.DialogConfirmDeleteBinding
import com.example.expensemanager.databinding.FragmentHomeBinding
import com.example.expensemanager.databinding.TransactionDetailBinding
import com.example.expensemanager.databinding.ViewAllTransactionsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
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
    private val listOfIncomes = mutableListOf<String>()
    private val listOfExpenses = mutableListOf<String>()
    private val allCombinedValues = mutableListOf<Data>()
    private val allIncomeValues = mutableListOf<Data>()
    private val allExpenseValues = mutableListOf<Data>()

    // Formateador de fecha
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM yyyy HH:mm:ss", Locale("en"))

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
        val uid : String = user.uid
        incomeDatabase = FirebaseDatabase.getInstance().reference.child("IncomeData").child(uid)
        expenseDatabase = FirebaseDatabase.getInstance().reference.child("ExpenseData").child(uid)
        listOfIncomes.addAll(listOf("Payckeck", "Intellectual Propperty", "Stocks", "Business",
            "Savings, bonds or lending", "others (income)"))
        listOfExpenses.addAll(listOf("House", "Food", "Entertainment", "Personal expenses", "Health care",
            "Transportation", "Debt / Student Loan", "others (expense)"))

        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH) // 0-based: enero es 0
        val currentYear = calendar.get(Calendar.YEAR)

        val incomeListener = object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                incomeValues.clear()
                allIncomeValues.clear()
                binding.totalIncome.text = "0.00"

                for (ds in dataSnapshot.children) {
                    val data = ds.getValue(Data::class.java)
                    if (data != null) {
                        // Convertir la fecha de la transacción a LocalDateTime usando el formato adecuado
                        val transactionDate = LocalDateTime.parse(data.date, formatter)

                        // Extraemos el mes y el año de la transacción
                        val transactionMonth = transactionDate.monthValue - 1  // Enero es 1, pero Calendar usa 0
                        val transactionYear = transactionDate.year

                        allIncomeValues.add(data)

                        // Comprobar si la transacción es de este mes y año
                        if (transactionMonth == currentMonth && transactionYear == currentYear) {
                            incomeValues.add(data)
                            val value : Double = binding.totalIncome.text.toString().toDouble() + data.amount
                            binding.totalIncome.text = value.toString()
                        }
                    }
                }

                // Asegúrate de que el valor de totalIncome tenga un signo "+"
                val text : String = "+" + binding.totalIncome.text
                binding.totalIncome.text = text

                // Ajustar tamaño del texto
                val textSize = when (binding.totalIncome.text.length) {
                    in 1..10 -> 18f
                    in 11..16 -> 14f
                    else -> 12f
                }
                binding.totalIncome.textSize = textSize

                // Actualizar la lista combinada
                updateCombinedList()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("loadIncome:onCancelled", databaseError.toException())
            }
        }
        val expenseListener = object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                expenseValues.clear()
                allExpenseValues.clear()
                binding.totalExpenses.text = "0.00"

                for (ds in dataSnapshot.children) {
                    val data = ds.getValue(Data::class.java)
                    if (data != null) {
                        // Convertir la fecha de la transacción a LocalDateTime usando el formato adecuado
                        val transactionDate = LocalDateTime.parse(data.date, formatter)

                        // Extraemos el mes y el año de la transacción
                        val transactionMonth = transactionDate.monthValue - 1  // Enero es 1, pero Calendar usa 0
                        val transactionYear = transactionDate.year

                        allExpenseValues.add(data)

                        // Comprobar si la transacción es de este mes y año
                        if (transactionMonth == currentMonth && transactionYear == currentYear) {
                            expenseValues.add(data)
                            val value : Double = binding.totalExpenses.text.toString().toDouble() + data.amount
                            binding.totalExpenses.text = value.toString()
                        }
                    }
                }

                // Asegurarse de que el valor de totalExpenses tenga un signo "-"
                val text: String = "-" + binding.totalExpenses.text
                binding.totalExpenses.text = text

                // Ajustar tamaño del texto según la longitud
                val textSize = when (binding.totalExpenses.text.length) {
                    in 1..10 -> 18f
                    in 11..16 -> 14f
                    else -> 12f
                }
                binding.totalExpenses.textSize = textSize

                // Actualizar la lista combinada
                updateCombinedList()
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
        binding.viewAllTransactions.setOnClickListener{
            if (allCombinedValues.isEmpty()) {
                toastMessage("There are no transactions yet, add one pressing '+'!")
            } else {
                viewAllTransactions()
            }
        }
        //Detail view of a transaction
        binding.listCombined.setOnItemClickListener { parent, _, position, _ ->
            transactionDetailView(parent, position)
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

    private fun viewAllTransactions() {
        val binding = ViewAllTransactionsBinding.inflate(layoutInflater)
        val myDialog = AlertDialog.Builder(activity)
            .setView(binding.root)
            .create()
        myDialog.setCancelable(false)

        // Ordenar la lista combinada por fecha
        allCombinedValues.sortByDescending { data ->
            LocalDateTime.parse(data.date, formatter)
        }

        // Verificar si el contexto está disponible y actualizar el adaptador
        context?.let { safeContext ->
            val listAdapter = ListAdapter(safeContext, R.layout.list_item, allCombinedValues)
            binding.listCombined.adapter = listAdapter
            listAdapter.notifyDataSetChanged()
        }
        myDialog.show()

        binding.closeButton.setOnClickListener {
            myDialog.dismiss()
        }
    }

    private fun transactionDetailView(parent: AdapterView<*>, position: Int) {
        val binding = TransactionDetailBinding.inflate(layoutInflater)
        val myDialog = AlertDialog.Builder(activity)
            .setView(binding.root)
            .create()
        myDialog.setCancelable(false)

        val selectedItem = parent.getItemAtPosition(position) as? Data

        selectedItem?.let { item ->
            binding.noteEditText.setText(item.note)
            binding.quantityEditText.setText(item.amount.toString())
            binding.date.text = item.date
            binding.category.text = item.type
            if (listOfIncomes.contains(item.type)) {
                binding.quantityEditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.income))
                when (item.type) {
                    "Payckeck" -> {
                        //icon by https://www.flaticon.com/authors/juicy-fish
                        binding.icon.setImageResource(R.drawable.paycheck)
                    }

                    "Intellectual Propperty" -> {
                        //icon by freepik https://www.flaticon.com/free-icons/intellectual-property
                        binding.icon.setImageResource(R.drawable.intellectual_property)
                    }

                    "Stocks" -> {
                        //icon by https://www.flaticon.com/authors/adriansyah
                        binding.icon.setImageResource(R.drawable.stocks)
                    }

                    "Business" -> {
                        //icon by https://www.flaticon.com/authors/pixel-perfect
                        binding.icon.setImageResource(R.drawable.business)
                    }

                    "Savings, bonds or lending" -> {
                        //icon by https://www.flaticon.com/authors/karyative
                        binding.icon.setImageResource(R.drawable.savings)
                    }

                    "others (income)" -> {
                        //icon by freepik
                        binding.icon.setImageResource(R.drawable.others_income)
                    }
                }
            } else {
                binding.quantityEditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.totalExpenses))
                when (item.type) {
                    "Food" -> {
                        //icon by freepik
                        binding.icon.setImageResource(R.drawable.food)
                    }
                    "House" -> {
                        // icon by imaginationlol
                        binding.icon.setImageResource(R.drawable.house)
                    }
                    "Entertainment" -> {
                        // icon by https://www.flaticon.com/authors/wanicon
                        binding.icon.setImageResource(R.drawable.entertainment)
                    }
                    "Personal expenses" -> {
                        //icon by freepik
                        binding.icon.setImageResource(R.drawable.personal_expenses)
                    }
                    "Health care" -> {
                        //icon by freepik
                        binding.icon.setImageResource(R.drawable.healthcare)
                    }
                    "Transportation" -> {
                        //icon by freepik
                        binding.icon.setImageResource(R.drawable.transport)
                    }
                    "Debt / Student Loan"->{
                        //icon by https://www.flaticon.com/authors/afian-rochmah-afif
                        binding.icon.setImageResource(R.drawable.debt)
                    }
                    "others (expense)" -> {
                        //icon by https://www.flaticon.com/authors/surang
                        binding.icon.setImageResource(R.drawable.others_expense)
                    }
                }
            }
        }
        binding.deleteButton.setOnClickListener {
            val confirmBinding = DialogConfirmDeleteBinding.inflate(layoutInflater)
            val confirmDialog = AlertDialog.Builder(activity)
                .setView(confirmBinding.root)
                .create()

            confirmBinding.btnYes.setOnClickListener {
                selectedItem?.let { item ->
                    if (listOfIncomes.contains(item.type)){
                        incomeDatabase.child(item.id).removeValue()
                    } else{
                        expenseDatabase.child(item.id).removeValue()
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
                if (validateEditedFields(binding.noteEditText, binding.quantityEditText)){
                    item.note = binding.noteEditText.text.toString()
                    val amount = binding.quantityEditText.text.toString().toDoubleOrNull() ?: 0.0

                    if (listOfIncomes.contains(item.type)){
                        editIncome(item.id, item.type, binding.noteEditText.text.toString(), amount, item.date)
                    } else{
                        editExpense(item.id, item.type, binding.noteEditText.text.toString(), amount, item.date)
                    }
                    (parent.adapter as? BaseAdapter)?.notifyDataSetChanged()
                } else return@setOnClickListener
            }
            myDialog.dismiss()
        }

        myDialog.show()
    }

    private fun validateEditedFields(note: EditText, amount: EditText) : Boolean {
        val amountStr = amount.text.toString().trim()
        val noteStr = note.text.toString().trim()

        if (TextUtils.isEmpty(amountStr)) {
            amount.error = "Required field..."
            return false
        }

        if (TextUtils.isEmpty(noteStr)) {
            note.error = "Required field..."
            return false
        }
        return true
    }

    private fun editIncome(id : String,  type: String, note: String, amount : Double, date: String) {
        val data = Data(amount, type, note, id, date)

        incomeDatabase.child(id).setValue(data)
        toastMessage("Data edited")
    }

    private fun editExpense(id: String, type: String, note: String, amount: Double, date :String) {
        val data = Data(amount, type, note, id, date)

        expenseDatabase.child(id).setValue(data)
        toastMessage("Data edited")
    }

    private fun updateBalance() {
        val income = binding.totalIncome.text.toString().toDoubleOrNull() ?: 0.0
        val expenses = binding.totalExpenses.text.toString().toDoubleOrNull() ?: 0.0
        val number = income + expenses

        val formattedNumber = String.format("%.2f", number)
        val normalizedAmountString = formattedNumber.replace(",", ".")  // Reemplazar la coma por punto
        val balance = normalizedAmountString.toDouble()

        binding.totalBalance.text = formatBalanceText(balance)

        // Ajustar dinámicamente el tamaño del texto
        val textSize = when (binding.totalBalance.text.length) {
            in 1..10 -> 40f // Hasta 6 caracteres -> 40sp
            in 11..15 -> 32f // 7-9 caracteres -> 32sp
            else -> 24f    // 10 o más caracteres -> 24sp
        }
        binding.totalBalance.textSize = textSize
        context?.let { safeContext ->
            binding.totalBalance.setTextColor(getBalanceColor(safeContext, balance))
        }
    }

    private fun formatBalanceText(balance: Double): String {
        return if (balance > 0) "+$balance €" else "$balance €"
    }

    private fun getBalanceColor(context: Context, balance: Double): Int {
        return if (balance > 0) {
            ContextCompat.getColor(context, R.color.income)
        } else {
            ContextCompat.getColor(context, R.color.totalExpenses)
        }
    }

    private fun updateUIForEmptyState(isEmpty: Boolean) {
        binding.emptyImage.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.emptyText.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.emptyText2.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.listCombined.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    // Función para actualizar la lista combinada y ordenarla
    private fun updateList() {
        // Ordenar la lista combinada por fecha
        combinedValues.sortByDescending { data ->
            LocalDateTime.parse(data.date, formatter)
        }

        // Verificar si el contexto está disponible y actualizar el adaptador
        context?.let { safeContext ->
            val listAdapter = ListAdapter(safeContext, R.layout.list_item, combinedValues)
            binding.listCombined.adapter = listAdapter
            listAdapter.notifyDataSetChanged()
        }
    }

    private fun updateCombinedList() {
        combinedValues.clear()
        allCombinedValues.clear()
        combinedValues.addAll(incomeValues)
        combinedValues.addAll(expenseValues)
        allCombinedValues.addAll(allIncomeValues)
        allCombinedValues.addAll(allExpenseValues)
        updateBalance()

        if (combinedValues.isEmpty()) {
            updateUIForEmptyState(true)
        } else {
            updateUIForEmptyState(false)
            updateList()
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
            if(!hasMoney()) {
                toastMessage("You can't make that operation now as to don't have money")
            } else{
                expenseDataInsert()
                ftAnimation()
            }
        }
    }

    private fun incomeDataInsert() {
        val myDialog = AlertDialog.Builder(activity)
        val binding = CustomLayoutForInsertdataBinding.inflate(layoutInflater) // Usar View Binding aquí
        myDialog.setView(binding.root)
        val dialog: AlertDialog = myDialog.create()
        dialog.setCancelable(false)

        val arrayAdapter = ArrayAdapter<String>(binding.root.context, R.layout.dropdown_item, listOfIncomes)

        var typeStr = ""
        binding.autoCompleteTextView.setAdapter(arrayAdapter)
        binding.autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            val item: String = parent.getItemAtPosition(position).toString()
            toastMessage(item)
            typeStr = item
        }

        dialog.show()

        binding.btnSave.setOnClickListener {
            if (!validateFields(
                    binding.noteEdt,
                    binding.autoCompleteTextView,
                    binding.amountEdt,
                    typeStr
                )) {
                return@setOnClickListener
            }

            val formattedNumber = getFormattedAmount(binding.amountEdt.text.toString())
            if (formattedNumber == null) {
                toastMessage("Please enter a valid number")
                return@setOnClickListener
            }

            addIncome(
                binding.noteEdt.text.toString(),
                formattedNumber,
                typeStr
            )
            dialog.dismiss()
        }

        binding.btnCancelData.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun validateFields(
        note: EditText,
        typeEdt: AutoCompleteTextView,
        amount: EditText,
        typeStr: String,
    ) : Boolean{
        val amountStr = amount.text.toString().trim()
        val noteStr = note.text.toString().trim()

        if (TextUtils.isEmpty(typeStr)) {
            typeEdt.error = "Required field..."
            return false
        }
        if (TextUtils.isEmpty(amountStr)) {
            amount.error = "Required field..."
            return false
        } else if (amountStr.toDouble() > binding.totalBalance.toString().toDouble()){
            amount.error = "Amount cannot be superior to your current balance"
            return false
        }

        if (TextUtils.isEmpty(noteStr)) {
            note.error = "Required field..."
            return false
        }
        return true
    }

    private fun addIncome(note: String, amount : Double, type : String) {
        val id: String = incomeDatabase.push().key!!
        val date: String = LocalDateTime.now().format(formatter)
        val data = Data(amount, type, note, id, date)

        incomeDatabase.child(id).setValue(data)
        toastMessage("Data added")
    }

    private fun expenseDataInsert() {
        val myDialog = AlertDialog.Builder(activity)
        val binding = CustomLayoutForExpenseDataBinding.inflate(layoutInflater) // Usar View Binding aquí
        myDialog.setView(binding.root)
        val dialog: AlertDialog = myDialog.create()
        dialog.setCancelable(false)

        val arrayAdapter = ArrayAdapter(binding.root.context, R.layout.dropdown_item, listOfExpenses)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)

        var type = ""
        binding.autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            val item: String = parent.getItemAtPosition(position).toString()
            toastMessage(item)
            type = item
        }

        dialog.show()

        binding.btnSave.setOnClickListener {
            if (!validateFields(
                    binding.noteEdt,
                    binding.autoCompleteTextView,
                    binding.amountEdt,
                    type
                )) {
                return@setOnClickListener
            }

            val formattedNumber = getFormattedAmount(binding.amountEdt.text.toString())
            if (formattedNumber == null) {
                toastMessage("Please enter a valid number")
                return@setOnClickListener
            }

            addExpense(
                binding.noteEdt.text.toString(),
                formattedNumber,
                type
            )
            dialog.dismiss()
        }

        binding.btnCancelData.setOnClickListener {
            dialog.dismiss()
        }
    }
    private fun getFormattedAmount(amountString: String): Double? {
        val number = amountString.toDoubleOrNull() ?: return null
        return String.format("%.2f", number).toDouble()
    }

    private fun addExpense(note: String, amount: Double, type: String) {
        val id: String = expenseDatabase.push().key!!
        val date: String = LocalDateTime.now().format(formatter)
        val data = Data(amount, type, note, id, date)

        expenseDatabase.child(id).setValue(data)
        toastMessage("Data added")
    }

    private fun hasMoney(): Boolean {
        if(binding.totalBalance.text.toString().replace("€", "").toDouble() == 0.0){
            return false
        }
        return true
    }

    private fun toastMessage(message:String){
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}