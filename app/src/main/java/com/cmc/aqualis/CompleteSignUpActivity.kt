package com.cmc.aqualis

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.cmc.aqualis.database.User
import com.cmc.aqualis.database.UserDatabase
import kotlinx.coroutines.launch
import java.util.*

class CompleteSignUpActivity : AppCompatActivity() {
    private var lastName: EditText? = null
    private var firstName: EditText? = null
    private var birthDateInput: EditText? = null
    private var genderGroup: RadioGroup? = null
    private var weightInput: EditText? = null
    private var heightInput: EditText? = null
    private var connectButton: Button? = null
    private var backButton: ImageButton? = null
    private lateinit var userDatabase: UserDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Récupération des vues
        lastName = findViewById(R.id.lastName)
        firstName = findViewById(R.id.firstName)
        birthDateInput = findViewById(R.id.birthDateInput)
        genderGroup = findViewById(R.id.genderGroup)
        weightInput = findViewById(R.id.weightInput)
        heightInput = findViewById(R.id.heightInput)
        connectButton = findViewById(R.id.connectButton)
        backButton = findViewById(R.id.backButton)

        // Initialisation de la base de données Room
        userDatabase = UserDatabase.getDatabase(applicationContext)

        // Configuration du DatePicker pour la date de naissance
        setupDatePicker()

        // Récupération des données de l'Intent
        val email = intent.getStringExtra("email")
        val password = intent.getStringExtra("password")

        if (email == null || password == null) {
            Toast.makeText(this, "Erreur : email ou mot de passe manquant", Toast.LENGTH_SHORT).show()
            return
        }

        // Gestion du clic sur le bouton "Terminer l'inscription"
        connectButton?.setOnClickListener {
            handleContinueButtonClick(email, password)
        }

        // Gestion du clic sur le bouton "Retour"
        backButton?.setOnClickListener {
            finish()
        }
    }

    private fun setupDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -18) // Limite l'âge minimum à 18 ans

        birthDateInput?.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { view, selectedYear, selectedMonth, selectedDay ->
                    birthDateInput?.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
                },
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH]
            )
            datePicker.datePicker.maxDate = calendar.timeInMillis
            datePicker.show()
        }
    }

    private fun handleContinueButtonClick(email: String, password: String) {
        val lastNameStr = lastName?.text.toString().trim()
        val firstNameStr = firstName?.text.toString().trim()
        val birthDateStr = birthDateInput?.text.toString().trim()
        val weightStr = weightInput?.text.toString().trim()
        val heightStr = heightInput?.text.toString().trim()

        val selectedGenderId = genderGroup?.checkedRadioButtonId
        val gender = when (selectedGenderId) {
            R.id.maleRadio -> "Homme"
            R.id.femaleRadio -> "Femme"
            else -> ""
        }

        if (lastNameStr.isEmpty() || firstNameStr.isEmpty() || birthDateStr.isEmpty() ||
            weightStr.isEmpty() || heightStr.isEmpty() || gender.isEmpty()) {
            Toast.makeText(this, "Tous les champs sont obligatoires", Toast.LENGTH_SHORT).show()
            return
        }

        val user = User(
            email = email,
            password = password,
            lastName = lastNameStr,
            firstName = firstNameStr,
            birthDate = birthDateStr,
            gender = gender,
            weight = weightStr.toDouble(),
            height = heightStr.toDouble()
        )

        lifecycleScope.launch {
            try {
                userDatabase.userDao().insert(user)
                Log.d("CompleteSignUp", "User inserted: $email")
                
                // Enregistrer les informations de l'utilisateur dans SharedPreferences
                saveUserInfoToPreferences(email, "$firstNameStr $lastNameStr")
                
                // Afficher un message de succès
                Toast.makeText(this@CompleteSignUpActivity, "Inscription réussie !", Toast.LENGTH_SHORT).show()
                
                // Rediriger vers l'écran d'accueil
                val intent = Intent(this@CompleteSignUpActivity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                Log.e("CompleteSignUp", "Error inserting user", e)
                Toast.makeText(this@CompleteSignUpActivity, "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun saveUserInfoToPreferences(email: String, username: String) {
        val sharedPreferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.putString("username", username)
        editor.putBoolean("is_logged_in", true)
        editor.apply()
    }
}
