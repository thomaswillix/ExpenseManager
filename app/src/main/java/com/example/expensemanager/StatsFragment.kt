package com.example.expensemanager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.expensemanager.databinding.FragmentStatsBinding
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class StatsFragment : Fragment() {
    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var incomeDatabase: DatabaseReference
    private lateinit var expenseDatabase: DatabaseReference
    private val incomeValues = mutableListOf<Data>()
    private val expenseValues = mutableListOf<Data>()
    private lateinit var incomeListener: ValueEventListener
    private lateinit var expenseListener: ValueEventListener

    // Binding
    private lateinit var binding: FragmentStatsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatsBinding.inflate(inflater, container, false)
        val myView: View = binding.root

        auth = FirebaseAuth.getInstance()
        val user: FirebaseUser = auth.currentUser!!
        val uid: String = user.uid
        incomeDatabase = FirebaseDatabase.getInstance().reference.child("IncomeData").child(uid)
        expenseDatabase = FirebaseDatabase.getInstance().reference.child("ExpenseData").child(uid)

        incomeListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                incomeValues.clear()
                for (ds in dataSnapshot.children) {
                    val data = ds.getValue(Data::class.java)
                    if (data != null) {
                        incomeValues.add(data)
                    }
                }
                updateCharts()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("StatsFragment", "loadIncome:onCancelled", databaseError.toException())
            }
        }

        expenseListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                expenseValues.clear()
                for (ds in dataSnapshot.children) {
                    val data = ds.getValue(Data::class.java)
                    if (data != null) {
                        expenseValues.add(data)
                    }
                }
                updateCharts()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("StatsFragment", "loadExpense:onCancelled", databaseError.toException())
            }
        }

        incomeDatabase.addValueEventListener(incomeListener)
        expenseDatabase.addValueEventListener(expenseListener)

        return myView
    }

    override fun onPause() {
        super.onPause()
        incomeDatabase.removeEventListener(incomeListener)
        expenseDatabase.removeEventListener(expenseListener)
    }

    override fun onResume() {
        super.onResume()
        incomeDatabase.addValueEventListener(incomeListener)
        expenseDatabase.addValueEventListener(expenseListener)
    }

    private fun updateCharts() {
        val incomeEntries = mutableListOf<BarEntry>()
        val expenseEntries = mutableListOf<BarEntry>()
        val incomeLineEntries = mutableListOf<Entry>()
        val expenseLineEntries = mutableListOf<Entry>()

        var index = 0

        // Preparar datos para los gráficos
        for (income in incomeValues) {
            incomeEntries.add(BarEntry(index.toFloat(), income.amount.toFloat()))
            incomeLineEntries.add(Entry(index.toFloat(), income.amount.toFloat()))
            index++
        }

        index = 0
        for (expense in expenseValues) {
            expenseEntries.add(BarEntry(index.toFloat(), expense.amount.toFloat()))
            expenseLineEntries.add(Entry(index.toFloat(), expense.amount.toFloat()))
            index++
        }

        // Configurar datos del BarChart
        val barDataSetIncome = BarDataSet(incomeEntries, "Incomes")
        val barDataSetExpense = BarDataSet(expenseEntries, "Expenses")
        val barData = BarData(barDataSetIncome, barDataSetExpense)

        binding.barChart.data = barData
        binding.barChart.invalidate() // Refrescar el BarChart

        // Configurar LineChart para ingresos
        val lineDataSetIncome = LineDataSet(incomeLineEntries, "Incomes Over Time")
        val lineDataSetExpense = LineDataSet(expenseLineEntries, "Expenses Over Time")
        if (isAdded) {
            val incomeColor = resources.getColor(R.color.income, null)
            val expenseColor = resources.getColor(R.color.totalExpenses, null)

            lineDataSetIncome.color = incomeColor
            lineDataSetIncome.setCircleColor(incomeColor)

            lineDataSetExpense.color = expenseColor
            lineDataSetExpense.setCircleColor(expenseColor)
        }
        lineDataSetIncome.lineWidth = 2f
        val incomeLineData = LineData(lineDataSetIncome)

        binding.linechartIncomes.data = incomeLineData
        binding.linechartIncomes.invalidate() // Refrescar LineChart de ingresos

        // Configurar LineChart para gastos
        lineDataSetExpense.lineWidth = 2f
        val expenseLineData = LineData(lineDataSetExpense)

        binding.linechartExpenses.data = expenseLineData
        binding.linechartExpenses.invalidate() // Refrescar LineChart de gastos

        // Ocultar barras de progreso cuando los datos estén cargados
        binding.progressBar.visibility = View.GONE
        binding.progressBar2.visibility = View.GONE
        binding.progressBar3.visibility = View.GONE
    }

    data class Data(
        val amount: Double = 0.0,
        val type: String = "",
        val date: String = ""
    )
}