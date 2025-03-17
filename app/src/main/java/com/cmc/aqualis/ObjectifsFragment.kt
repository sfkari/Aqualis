package com.cmc.aqualis

import androidx.fragment.app.Fragment

/*
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.patrykandpatrick.vico.core.chart.column.ColumnChart
import com.patrykandpatrick.vico.core.chart.column.ColumnChartStyle
import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.views.chart.ChartView
import kotlinx.coroutines.launch

class ObjectifsFragment : Fragment() {

    // Déclaration des variables
    private lateinit var chartView: ChartView
    private lateinit var db: AppDatabase
    private lateinit var dao: ConsumptionDao
    private lateinit var chartEntryModelProducer: ChartEntryModelProducer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflater le layout du fragment
        val view = inflater.inflate(R.layout.fragment_objectifs, container, false)

        // Initialiser les composants
        chartView = view.findViewById(R.id.chartView)
        db = AppDatabase.getDatabase(requireContext())
        dao = db.consumptionDao()

        // Initialiser le producteur d'entrées pour le graphique
        chartEntryModelProducer = ChartEntryModelProducer()

        // Charger les données du graphique
        loadChartData()

        return view
    }

    private fun loadChartData() {
        lifecycleScope.launch {
            // Récupérer les enregistrements de la base de données
            val records = dao.getAllRecords()

            // Grouper les enregistrements par jour et calculer la somme des quantités
            val groupedRecords = records.groupBy { it.timestamp / (24 * 60 * 60 * 1000) } // Grouper par jour
                .map { (day, records) -> day to records.sumOf { it.amount } }
                .sortedBy { it.first } // Trier par jour

            // Créer les entrées du graphique
            val chartEntries = groupedRecords.map { (day, amount) ->
                object : ChartEntry {
                    override val x = day.toFloat() // Jour sur l'axe X
                    override val y = amount.toFloat() // Quantité sur l'axe Y
                    override fun withY(y: Float): ChartEntry {
                        return object : ChartEntry {
                            override val x = this@ObjectifsFragment.x
                            override val y = y
                            override fun withY(y: Float): ChartEntry = this.withY(y)
                        }
                    }
                }
            }

            // Mettre à jour les entrées du graphique
            chartEntryModelProducer.setEntries(entryModelOf(chartEntries))

            // Configurer le graphique en colonnes
            val columnChart = ColumnChart(
                columnChartStyle = ColumnChartStyle(
                    columnColors = listOf(Color.BLUE), // Couleur des colonnes
                    columnWidth = 0.5f // Largeur des colonnes
                )
            )

            // Appliquer le graphique à la vue
            chartView.setChart(columnChart)
            chartView.chart?.setEntryProducer(chartEntryModelProducer)
        }
    }
}
*/

class ObjectifsFragment : Fragment() {}