package com.amadev.rando.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amadev.rando.databinding.CustomGenreRecyclerviewPatternBinding

class GenresRecyclerViewAdapter(val view: View, var context: Context, var list: ArrayList<String>) :
    RecyclerView.Adapter<GenresRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(val binding: CustomGenreRecyclerviewPatternBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CustomGenreRecyclerviewPatternBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setUpViews(holder, position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun setUpViews(holder: ViewHolder, position: Int) {
        with(holder) {
            binding.apply {
                genreName.text = list[position]
            }
        }
    }
}
