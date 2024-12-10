package com.example.expensemanager

import android.os.Bundle
// Para AnyChart y las vistas de gráfico
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.core.cartesian.series.Line
import com.anychart.enums.MarkerType
import com.anychart.enums.TooltipPositionMode

// Para la manipulación de los datos
import com.anychart.data.Set
import com.anychart.data.Mapping
// Para el uso del Binding en el fragmento
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.anychart.charts.Cartesian
import com.anychart.charts.Radar
import com.anychart.enums.Align
import com.example.expensemanager.databinding.FragmentStatsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class StatsFragment : Fragment() {
    //Firebase
    private lateinit var auth : FirebaseAuth
    private lateinit var incomeDatabase : DatabaseReference
    private lateinit var expenseDatabase : DatabaseReference
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
        incomeDatabase =FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid)
        expenseDatabase =FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid)

        createLineChart()
        //createRadarChartIncomes()
        //createRadarChartExpenses()
        return myView
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

        cartesian.title("Line chart on all incomes and expenses.");

        cartesian.yAxis(0).title("Currency per day");
        cartesian.xAxis(0).labels().padding(5, 5, 5, 5);

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
        set.data(seriesData as List<DataEntry>?);
        val series1Mapping: Mapping = set.mapAs("{ x: 'x', value: 'value' }");
        val series2Mapping : Mapping = set.mapAs("{ x: 'x', value: 'value2' }");
        val series3Mapping : Mapping = set.mapAs("{ x: 'x', value: 'value3' }");

        val series1: Line = cartesian.line(series1Mapping);
        series1.name("Money");
        series1.hovered().markers().enabled(true);
        series1.hovered().markers()
            .type(MarkerType.CIRCLE)
            .size(4);
        series1.tooltip()
            .position("right")
            .anchor("left | center")
            .offsetX(5)
            .offsetY(5);

        val series2 : Line = cartesian.line(series2Mapping);
        series2.name("Income");
        series2.hovered().markers().enabled(true);
        series2.hovered().markers()
            .type(MarkerType.CIRCLE)
            .size(4);
        series2.tooltip()
            .position("right")
            .anchor("left | center")
            .offsetX(5)
            .offsetY(5);

        val series3 : Line  = cartesian.line(series3Mapping);
        series3.name("Expense");
        series3.hovered().markers().enabled(true);
        series3.hovered().markers()
            .type(MarkerType.CIRCLE)
            .size(4)
        series3.tooltip()
            .position("right")
            .anchor("left | center")
            .offsetX(5)
            .offsetY(5);

        cartesian.legend().enabled(true);
        cartesian.legend().fontSize(13);
        cartesian.legend().padding(0, 0, 10, 0);

        anyChartView.setChart(cartesian)
    }

    private fun createRadarChartIncomes(){
        //val anyChartView: AnyChartView = binding.anyChartView2
        //anyChartView.setProgressBar(binding.progressBar2)

        val radar: Radar = AnyChart.radar()

        radar.title("Stats de income-expenses por categoria")

        radar.yScale().minimum(0.0)
        radar.yScale().minimumGap(0.0)
        radar.yScale().ticks().interval(50.0)

        radar.xAxis().labels().padding(5.0, 5.0, 5.0, 5.0)

        radar.legend()
            .align(Align.CENTER)
            .enabled(true)

        val data: MutableList<DataEntry> = ArrayList()
        data.add(CustomDataEntry2("Strength", 136.0, 199.0, 43.0))
        data.add(CustomDataEntry2("Agility", 79.0, 125.0, 56.0))
        data.add(CustomDataEntry2("Stamina", 149.0, 173.0, 101.0))
        data.add(CustomDataEntry2("Intellect", 135.0, 33.0, 202.0))
        data.add(CustomDataEntry2("Spirit", 158.0, 64.0, 196.0))

        val set = Set.instantiate()
        set.data(data)
        val shamanData = set.mapAs("{ x: 'x', value: 'value' }")
        val warriorData = set.mapAs("{ x: 'x', value: 'value2' }")
        val priestData = set.mapAs("{ x: 'x', value: 'value3' }")

        val shamanLine: com.anychart.core.radar.series.Line? = radar.line(shamanData)
        shamanLine?.name("Paycheck")
        shamanLine?.markers()
            ?.enabled(true)
            ?.type(MarkerType.CIRCLE)
            ?.size(3.0)

        val warriorLine: com.anychart.core.radar.series.Line? = radar.line(warriorData)
        warriorLine?.name("Intellectual Propperty")
        warriorLine?.markers()
            ?.enabled(true)
            ?.type(MarkerType.CIRCLE)
            ?.size(3.0)

        val priestLine: com.anychart.core.radar.series.Line? = radar.line(priestData)
        priestLine?.name("Stocks")
        priestLine?.markers()
            ?.enabled(true)
            ?.type(MarkerType.CIRCLE)
            ?.size(3.0)

        radar.tooltip().format("Value: {%Value}")

        //anyChartView.setChart(radar)
    }

    private fun createRadarChartExpenses(){
        //val anyChartView: AnyChartView = binding.anyChartView3

        val radar: Radar = AnyChart.radar()

        radar.title("WoW base stats comparison radar chart: Shaman vs Warrior vs Priest")

        //anyChartView.setProgressBar(binding.progressBar3)
        radar.yScale().minimum(0.0)
        radar.yScale().minimumGap(0.0)
        radar.yScale().ticks().interval(50.0)

        radar.xAxis().labels().padding(5.0, 5.0, 5.0, 5.0)

        radar.legend()
            .align(Align.CENTER)
            .enabled(true)

        val data: MutableList<DataEntry> = ArrayList()
        data.add(CustomDataEntry2("Strength", 136.0, 199.0, 43.0))
        data.add(CustomDataEntry2("Agility", 79.0, 125.0, 56.0))
        data.add(CustomDataEntry2("Stamina", 149.0, 173.0, 101.0))
        data.add(CustomDataEntry2("Intellect", 135.0, 33.0, 202.0))
        data.add(CustomDataEntry2("Spirit", 158.0, 64.0, 196.0))

        val set = Set.instantiate()
        set.data(data)
        val shamanData = set.mapAs("{ x: 'x', value: 'value' }")
        val warriorData = set.mapAs("{ x: 'x', value: 'value2' }")
        val priestData = set.mapAs("{ x: 'x', value: 'value3' }")

        val shamanLine: com.anychart.core.radar.series.Line? = radar.line(shamanData)
        shamanLine?.name("Shaman")
        shamanLine?.markers()
            ?.enabled(true)
            ?.type(MarkerType.CIRCLE)
            ?.size(3.0)

        val warriorLine: com.anychart.core.radar.series.Line? = radar.line(warriorData)
        warriorLine?.name("Warrior")
        warriorLine?.markers()
            ?.enabled(true)
            ?.type(MarkerType.CIRCLE)
            ?.size(3.0)

        val priestLine: com.anychart.core.radar.series.Line? = radar.line(priestData)
        priestLine?.name("Priest")
        priestLine?.markers()
            ?.enabled(true)
            ?.type(MarkerType.CIRCLE)
            ?.size(3.0)

        radar.tooltip().format("Value: {%Value}")

        //anyChartView.setChart(radar)
    }

    private class CustomDataEntry2(x: String?, value: Number?, value2: Number?, value3: Number?) :
        ValueDataEntry(x, value) {
        init {
            setValue("value2", value2)
            setValue("value3", value3)
        }
    }

    data class CustomDataEntry(
        val year: String,
        val value1: Double,
        val value2: Double,
        val value3: Double
    ) : DataEntry()
}