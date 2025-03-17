package com.cmc.aqualis

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class WelcomeActivity : AppCompatActivity() {
    // Google Sign-In
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001 // Code de requête pour Google Sign-In

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialisation des vues
        val loginButton: Button = findViewById(R.id.loginButton)
        val signupButton: Button = findViewById(R.id.signupButton)
        val googleButton: Button = findViewById(R.id.googleButton)

        // Configuration de Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail() // Demander l'adresse e-mail de l'utilisateur
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Gestion du clic sur le bouton "Se connecter"
        loginButton.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        // Gestion du clic sur le bouton "S'inscrire"
        signupButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
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
            account?.let {
                // Connexion réussie
                Toast.makeText(this, "Connexion Google réussie", Toast.LENGTH_SHORT).show()

                // Récupérer les informations de l'utilisateur
                val email = account.email ?: ""
                val name = account.displayName ?: ""

                // Rediriger vers l'écran d'accueil ou l'écran de complétion d'inscription
                val intent = Intent(this, HomeActivity::class.java) // Remplacez par l'écran approprié
                intent.putExtra("email", email)
                intent.putExtra("name", name)
                startActivity(intent)
                finish()
            }
        } catch (e: ApiException) {
            // Échec de la connexion Google
            Toast.makeText(this, "Échec de la connexion Google", Toast.LENGTH_SHORT).show()
        }
    }
}