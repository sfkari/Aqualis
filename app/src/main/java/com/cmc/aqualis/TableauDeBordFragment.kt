package com.cmc.aqualis

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.cmc.aqualis.database.DailyConsumption
import com.cmc.aqualis.database.UserDatabase
import com.cmc.aqualis.database.WaterConsumption
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TableauDeBordFragment : Fragment() {
    private var consumed = 0f
    private val dailyGoal = 1460f
    private val records = mutableListOf<Int>()
    private lateinit var progressCircle: ProgressCircleView
    private lateinit var userDatabase: UserDatabase
    private var userId: String = "" // ID de l'utilisateur
    private lateinit var barChart: BarChart
    private lateinit var waterIntakeChart: BarChart
    private lateinit var tvTotalConsumption: TextView
    private lateinit var tvAverageConsumption: TextView
    private lateinit var tvStreakDays: TextView
    private lateinit var tvUsername: TextView
    private lateinit var backButton: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tableau_de_bord, container, false)

        // Initialisation de la base de données Room
        userDatabase = UserDatabase.getDatabase(requireContext())

        // Initialiser les vues
        initViews(view)
        
        // Récupérer l'email de l'utilisateur depuis SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email", null)
        val username = sharedPreferences.getString("username", "Utilisateur")
        
        tvUsername.text = username

        if (email != null) {
            getUserByEmail(email)  // Récupère l'utilisateur avec l'email depuis Room
            loadWaterConsumptionData(email)
        } else {
            Toast.makeText(requireContext(), "Utilisateur non trouvé", Toast.LENGTH_SHORT).show()
        }

        val progressText: TextView = view.findViewById(R.id.progressText)
        val listView: ListView = view.findViewById(R.id.listView)
        val addWater: ImageButton = view.findViewById(R.id.addWater)
        progressCircle = view.findViewById(R.id.progressCircle)

        val adapter = RecordAdapter(requireContext(), records)
        listView.adapter = adapter

        addWater.setOnClickListener {
            addWaterIntake(adapter, progressText)
        }
        
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        setupBarChart()

        return view
    }
    
    private fun initViews(view: View) {
        progressCircle = view.findViewById(R.id.progressCircle)
        barChart = view.findViewById(R.id.barChart)
        waterIntakeChart = view.findViewById(R.id.waterIntakeChart)
        tvTotalConsumption = view.findViewById(R.id.tvTotalConsumption)
        tvAverageConsumption = view.findViewById(R.id.tvAverageConsumption)
        tvStreakDays = view.findViewById(R.id.tvStreakDays)
        tvUsername = view.findViewById(R.id.username)
        backButton = view.findViewById(R.id.backButton)
        
        setupWaterIntakeChart()
    }

    private fun getUserByEmail(email: String) {
        lifecycleScope.launch {
            val user = userDatabase.userDao().getUserByEmail(email)
            if (user != null) {
                userId = user.email // Utilisation de l'email comme ID de l'utilisateur
            } else {
                Toast.makeText(requireContext(), "Utilisateur non trouvé", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun loadWaterConsumptionData(email: String) {
        lifecycleScope.launch {
            try {
                // Récupérer la consommation d'aujourd'hui
                val calendar = Calendar.getInstance()
                val today = getDateWithoutTime(calendar.time)
                
                val todayConsumption = withContext(Dispatchers.IO) {
                    userDatabase.waterConsumptionDao().getTotalConsumptionForDay(email, today)
                }
                
                // Mettre à jour la consommation actuelle
                consumed = todayConsumption.toFloat()
                updateWaterDisplay()
                
                // Charger l'historique pour le graphique
                loadConsumptionHistory(email)
                
                // Calculer et afficher les statistiques
                calculateStatistics(email)
                
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Erreur lors du chargement des données: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun calculateStatistics(email: String) {
        lifecycleScope.launch {
            try {
                // Obtenir le total de la consommation
                val totalConsumption = withContext(Dispatchers.IO) {
                    userDatabase.waterConsumptionDao().getTotalConsumption(email)
                }
                
                // Obtenir la moyenne quotidienne
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, -7) // 7 derniers jours
                val startDate = getDateWithoutTime(calendar.time)
                
                val weekConsumption = withContext(Dispatchers.IO) {
                    userDatabase.waterConsumptionDao().getConsumptionSinceDate(email, startDate)
                }
                
                val averageDaily = if (weekConsumption.isNotEmpty()) {
                    weekConsumption.sumOf { it.amount } / weekConsumption.size
                } else {
                    0.0
                }
                
                // Calculer le nombre de jours consécutifs avec consommation
                val streak = calculateStreak(email)
                
                withContext(Dispatchers.Main) {
                    tvTotalConsumption.text = "${String.format("%.1f", totalConsumption)} ml"
                    tvAverageConsumption.text = "${String.format("%.1f", averageDaily)} ml/jour"
                    tvStreakDays.text = "$streak jours"
                }
                
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Erreur lors du calcul des statistiques: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private suspend fun calculateStreak(email: String): Int {
        var streak = 0
        val calendar = Calendar.getInstance()
        val today = getDateWithoutTime(calendar.time)
        
        var checkDate = today
        var hasConsumption = true
        
        while (hasConsumption) {
            val consumption = withContext(Dispatchers.IO) {
                userDatabase.waterConsumptionDao().getTotalConsumptionForDay(email, checkDate)
            }
            
            if (consumption > 0) {
                streak++
                calendar.time = checkDate
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                checkDate = getDateWithoutTime(calendar.time)
            } else {
                hasConsumption = false
            }
        }
        
        return streak
    }
    
    private fun loadConsumptionHistory(email: String) {
        lifecycleScope.launch {
            try {
                // Récupérer les 7 derniers jours
                val calendar = Calendar.getInstance()
                val endDate = getDateWithoutTime(calendar.time)
                calendar.add(Calendar.DAY_OF_YEAR, -6) // 7 jours au total
                val startDate = getDateWithoutTime(calendar.time)
                
                val dailyConsumption = withContext(Dispatchers.IO) {
                    userDatabase.waterConsumptionDao().getDailyConsumptionBetweenDates(email, startDate, endDate)
                }
                
                // Convertir les résultats en paires (Date, Double) pour l'affichage
                val consumptionPairs = dailyConsumption.map { Pair(it.date, it.total) }
                
                updateBarChart(consumptionPairs)
                
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Erreur lors du chargement de l'historique: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun updateBarChart(dailyData: List<Pair<Date, Double>>) {
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()
        val dateFormat = SimpleDateFormat("EEE", Locale.FRANCE)
        
        // Créer une map des 7 derniers jours avec 0 comme valeur par défaut
        val calendar = Calendar.getInstance()
        val dateMap = mutableMapOf<String, Double>()
        
        for (i in 6 downTo 0) {
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            val date = calendar.time
            val dateLabel = dateFormat.format(date)
            dateMap[dateLabel] = 0.0
            labels.add(dateLabel)
            calendar.add(Calendar.DAY_OF_YEAR, i) // Réinitialiser à aujourd'hui
        }
        
        // Remplir avec les données réelles
        for ((date, amount) in dailyData) {
            val dateLabel = dateFormat.format(date)
            dateMap[dateLabel] = amount
        }
        
        // Créer les entrées pour le graphique
        dateMap.values.forEachIndexed { index, value ->
            entries.add(BarEntry(index.toFloat(), value.toFloat()))
        }
        
        val dataSet = BarDataSet(entries, "Consommation d'eau (ml)")
        dataSet.color = resources.getColor(R.color.vert, null)
        
        val data = BarData(dataSet)
        data.barWidth = 0.6f
        
        barChart.data = data
        barChart.invalidate()
    }
    
    private fun setupBarChart() {
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.setDrawBarShadow(false)
        barChart.setDrawValueAboveBar(true)
        
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        
        val labels = ArrayList<String>()
        val dateFormat = SimpleDateFormat("EEE", Locale.FRANCE)
        val calendar = Calendar.getInstance()
        
        for (i in 6 downTo 0) {
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            labels.add(dateFormat.format(calendar.time))
            calendar.add(Calendar.DAY_OF_YEAR, i) // Réinitialiser à aujourd'hui
        }
        
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        
        barChart.axisLeft.setDrawGridLines(false)
        barChart.axisRight.isEnabled = false
        
        // Animation
        barChart.animateY(1000)
    }
    
    private fun getDateWithoutTime(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    private fun addWaterIntake(adapter: RecordAdapter, progressText: TextView) {
        // Ajout de 100 ml de consommation
        records.add(100)
        consumed += 100
        adapter.notifyDataSetChanged()
        updateWaterDisplay()

        // Vérifie que l'ID utilisateur est récupéré
        if (userId.isNotEmpty()) {
            // Enregistre la consommation d'eau dans la base de données
            val waterConsumption = WaterConsumption(
                userId = userId,  // Utilise l'ID de l'utilisateur récupéré
                amount = 100.0,
                date = Calendar.getInstance().time // Utilise la date actuelle
            )

            // Enregistrement asynchrone dans Room
            lifecycleScope.launch {
                try {
                    userDatabase.waterConsumptionDao().insert(waterConsumption)
                    Toast.makeText(requireContext(), "100 ml ajoutés à votre consommation", Toast.LENGTH_SHORT).show()
                    
                    // Mettre à jour les statistiques et le graphique
                    loadWaterConsumptionData(userId)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "Erreur : ID utilisateur non disponible", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun updateWaterDisplay() {
        val progressText: TextView? = view?.findViewById(R.id.progressText)
        progressText?.text = "${consumed.toInt()} / ${dailyGoal.toInt()} ml"
        progressCircle.updateProgress(consumed)
        
        // Update column chart with current progress
        updateWaterIntakeChart()
    }
    
    private fun setupWaterIntakeChart() {
        // Configure the column chart appearance
        waterIntakeChart.description.isEnabled = false
        waterIntakeChart.legend.isEnabled = true // Enable legend to show labels
        waterIntakeChart.setDrawGridBackground(false)
        waterIntakeChart.setDrawBarShadow(false)
        waterIntakeChart.setDrawValueAboveBar(true)
        waterIntakeChart.setPinchZoom(false)
        waterIntakeChart.setScaleEnabled(false)
        waterIntakeChart.isDoubleTapToZoomEnabled = false
        
        // Configure X axis
        val xAxis = waterIntakeChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return when (value.toInt()) {
                    0 -> "Consommé"
                    1 -> "Objectif"
                    else -> ""
                }
            }
        }
        
        // Configure Y axis
        val leftAxis = waterIntakeChart.axisLeft
        leftAxis.setDrawGridLines(true)
        leftAxis.axisMinimum = 0f
        leftAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "${value.toInt()} ml"
            }
        }
        
        // Disable right Y axis
        waterIntakeChart.axisRight.isEnabled = false
        
        // Set initial data
        updateWaterIntakeChart()
    }
    
    private fun updateWaterIntakeChart() {
        // Create two separate datasets instead of one
        val consumedEntries = ArrayList<BarEntry>()
        consumedEntries.add(BarEntry(0f, consumed))
        
        val goalEntries = ArrayList<BarEntry>()
        goalEntries.add(BarEntry(1f, dailyGoal))
        
        // Create datasets and customize appearance
        val consumedDataSet = BarDataSet(consumedEntries, "Consommé")
        consumedDataSet.color = resources.getColor(R.color.vert, null)
        consumedDataSet.valueTextSize = 12f
        consumedDataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "${value.toInt()} ml"
            }
        }
        
        val goalDataSet = BarDataSet(goalEntries, "Objectif")
        goalDataSet.color = resources.getColor(R.color.gris_clair, null)
        goalDataSet.valueTextSize = 12f
        goalDataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "${value.toInt()} ml"
            }
        }
        
        // Create bar data with multiple datasets
        val data = BarData(consumedDataSet, goalDataSet)
        data.barWidth = 0.4f
        
        // Set data and update chart
        waterIntakeChart.data = data
        
        // Refresh the chart
        waterIntakeChart.invalidate()
        
        // Animation
        waterIntakeChart.animateY(1000)
    }
}
