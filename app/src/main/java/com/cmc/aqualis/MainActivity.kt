package com.cmc.aqualis

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var onboardingItemsAdapter: OnboardingItemsAdapter
    private lateinit var indicatorsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        indicatorsContainer = findViewById(R.id.indicatorsContainer)

        setOnboardingItems()
        setupIndicators()
        setCurrentIndicator(0)
    }

    private fun setOnboardingItems() {
        onboardingItemsAdapter = OnboardingItemsAdapter(
            listOf(
                OnboardingItem(
                    onboardingImage = R.drawable.onboarding1,
                    title = "Reste Hydraté Toute la Journée",
                    description = "Recevez des rappels personnalisés pour boire de l’eau et restez en pleine forme !"
                ),
                OnboardingItem(
                    onboardingImage = R.drawable.onboarding2,
                    title = "Suivez Votre Hydratation Facilement",
                    description = "Enregistrez chaque verre et suivez vos progrès pour une meilleure santé."
                ),
                OnboardingItem(
                    onboardingImage = R.drawable.onboarding3,
                    title = "Atteignez Vos Objectifs d’Hydratation",
                    description = "Fixez-vous des objectifs et transformez l’hydratation en une habitude saine !"
                )
            )
        )
        val onboardingViewPager = findViewById<ViewPager2>(R.id.onboardingViewPager)
        onboardingViewPager.adapter = onboardingItemsAdapter
        onboardingViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
        (onboardingViewPager.getChildAt(0) as RecyclerView).overScrollMode =
            RecyclerView.OVER_SCROLL_NEVER

        val imageNext = findViewById<ImageView>(R.id.imageNext)
        val buttonGetStarted = findViewById<MaterialButton>(R.id.buttonGetStarted)
        val textSkip = findViewById<TextView>(R.id.textSkip)

        imageNext.setOnClickListener {
            val currentItem = onboardingViewPager.currentItem
            val itemCount = onboardingItemsAdapter.itemCount

            if (currentItem + 1 < itemCount) {
                onboardingViewPager.currentItem += 1
                if (onboardingViewPager.currentItem == itemCount - 1) {
                    buttonGetStarted.visibility = View.VISIBLE
                    imageNext.visibility = View.INVISIBLE
                }
            } else {
                navigateToHomeActivity()
            }
        }

        textSkip.setOnClickListener {
            navigateToHomeActivity()
        }

        buttonGetStarted.setOnClickListener {
            navigateToHomeActivity()
        }

    }

    private fun navigateToHomeActivity() {
        startActivity(Intent(applicationContext, WelcomeActivity::class.java))
        finish()
    }

    private fun setupIndicators() {
        indicatorsContainer.removeAllViews()
        val indicators = arrayOfNulls<ImageView>(onboardingItemsAdapter.itemCount)
        val layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
            setMargins(8, 0, 8, 0)
        }

        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext).apply {
                setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive_background
                    )
                )
                this.layoutParams = layoutParams
            }
            indicatorsContainer.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(position: Int) {
        val childCount = indicatorsContainer.childCount
        for (i in 0 until childCount) {
            val imageView = indicatorsContainer.getChildAt(i) as ImageView
            imageView.setImageDrawable(
                ContextCompat.getDrawable(
                    applicationContext,
                    if (i == position) R.drawable.indicator_active_background
                    else R.drawable.indicator_inactive_background
                )
            )
        }
    }
}
