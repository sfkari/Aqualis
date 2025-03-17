package com.cmc.aqualis

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ConseilsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_conseils, container, false)

        recyclerView = rootView.findViewById(R.id.recyclerView)

        val conseils = listOf(
            Conseils(R.drawable.lubrication, "Lubrification", "L'eau aide à lubrifier les articulations et les yeux. Pensez à boire régulièrement pour éviter la sécheresse oculaire et protéger vos articulations."),
            Conseils(R.drawable.temperature, "Régulation de la température", "L’eau permet au corps de réguler sa température grâce à la transpiration. Buvez davantage en cas de chaleur ou d’activité physique pour éviter la surchauffe du corps."),
            Conseils(R.drawable.energy, "Aide à convertir la nourriture en énergie", "L’hydratation est essentielle pour un métabolisme efficace. Buvez de l’eau avant, pendant et après les repas pour faciliter l’absorption des nutriments."),
            Conseils(R.drawable.digestion, "Digestion", "L’eau participe activement à la digestion et prévient la constipation. Consommez de l’eau régulièrement, surtout avec des fibres (légumes, fruits, céréales complètes)."),
            Conseils(R.drawable.skin, "Peau en bonne santé", "Une bonne hydratation améliore l’élasticité de la peau et réduit la sécheresse. Buvez suffisamment d’eau et mangez des aliments riches en eau (concombre, pastèque, orange…).")
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = AdvicesAdapter(conseils)
            setHasFixedSize(true)
        }

        val shareButton: Button = rootView.findViewById(R.id.shareButton)
        shareButton.setOnClickListener {
            shareAdvice()
        }

        return rootView
    }

    private fun shareAdvice() {
        val conseilsText = "Voici quelques conseils d'hydratation :\n" +
                "1. Lubrification : L'eau aide à lubrifier les articulations et les yeux.\n" +
                "2. Régulation de la température : L’eau permet au corps de réguler sa température.\n" +
                "3. Aide à convertir la nourriture en énergie : L’hydratation est essentielle pour un métabolisme efficace.\n" +
                "4. Digestion : L’eau participe activement à la digestion et prévient la constipation.\n" +
                "5. Peau en bonne santé : Une bonne hydratation améliore l’élasticité de la peau."

        val imageUri = Uri.parse("android.resource://${requireContext().packageName}/drawable/${R.drawable.conseils}")

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/*"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Conseils d'hydratation")
        //shareIntent.putExtra(Intent.EXTRA_TEXT, conseilsText)
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)

        if (shareIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(Intent.createChooser(shareIntent, "Partager via"))
        } else {
            Toast.makeText(requireContext(), "Aucune application de partage disponible", Toast.LENGTH_SHORT).show()
        }
    }
}
