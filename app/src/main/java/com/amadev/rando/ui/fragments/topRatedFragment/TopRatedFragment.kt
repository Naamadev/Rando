package com.amadev.rando.ui.fragments.topRatedFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amadev.rando.R
import com.amadev.rando.adapter.EndlessRecyclerOnScrollListener
import com.amadev.rando.adapter.MoviesRecyclerViewAdapter
import com.amadev.rando.databinding.FragmentTopRatedBinding
import com.amadev.rando.model.MovieDetailsResults
import com.amadev.rando.util.Util
import com.amadev.rando.util.Util.isNetworkAvailable
import com.amadev.rando.util.Util.showToast
import org.koin.android.viewmodel.ext.android.viewModel


class TopRatedFragment : Fragment() {
    private var _binding: FragmentTopRatedBinding? = null
    private val binding get() = _binding!!
    private val topRatedFragmentViewModel: TopRatedFragmentViewModel by viewModel()
    private val action = R.id.action_topRatedFragment_to_movieDetailsFragment
    lateinit var gridLayoutManager: GridLayoutManager
    lateinit var adapter: MoviesRecyclerViewAdapter

    private var currentPage = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopRatedBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isNetworkAvailable(requireContext())) {
            getTopRatedMovies()
            setUpRecyclerviewAdapter()
            setUpObservers()
        } else {
            showToast(requireContext(), getString(R.string.noInternetConnection))
        }
    }

    private fun setUpRecyclerviewAdapter() {
        gridLayoutManager = GridLayoutManager(requireContext(), 3)
        adapter =
            MoviesRecyclerViewAdapter(requireView(), requireContext(), arrayListOf(), action)
        binding.apply {
            topRatedRecyclerView.layoutManager = gridLayoutManager
            topRatedRecyclerView.adapter = adapter

        }
    }

    private fun setUpObservers() {
        topRatedFragmentViewModel.topRatedMoviesResultsLiveData.observe(viewLifecycleOwner) {
            setUpRecyclerView(it, binding.topRatedRecyclerView)
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
                getTopRatedMovies()
            }
        })
    }

    private fun getTopRatedMovies() {
        currentPage++
        topRatedFragmentViewModel.getTopRatedMovies(currentPage)
    }
}