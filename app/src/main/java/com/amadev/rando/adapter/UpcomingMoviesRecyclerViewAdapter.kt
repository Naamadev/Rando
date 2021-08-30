package com.amadev.rando.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.amadev.rando.databinding.CustomUpcomingMoviesRecyclerviewPatternBinding
import com.amadev.rando.model.MovieDetailsResults
import com.amadev.rando.ui.dialogs.movieDetails.MovieDetailsDialog
import com.amadev.rando.util.Util.getProgressDrawable
import com.amadev.rando.util.Util.loadImageWithGlide

class UpcomingMoviesRecyclerViewAdapter(
    val view: View,
    var context: Context,
    var list: ArrayList<MovieDetailsResults>
) : RecyclerView.Adapter<UpcomingMoviesRecyclerViewAdapter.ViewHolder>() {

    val activity = context as FragmentActivity
    private val fragmentManager = activity.supportFragmentManager

    class ViewHolder(val binding: CustomUpcomingMoviesRecyclerviewPatternBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CustomUpcomingMoviesRecyclerviewPatternBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
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

        holder.itemView.setOnClickListener {
            val fragmentDialog = MovieDetailsDialog(list[position])
            fragmentDialog.show(fragmentManager, "fragment_alert")

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}




