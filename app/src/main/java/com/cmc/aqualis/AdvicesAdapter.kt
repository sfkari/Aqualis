package com.cmc.aqualis

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cmc.aqualis.Conseils

class AdvicesAdapter(private val advices: List<Conseils>) :
    RecyclerView.Adapter<AdvicesAdapter.AdviceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdviceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_advices, parent, false)
        return AdviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdviceViewHolder, position: Int) {
        val advice = advices[position]
        holder.bind(advice)
    }

    override fun getItemCount(): Int = advices.size

    inner class AdviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)

        fun bind(conseils: Conseils) {
            imageView.setImageResource(conseils.icon)
            titleTextView.text = conseils.title
            descriptionTextView.text = conseils.description
        }
    }
}
