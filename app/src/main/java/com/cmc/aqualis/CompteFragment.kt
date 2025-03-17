package com.cmc.aqualis

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.cmc.aqualis.database.UserDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CompteFragment : Fragment() {
    private lateinit var profileNameTextView: TextView
    private lateinit var firstNameTextView: TextView
    private lateinit var lastNameTextView: TextView
    private lateinit var genderTextView: TextView
    private lateinit var dobTextView: TextView
    private lateinit var weightTextView: TextView
    private lateinit var heightTextView: TextView
    private lateinit var signOutButton: Button
    private lateinit var backButton: ImageView
    private lateinit var editButton: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_compte, container, false)
        
        // Initialize views
        profileNameTextView = view.findViewById(R.id.profile_name)
        firstNameTextView = view.findViewById(R.id.first_name)
        lastNameTextView = view.findViewById(R.id.last_name)
        genderTextView = view.findViewById(R.id.gender)
        dobTextView = view.findViewById(R.id.dob)
        weightTextView = view.findViewById(R.id.weight)
        heightTextView = view.findViewById(R.id.height)
        signOutButton = view.findViewById(R.id.sign_out_button)
        backButton = view.findViewById(R.id.backButton)
        editButton = view.findViewById(R.id.editButton)
        
        // Load user data
        loadUserData()
        
        // Set up click listeners
        setupClickListeners()
        
        return view
    }
    
    private fun loadUserData() {
        // Get email from SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email", null)
        
        if (email != null) {
            // Load user data from database using the email
            lifecycleScope.launch {
                try {
                    val userDatabase = UserDatabase.getDatabase(requireContext())
                    val user = withContext(Dispatchers.IO) {
                        userDatabase.userDao().getUserByEmail(email)
                    }
                    
                    if (user != null) {
                        // Update UI with user data
                        profileNameTextView.text = "${user.firstName} ${user.lastName}"
                        firstNameTextView.text = user.firstName
                        lastNameTextView.text = user.lastName
                        genderTextView.text = user.gender
                        dobTextView.text = user.birthDate
                        weightTextView.text = "${user.weight} kg"
                        heightTextView.text = "${user.height} cm"
                    } else {
                        Toast.makeText(requireContext(), "Impossible de charger les données utilisateur", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // If no email is found, user is not logged in
            Toast.makeText(requireContext(), "Utilisateur non connecté", Toast.LENGTH_SHORT).show()
            navigateToSignIn()
        }
    }
    
    private fun setupClickListeners() {
        // Sign out button
        signOutButton.setOnClickListener {
            // Clear user preferences
            val sharedPreferences = requireActivity().getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
            
            // Navigate to sign in screen
            Toast.makeText(requireContext(), "Déconnexion réussie", Toast.LENGTH_SHORT).show()
            navigateToSignIn()
        }
        
        // Back button (optional, depending on your navigation)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
        
        // Edit button (for future implementation)
        editButton.setOnClickListener {
            Toast.makeText(requireContext(), "Fonctionnalité à venir", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun navigateToSignIn() {
        val intent = Intent(requireContext(), SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    companion object {
        @JvmStatic
        fun newInstance() = CompteFragment()
    }
}