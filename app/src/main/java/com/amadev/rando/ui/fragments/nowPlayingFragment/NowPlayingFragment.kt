package com.amadev.rando.ui.fragments.nowPlayingFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amadev.rando.R
import com.amadev.rando.adapter.EndlessRecyclerOnScrollListener
import com.amadev.rando.adapter.MoviesGridRecyclerViewAdapter
import com.amadev.rando.databinding.FragmentNowPlayingBinding
import com.amadev.rando.model.MovieDetailsResults
import com.amadev.rando.util.Util.isNetworkAvailable
import com.amadev.rando.util.Util.showToast
import org.koin.android.viewmodel.ext.android.viewModel

class NowPlayingFragment : Fragment() {

    private var _binding: FragmentNowPlayingBinding? = null
    private val binding get() = _binding!!
    private val nowPlayingViewModel: NowPlayingViewModel by viewModel()
    private val action = R.id.action_nowPlayingFragment_to_movieDetailsFragment
    lateinit var gridLayoutManager: GridLayoutManager
    lateinit var adapter: MoviesGridRecyclerViewAdapter

    private var currentPage = 0


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

        if (isNetworkAvailable(requireContext())) {
            getNowPlayingMovies()
            setUpRecyclerviewAdapter()
            setUpObservers()
        } else {
            showToast(requireContext(), getString(R.string.noInternetConnection))
        }
    }

    private fun setUpRecyclerviewAdapter() {
        gridLayoutManager = GridLayoutManager(requireContext(), 3)
        adapter =
            MoviesGridRecyclerViewAdapter(requireView(), requireContext(), arrayListOf(), action)
        binding.apply {
            nowPlayingRecyclerView.layoutManager = gridLayoutManager
            nowPlayingRecyclerView.adapter = adapter

        }
    }

    private fun setUpObservers() {
        nowPlayingViewModel.nowPlayingMoviesResultsLiveData.observe(viewLifecycleOwner) {
            setUpRecyclerView(it, binding.nowPlayingRecyclerView)
        }
    }

    private fun setUpRecyclerView(
        list: ArrayList<MovieDetailsResults>,
        recyclerView: RecyclerView
    ) {
        adapter.notifyDataSetChanged()
        adapter.list.apply {
            clear()
            addAll(list)
        }
        recyclerView.addOnScrollListener(object :
            EndlessRecyclerOnScrollListener(gridLayoutManager) {
            override fun onLoadMore(current_page: Int) {
                getNowPlayingMovies()
            }
        })
    }

    private fun getNowPlayingMovies() {
        currentPage++
        nowPlayingViewModel.getPopularMovies(currentPage)
    }
}