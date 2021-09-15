package com.amadev.rando.ui.fragments.upcomingFragment

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
import com.amadev.rando.databinding.FragmentUpcomingBinding
import com.amadev.rando.model.MovieDetailsResults
import com.amadev.rando.util.Util.isNetworkAvailable
import com.amadev.rando.util.Util.showToast
import org.koin.android.viewmodel.ext.android.viewModel


class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!
    private val upcomingFragmentViewModel: UpcomingFragmentViewModel by viewModel()
    private val action = R.id.action_upcomingFragment_to_movieDetailsFragment
    lateinit var gridLayoutManager: GridLayoutManager
    lateinit var adapter: MoviesGridRecyclerViewAdapter

    private var currentPage = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isNetworkAvailable(requireContext())) {
            getUpcomingMovies()
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
            upcomingRecyclerView.layoutManager = gridLayoutManager
            upcomingRecyclerView.adapter = adapter

        }
    }

    private fun setUpObservers() {
        upcomingFragmentViewModel.upcomingMoviesResultsLiveData.observe(viewLifecycleOwner) {
            setUpRecyclerView(it, binding.upcomingRecyclerView)
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
                getUpcomingMovies()
            }
        })
    }

    private fun getUpcomingMovies() {
        currentPage++
        upcomingFragmentViewModel.getUpcomingMovies(currentPage)
    }
}