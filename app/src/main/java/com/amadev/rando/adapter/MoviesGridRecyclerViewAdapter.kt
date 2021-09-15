package com.amadev.rando.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.amadev.rando.R
import com.amadev.rando.databinding.CustomMoviesGridRecyclerviewPatternBinding
import com.amadev.rando.model.MovieDetailsResults
import com.amadev.rando.util.Util.getProgressDrawable
import com.amadev.rando.util.Util.loadImageWithGlide

class MoviesGridRecyclerViewAdapter(
    val view: View,
    var context: Context,
    var list: ArrayList<MovieDetailsResults>,
    private val action : Int
) : RecyclerView.Adapter<MoviesGridRecyclerViewAdapter.ViewHolder>() {

    val activity = context as FragmentActivity
    val bundle = Bundle()

    class ViewHolder(val binding: CustomMoviesGridRecyclerviewPatternBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CustomMoviesGridRecyclerviewPatternBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            binding.apply {
                if (list.isEmpty()) {
                    movieName.text = context.getString(R.string.noMoviesHere)
                } else {
                    setUpViews(list[position], holder)

                    holder.itemView.setOnClickListener {
                        bundle.putParcelable("movieDetails", list[position])
                        navigateAndSendData(bundle, holder)
                    }
                }
            }

        }
    }

    private fun navigateAndSendData(bundle: Bundle?, holder: ViewHolder) {
        Navigation.createNavigateOnClickListener(
            action,
            bundle
        ).onClick(holder.itemView)
    }

    private fun setUpViews(
        list: MovieDetailsResults,
        holder: ViewHolder
    ) {
        with(holder) {
            binding.apply {
                movieName.text = list.title
                rating.text = list.vote_average.toString()
                ratingBar.rating = 1f
                movieImage.loadImageWithGlide(
                    list.poster_path,
                    getProgressDrawable(context)
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}







