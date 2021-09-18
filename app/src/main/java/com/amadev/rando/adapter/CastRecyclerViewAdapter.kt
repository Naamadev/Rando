package com.amadev.rando.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.amadev.rando.databinding.CustomCastRecyclerviewPatternBinding
import com.amadev.rando.model.CastModelResults
import com.amadev.rando.ui.dialogs.castDetails.CastDetailsDialog
import com.amadev.rando.util.Util.getProgressDrawable
import com.amadev.rando.util.Util.loadImageWithGlide

class CastRecyclerViewAdapter(
    val view: View,
    var context: Context,
    var list: ArrayList<CastModelResults>
) :
    RecyclerView.Adapter<CastRecyclerViewAdapter.ViewHolder>() {

    val activity = context as FragmentActivity
    private val fragmentManager = activity.supportFragmentManager

    class ViewHolder(val binding: CustomCastRecyclerviewPatternBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CustomCastRecyclerviewPatternBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            setUpViews(holder, position)

            itemView.setOnClickListener {
                showCastDialog(list[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun setUpViews(holder: ViewHolder, position: Int) {
        with(holder) {
            binding.apply {
                actorName.text = list[position].name
                actorImageRecyclerview.loadImageWithGlide(
                    list[position].profile_path,
                    getProgressDrawable(context)
                )
            }
        }
    }

    private fun showCastDialog(cast: CastModelResults) {
        val fragmentDialog = CastDetailsDialog(cast)
        fragmentDialog.show(fragmentManager, "")
    }

}