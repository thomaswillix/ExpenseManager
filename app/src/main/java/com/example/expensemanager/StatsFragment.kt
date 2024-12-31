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
import java.text.SimpleDateFormat
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
    private var dataEntriesIncome = arrayListOf<DataEntry>()
    private var dataEntriesExpense = arrayListOf<DataEntry>()

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
        getValues()
    }

    private fun createLineChart() {
        val anyChartView: AnyChartView = binding.anyChartView
        anyChartView.setProgressBar(binding.progressBar)

        val cartesian: Cartesian = AnyChart.line()

        cartesian.animation(true)
        cartesian.padding(10, 20, 5, 20)

        cartesian.crosshair().enabled(true)
        cartesian.crosshair().yLabel(true).yStroke()

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT)

        cartesian.title("Line chart of incomes and expenses over time.")
        cartesian.yAxis(0).title("Currency")
        cartesian.xAxis(0).title("Date")
        cartesian.xAxis(0).labels().padding(5, 5, 5, 5)
        cartesian.xAxis(0).labels().format("{%value|dateTimeFormat:'dd MMM yyyy HH:mm:ss'}")
        val seriesData: MutableList<DataEntry> = mutableListOf()

        for (data in combinedValues) {
            val income = if (data.type in listOfIncomes) data.amount else 0.0
            val expense = if (data.type in listOfExpenses) data.amount else 0.0
            seriesData.add(CustomDataEntry(data.date, income, expense))
        }
        /*val seriesData: MutableList<DataEntry> = mutableListOf(
            CustomDataEntry("2023-10-01", 100.0, 50.0),
            CustomDataEntry("2023-10-02", 150.0, 75.0),
            CustomDataEntry("2023-10-03", 200.0, 100.0)
        )*/
        val set: Set = Set.instantiate()
        set.data(seriesData)

        // Mapear series al gráfico
        val incomeMapping: Mapping = set.mapAs("{ x: 'x', value: 'value1' }")
        val expenseMapping: Mapping = set.mapAs("{ x: 'x', value: 'value2' }")

        val incomeSeries: Line = cartesian.line(incomeMapping)
        incomeSeries.name("Income")
        incomeSeries.hovered().markers().enabled(true)
        incomeSeries.hovered().markers().type(MarkerType.CIRCLE).size(4)
        incomeSeries.tooltip().position("right").anchor("left | center").offsetX(5).offsetY(5)

        val expenseSeries: Line = cartesian.line(expenseMapping)
        expenseSeries.name("Expense")
        expenseSeries.hovered().markers().enabled(true)
        expenseSeries.hovered().markers().type(MarkerType.CIRCLE).size(4)
        expenseSeries.tooltip().position("right").anchor("left | center").offsetX(5).offsetY(5)

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
        pie.data(dataEntriesIncome)
        pie.title("Ingresos por categoría")
        anyChartView.setChart(pie)

        myDialog.show()
    }

    private fun getValues() {
        for (data in combinedValues){
            val value = data.amount
            values[data.type] = values.getOrDefault(data.type, 0.0) + value
        }
        dataEntriesIncome.clear()
        dataEntriesExpense.clear()
        for (type in listOfIncomes) {
            val value = values[type] ?: 0.0
            dataEntriesIncome.add(ValueDataEntry(type, value))
        }
        for (type in listOfExpenses) {
            val value = values[type] ?: 0.0
            dataEntriesExpense.add(ValueDataEntry(type, value))
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
        pie.data(dataEntriesExpense)
        pie.title("Gastos por categoría")
        anyChartView.setChart(pie)

        myDialog.show()
    }
    data class CustomDataEntry(
        val date: String,
        val income: Double,
        val expense: Double
    ) : DataEntry() {
        init {
            // Parse the date string into a Date object
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM yyyy HH:mm:ss", Locale("en"))
            val parsedDate = LocalDateTime.parse(date, formatter)


            setValue("x", parsedDate.dayOfMonth)       // Valor para el eje X (la fecha en milisegundos)
            setValue("value1", income)     // Valor para la primera serie (ingresos)
            setValue("value2", expense)    // Valor para la segunda serie (egresos)
        }
    }
}