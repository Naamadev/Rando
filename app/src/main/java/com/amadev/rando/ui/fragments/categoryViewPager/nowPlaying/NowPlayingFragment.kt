package com.amadev.rando.ui.fragments.categoryViewPager.nowPlaying

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amadev.rando.adapter.MoviesRecyclerViewAdapter
import com.amadev.rando.databinding.FragmentNowPlayingBinding
import com.amadev.rando.model.MovieDetailsResults
import org.koin.android.viewmodel.ext.android.viewModel

class NowPlayingFragment : Fragment() {

    private var _binding: FragmentNowPlayingBinding? = null
    private val binding get() = _binding!!
    private val nowPlayingViewModel: NowPlayingViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNowPlayingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getTopRatedMovies()
        setUpObservers()
    }

    private fun setUpObservers() {
        nowPlayingViewModel.nowPlayingMoviesResultsLiveData.observe(viewLifecycleOwner) {
            setUpRecyclerViewAdapter(it, binding.nowPlayingRecyclerView)
        }
    }

    private fun setUpRecyclerViewAdapter(
        list: ArrayList<MovieDetailsResults>,
        recyclerView: RecyclerView
    ) {
        val adapter = MoviesRecyclerViewAdapter(requireView(), requireContext(), arrayListOf())
        adapter.list.apply {
            clear()
            addAll(list)
            binding.apply {
                recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
                recyclerView.adapter = adapter
            }
        }
    }

    private fun getTopRatedMovies() {
        nowPlayingViewModel.getPopularMovies()
    }
}