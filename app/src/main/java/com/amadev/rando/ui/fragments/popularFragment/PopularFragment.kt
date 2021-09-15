package com.amadev.rando.ui.fragments.popularFragment

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
import com.amadev.rando.databinding.FragmentPopularBinding
import com.amadev.rando.model.MovieDetailsResults
import com.amadev.rando.util.Util.isNetworkAvailable
import com.amadev.rando.util.Util.showToast
import org.koin.android.viewmodel.ext.android.viewModel

class PopularFragment : Fragment() {

    private var _binding: FragmentPopularBinding? = null
    private val binding get() = _binding!!
    private val popularFragmentViewModel: PopularFragmentViewModel by viewModel()

    private val action = R.id.action_popularFragment_to_movieDetailsFragment
    lateinit var gridLayoutManager: GridLayoutManager
    lateinit var adapter: MoviesRecyclerViewAdapter

    private var currentPage = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPopularBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isNetworkAvailable(requireContext())) {
            getPopularMovies()
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
            popularRecyclerView.layoutManager = gridLayoutManager
            popularRecyclerView.adapter = adapter

        }
    }

    private fun setUpObservers() {
        popularFragmentViewModel.popularMoviesResultsLiveData.observe(viewLifecycleOwner) {
            setUpRecyclerView(it, binding.popularRecyclerView)
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
                getPopularMovies()
            }
        })
    }

    private fun getPopularMovies() {
        currentPage++
        popularFragmentViewModel.getPopularMovies(currentPage)
    }
}