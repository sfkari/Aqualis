package com.cmc.aqualis

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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

class SignInActivity : AppCompatActivity() {
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var signInButton: Button
    private lateinit var backButton: ImageButton
    private lateinit var showPasswordButton: ImageView
    private lateinit var rememberMeCheck: CheckBox
    private lateinit var forgotPassword: TextView
    private lateinit var signUpLink: TextView
    private lateinit var googleButton: Button
    private lateinit var userDatabase: UserDatabase

    // Google Sign-In
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001 // Code de requête pour Google Sign-In

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialisation des vues
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        signInButton = findViewById(R.id.loginButton)
        backButton = findViewById(R.id.backButton)
        showPasswordButton = findViewById(R.id.showPasswordButton)
        rememberMeCheck = findViewById(R.id.rememberMeCheck)
        forgotPassword = findViewById(R.id.forgotPassword)
        signUpLink = findViewById(R.id.signUpLink)
        googleButton = findViewById(R.id.googleButton)

        // Initialisation de la base de données Room
        userDatabase = UserDatabase.getDatabase(applicationContext)

        // Configuration de Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail() // Demander l'adresse e-mail de l'utilisateur
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Logique pour afficher/masquer le mot de passe
        showPasswordButton.setOnClickListener {
            if (passwordInput.transformationMethod == null) {
                passwordInput.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
                showPasswordButton.setImageResource(R.drawable.ic_eye)
            } else {
                passwordInput.transformationMethod = null
                showPasswordButton.setImageResource(R.drawable.ic_eye_off)
            }
        }

        // Connexion de l'utilisateur
        signInButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Recherche de l'utilisateur dans la base de données
            lifecycleScope.launch {
                val user = withContext(Dispatchers.IO) {
                    userDatabase.userDao().getUserByEmail(email)
                }

                if (user == null) {
                    Toast.makeText(this@SignInActivity, "Utilisateur non trouvé", Toast.LENGTH_SHORT).show()
                } else if (user.password != password) {
                    Toast.makeText(this@SignInActivity, "Mot de passe incorrect", Toast.LENGTH_SHORT).show()
                } else {
                    // Connexion réussie
                    Toast.makeText(this@SignInActivity, "Connexion réussie", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@SignInActivity, HomeActivity::class.java)
                    startActivity(intent)

                }
            }
        }

        // Redirection vers l'écran d'inscription
        signUpLink.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Redirection vers l'écran de réinitialisation du mot de passe
        forgotPassword.setOnClickListener {
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
        }

        // Action pour se connecter avec Google
        googleButton.setOnClickListener {
            signInWithGoogle()
        }

        // Retour à l'écran précédent
        backButton.setOnClickListener {
            finish()
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

                // Rediriger vers l'écran d'accueil
                val intent = Intent(this, HomeActivity::class.java)
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