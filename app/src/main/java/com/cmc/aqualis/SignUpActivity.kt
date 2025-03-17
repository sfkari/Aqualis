package com.cmc.aqualis

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.cmc.aqualis.database.UserDatabase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpActivity : AppCompatActivity() {
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var googleButton: Button
    private lateinit var backButton: AppCompatImageButton
    private lateinit var userDatabase: UserDatabase

    // Google Sign-In
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001 // Code de requête pour Google Sign-In

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialisation des vues
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        googleButton = findViewById(R.id.googleButton)
        backButton = findViewById(R.id.backButton)

        // Initialisation de la base de données Room
        userDatabase = UserDatabase.getDatabase(applicationContext)

        // Configuration de Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail() // Demander l'adresse e-mail de l'utilisateur
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Gestion du clic sur le bouton "S'inscrire"
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Tous les champs sont obligatoires", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Vérifier si l'email existe déjà dans la base de données
            lifecycleScope.launch {
                val emailExists = withContext(Dispatchers.IO) {
                    userDatabase.userDao().isEmailExists(email)
                }

                if (emailExists) {
                    // Afficher un message d'erreur si l'email existe déjà
                    Toast.makeText(this@SignUpActivity, "Cet email est déjà utilisé", Toast.LENGTH_SHORT).show()
                } else {
                    // Passer à l'activité suivante si l'email n'existe pas
                    val intent = Intent(this@SignUpActivity, CompleteSignUpActivity::class.java)
                    intent.putExtra("email", email)
                    intent.putExtra("password", password)
                    startActivity(intent)
                }
            }
        }

        // Gestion du clic sur le bouton "Retour"
        backButton.setOnClickListener {
            finish()
        }

        // Gestion du clic sur le bouton Google
        googleButton.setOnClickListener {
            signInWithGoogle()
        }
    }

    // Méthode pour lancer la connexion Google
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    // Gestion du résultat de la connexion Google
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    // Méthode pour gérer le résultat de la connexion Google
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                Log.d("GoogleSignIn", "Google Sign-In succeeded: ${account.email}")
                val intent = Intent(this, CompleteSignUpActivity::class.java)
                intent.putExtra("email", account.email)
                intent.putExtra("name", account.displayName)
                startActivity(intent)
                finish()
            }
        } catch (e: ApiException) {
            Log.e("GoogleSignIn", "Google Sign-In failed", e)
            Toast.makeText(this, "Échec de la connexion Google", Toast.LENGTH_SHORT).show()
        }
    }
}
