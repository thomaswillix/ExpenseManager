package com.example.expensemanager

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class FirebaseUtil {
    val incomeValues = mutableListOf<Data>()
    val expenseValues = mutableListOf<Data>()
    val allIncomeValues = mutableListOf<Data>()
    val allExpenseValues = mutableListOf<Data>()
    val combinedValues = mutableListOf<Data>()

    // Formateador de fecha
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM yyyy HH:mm:ss", Locale("en"))
    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH) // 0-based: enero es 0
    val currentYear = calendar.get(Calendar.YEAR)

    fun processIncome(dataSnapshot: DataSnapshot): Double {
        incomeValues.clear()
        allIncomeValues.clear()
        var totalIncome = 0.0

        for (ds in dataSnapshot.children) {
            val data = ds.getValue(Data::class.java)
            if (data != null) {
                allIncomeValues.add(data)

                val transactionDate = LocalDateTime.parse(data.date, formatter)
                if (transactionDate.monthValue - 1 == currentMonth && transactionDate.year == currentYear) {
                    incomeValues.add(data)
                    totalIncome += data.amount
                }
            }
        }
        return totalIncome
    }

    fun processExpense(dataSnapshot: DataSnapshot): Double {
        expenseValues.clear()
        allExpenseValues.clear()
        var totalExpenses = 0.0

        for (ds in dataSnapshot.children) {
            val data = ds.getValue(Data::class.java)
            if (data != null) {
                allExpenseValues.add(data)

                val transactionDate = LocalDateTime.parse(data.date, formatter)
                if (transactionDate.monthValue - 1 == currentMonth && transactionDate.year == currentYear) {
                    expenseValues.add(data)
                    totalExpenses += data.amount
                }
            }
        }
        return totalExpenses
    }

    fun updateCombinedList() {
        combinedValues.clear()
        combinedValues.addAll(incomeValues)
        combinedValues.addAll(expenseValues)
        combinedValues.sortByDescending { LocalDateTime.parse(it.date, formatter) }
    }
}
