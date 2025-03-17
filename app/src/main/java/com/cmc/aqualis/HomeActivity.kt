package com.cmc.aqualis

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        replaceFragment(TableauDeBordFragment())

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.menu.clear()
        menuInflater.inflate(R.menu.bottom_nav_menu, bottomNavigationView.menu)

        bottomNavigationView.background = null

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.tableauDeBord -> replaceFragment(TableauDeBordFragment())
                R.id.objectifs -> replaceFragment(ObjectifsFragment())
                R.id.conseils -> replaceFragment(ConseilsFragment())
                R.id.compte -> replaceFragment(CompteFragment())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }
}

