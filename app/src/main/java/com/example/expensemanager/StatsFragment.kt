package com.example.expensemanager

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.core.cartesian.series.Line
import com.anychart.enums.MarkerType
import com.anychart.enums.TooltipPositionMode
import com.anychart.data.Set
import com.anychart.data.Mapping
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.anychart.charts.Cartesian
import com.anychart.charts.Pie
import com.example.expensemanager.databinding.ExpenseStatsBinding
import com.example.expensemanager.databinding.FragmentStatsBinding
import com.example.expensemanager.databinding.IncomeStatsBinding
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

class StatsFragment : Fragment() {
    //Firebase
    private lateinit var auth : FirebaseAuth
    private lateinit var incomeDatabase : DatabaseReference
    private lateinit var expenseDatabase : DatabaseReference
    private val incomeValues = mutableListOf<Data>()
    private val expenseValues = mutableListOf<Data>()
    private val combinedValues = mutableListOf<Data>()
    private val listOfIncomes = mutableListOf<String>()
    private val listOfExpenses = mutableListOf<String>()
    private val values = mutableMapOf<String, Double>()

    // Formateador de fecha
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM yyyy HH:mm:ss", Locale("en"))

    // Binding
    private lateinit var binding: FragmentStatsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentStatsBinding.inflate(layoutInflater, container, false)
        val myView : View = binding.root
        super.onViewCreated(myView, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val user : FirebaseUser = auth.currentUser!!
        val uid:String = user.uid
        incomeDatabase =FirebaseDatabase.getInstance().reference.child("IncomeData").child(uid)
        expenseDatabase =FirebaseDatabase.getInstance().reference.child("ExpenseData").child(uid)
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

                for (ds in dataSnapshot.children) {
                    val data = ds.getValue(Data::class.java)
                    if (data != null) {
                        // Convertir la fecha de la transacción a LocalDateTime usando el formato adecuado
                        val transactionDate = LocalDateTime.parse(data.date, formatter)

                        // Extraemos el mes y el año de la transacción
                        val transactionMonth = transactionDate.monthValue - 1  // Enero es 1, pero Calendar usa 0
                        val transactionYear = transactionDate.year

                        // Comprobar si la transacción es de este mes y año
                        if (transactionMonth == currentMonth && transactionYear == currentYear) {
                            incomeValues.add(data)
                        }
                    }
                }
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

                for (ds in dataSnapshot.children) {
                    val data = ds.getValue(Data::class.java)
                    if (data != null) {
                        // Convertir la fecha de la transacción a LocalDateTime usando el formato adecuado
                        val transactionDate = LocalDateTime.parse(data.date, formatter)

                        // Extraemos el mes y el año de la transacción
                        val transactionMonth = transactionDate.monthValue - 1  // Enero es 1, pero Calendar usa 0
                        val transactionYear = transactionDate.year

                        // Comprobar si la transacción es de este mes y año
                        if (transactionMonth == currentMonth && transactionYear == currentYear) {
                            expenseValues.add(data)
                        }
                    }
                }
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
        getValues(values)

        createLineChart()
        binding.showIncomeData.setOnClickListener {
            createPieChartIncomes()
        }
        binding.showExpenseData.setOnClickListener {
            createPieChartExpenses()
        }
        return myView
    }

    // Function to update and order the combined list
    private fun updateList() {
        // Ordenar la lista combinada por fecha
        combinedValues.sortByDescending { data ->
            LocalDateTime.parse(data.date, formatter)
        }
    }

    private fun updateCombinedList() {
        combinedValues.clear()
        combinedValues.addAll(incomeValues)
        combinedValues.addAll(expenseValues)

        updateList()
    }

