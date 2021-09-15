package com.amadev.rando.ui.fragments.favoritesFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.amadev.rando.R
import com.amadev.rando.adapter.MoviesGridRecyclerViewAdapter
import com.amadev.rando.databinding.FragmentFavoritesBinding
import com.amadev.rando.model.MovieDetailsResults
import com.amadev.rando.util.Util.isNetworkAvailable
import com.amadev.rando.util.Util.showSnackBar
import com.amadev.rando.util.Util.showToast
import org.koin.android.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val favoritesFragmentViewModel: FavoritesFragmentViewModel by viewModel()
    private val action = R.id.action_favoritesFragment_to_movieDetailsFragment
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var adapter: MoviesGridRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isNetworkAvailable(requireContext())) {
            getFavoriteMovies()
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
            favoriteMoviesRecyclerView.layoutManager = gridLayoutManager
            favoriteMoviesRecyclerView.adapter = adapter
        }
    }

    private fun getFavoriteMovies() {
        favoritesFragmentViewModel.getFavoriteMovies()
    }

    private fun setUpObservers() {
        favoritesFragmentViewModel.apply {
            favoritesMoviesLiveData.observe(viewLifecycleOwner) { favoriteMoviesList ->
                if (favoriteMoviesList != null) {
                    setUpFavoriteMoviesRecyclerview(favoriteMoviesList)
                }
            }
            popUpMessageLiveData.observe(viewLifecycleOwner) {
                showSnackBar(requireView(), it)
            }
            progressBarLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    false -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setUpFavoriteMoviesRecyclerview(favoriteMoviesList: ArrayList<MovieDetailsResults>) {
        adapter.notifyDataSetChanged()
        adapter.list.apply {
            clear()
            addAll(favoriteMoviesList)
        }
    }
}