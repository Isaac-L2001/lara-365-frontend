package com.lara365.experience.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lara365.experience.DetailActivity
import com.lara365.experience.databinding.ItemEstablishmentBinding
import com.lara365.experience.models.Establishment

class EstablishmentAdapter(private val establishments: List<Establishment>) :
    RecyclerView.Adapter<EstablishmentAdapter.EstablishmentViewHolder>() {

    // 1. Clase interna corregida para usar ViewBinding
    inner class EstablishmentViewHolder(val binding: ItemEstablishmentBinding) :
        RecyclerView.ViewHolder(binding.root)

    // 2. Inflamos usando el Binding directamente (más limpio)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EstablishmentViewHolder {
        val binding = ItemEstablishmentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EstablishmentViewHolder(binding)
    }

    //EstablishmentAdapter.kt
    // 3. Ahora los nombres coinciden perfectamente
    override fun onBindViewHolder(holder: EstablishmentViewHolder, position: Int) {
        val item = establishments[position]

        with(holder.binding) {
            tvId.text = "${item.id}"
            tvName.text = item.nombre_comercio
            tvCategory.text = item.categoria
            tvDiscount.text = "${item.porcentaje_descuento}%"
            tvAddress.text = item.ubicacion

            Glide.with(root.context)
                .load(item.image_url)
                .centerCrop()
                .into(ivEstablishment)

            // Configuración del click para ir al detalle
            root.setOnClickListener {
                val intent = Intent(root.context, DetailActivity::class.java).apply {
                    putExtra("EXTRA_ESTABLISHMENT", item)
                    putExtra("EXTRA_ID", item.id)
                }
                root.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = establishments.size
}