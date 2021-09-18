package com.amadev.rando.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.amadev.rando.R
import com.amadev.rando.databinding.CustomUpcomingMoviesRecyclerviewPatternBinding
import com.amadev.rando.model.MovieDetailsResults
import com.amadev.rando.util.Util.getProgressDrawable
import com.amadev.rando.util.Util.loadImageWithGlide

class UpcomingMoviesRecyclerViewAdapter(
    val view: View,
    var context: Context,
    var list: ArrayList<MovieDetailsResults>
) : RecyclerView.Adapter<UpcomingMoviesRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(val binding: CustomUpcomingMoviesRecyclerviewPatternBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CustomUpcomingMoviesRecyclerviewPatternBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            setUpViews(holder, position)

            itemView.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable("movieDetails", list[position])
                Navigation.createNavigateOnClickListener(
                    R.id.action_mainFragment_to_movieDetailsFragment,
                    bundle
                ).onClick(holder.itemView)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun setUpViews(holder: ViewHolder, position: Int) {
        with(holder) {
            binding.apply {
                movieName.text = list[position].title
                rating.text = list[position].vote_average.toString()
                ratingBar.rating = 1f
                movieImage.loadImageWithGlide(
                    list[position].poster_path,
                    getProgressDrawable(context)
                )
            }
        }
    }
}