    private fun createLineChart(){
        val anyChartView : AnyChartView = binding.anyChartView
        anyChartView.setProgressBar(binding.progressBar)

        val cartesian: Cartesian = AnyChart.line()

        cartesian.animation(true)

        cartesian.padding(10, 20, 5, 20)

        cartesian.crosshair().enabled(true)
        cartesian.crosshair()
            .yLabel(true)
            // TODO ystroke
            .yStroke()


        cartesian.tooltip().positionMode(TooltipPositionMode.POINT)

        cartesian.title("Line chart on all incomes and expenses.")

        cartesian.yAxis(0).title("Currency per day")
        cartesian.xAxis(0).labels().padding(5, 5, 5, 5)

        val seriesData: MutableList<CustomDataEntry> = mutableListOf()

        seriesData.add(CustomDataEntry("2002", 3.6, 2.3, 2.8))
        seriesData.add(CustomDataEntry("2003", 7.1, 4.0, 4.1))
        seriesData.add(CustomDataEntry("2004", 8.5, 6.2, 5.1))
        seriesData.add(CustomDataEntry("2005", 9.2, 11.8, 6.5))
        seriesData.add(CustomDataEntry("2006", 10.1, 13.0, 12.5))
        seriesData.add(CustomDataEntry("2008", 11.6, 13.9, 18.0))
        seriesData.add(CustomDataEntry("2009", 16.4, 18.0, 21.0))
        seriesData.add(CustomDataEntry("20012", 18.0, 23.3, 20.3))
        seriesData.add(CustomDataEntry("2013", 13.2, 24.7, 19.2))

        val set : Set = Set.instantiate()
        set.data(seriesData as List<DataEntry>?)
        val series1Mapping: Mapping = set.mapAs("{ x: 'x', value: 'value' }")
        val series2Mapping : Mapping = set.mapAs("{ x: 'x', value: 'value2' }")
        val series3Mapping : Mapping = set.mapAs("{ x: 'x', value: 'value3' }")

        val series1: Line = cartesian.line(series1Mapping)
        series1.name("Money")
        series1.hovered().markers().enabled(true)
        series1.hovered().markers()
            .type(MarkerType.CIRCLE)
            .size(4)
        series1.tooltip()
            .position("right")
            .anchor("left | center")
            .offsetX(5)
            .offsetY(5)

        val series2 : Line = cartesian.line(series2Mapping)
        series2.name("Income")
        series2.hovered().markers().enabled(true)
        series2.hovered().markers()
            .type(MarkerType.CIRCLE)
            .size(4)
        series2.tooltip()
            .position("right")
            .anchor("left | center")
            .offsetX(5)
            .offsetY(5)

        val series3 : Line  = cartesian.line(series3Mapping)
        series3.name("Expense")
        series3.hovered().markers().enabled(true)
        series3.hovered().markers()
            .type(MarkerType.CIRCLE)
            .size(4)
        series3.tooltip()
            .position("right")
            .anchor("left | center")
            .offsetX(5)
            .offsetY(5)

        cartesian.legend().enabled(true)
        cartesian.legend().fontSize(13)
        cartesian.legend().padding(0, 0, 10, 0)

        anyChartView.setChart(cartesian)
    }

    private fun createPieChartIncomes(){
        val binding = IncomeStatsBinding.inflate(layoutInflater)
        val myDialog = AlertDialog.Builder(activity)
            .setView(binding.root)
            .create()
        myDialog.setCancelable(true)
        val anyChartView: AnyChartView = binding.anyChartView
        anyChartView.setProgressBar(binding.progressBar)

        val pie: Pie = AnyChart.pie()

        val dataEntries = arrayListOf<DataEntry>()
        dataEntries.add(ValueDataEntry("Test", 9.0))
        for (type in listOfIncomes) {
            val value = values[type] ?: 0.0
            dataEntries.add(ValueDataEntry(type, value))
        }
        pie.data(dataEntries)
        pie.title("Gastos por categoría")
        pie.legend().itemsLayout(com.anychart.enums.LegendLayout.VERTICAL)
        pie.legend().position("bottom")
        pie.legend().align("center")

        anyChartView.setChart(pie)

        myDialog.show()
    }

    private fun getValues(values: MutableMap<String,Double>) {
        for (data in combinedValues){
            val value = data.amount
            values[data.type] = values.getOrDefault(data.type, 0.0) + value
        }
    }

    private fun createPieChartExpenses(){
        val binding = ExpenseStatsBinding.inflate(layoutInflater)
        val myDialog = AlertDialog.Builder(activity)
            .setView(binding.root)
            .create()
        myDialog.setCancelable(true)
        val anyChartView: AnyChartView = binding.anyChartView
        anyChartView.setProgressBar(binding.progressBar)

        val pie: Pie = AnyChart.pie()

        val dataEntries = arrayListOf<DataEntry>()
        for (type in listOfExpenses){
            dataEntries.add(ValueDataEntry(type, values[type]))
        }
        pie.data(dataEntries)
        pie.title("Gastos por categoría")
        anyChartView.setChart(pie)

        myDialog.show()
    }


    data class CustomDataEntry(
        val year: String,
        val value1: Double,
        val value2: Double,
        val value3: Double
    ) : DataEntry()
}