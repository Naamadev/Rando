package com.amadev.rando.ui.fragments.movieDetailsFragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.amadev.rando.R
import com.amadev.rando.YoutubeActivity
import com.amadev.rando.adapter.CastRecyclerViewAdapter
import com.amadev.rando.adapter.GenresRecyclerViewAdapter
import com.amadev.rando.databinding.MovieDetailsFragmentBinding
import com.amadev.rando.model.CastModelResults
import com.amadev.rando.model.MovieDetailsResults
import com.amadev.rando.util.Genres
import com.amadev.rando.util.Util
import com.amadev.rando.util.Util.getProgressDrawable
import com.amadev.rando.util.Util.loadImageWithGlide
import com.amadev.rando.util.Util.showSnackBar
import com.amadev.rando.util.Util.showToast
import org.koin.android.viewmodel.ext.android.viewModel

class MovieDetailsFragment : Fragment() {

    private var _binding: MovieDetailsFragmentBinding? = null
    private val binding get() = _binding!!
    private val movieDetailsViewModel: MovieDetailsViewModel by viewModel()
    lateinit var intent: Intent
    var movieId: Int? = null
    private var userLogged: Boolean? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MovieDetailsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Util.isNetworkAvailable(requireContext())) {
            checkIsUserLoggedIn()
            setUpObservers()
            setUpViewModel()
            setUpOnClickListeners()
        } else {
            showToast(requireContext(), getString(R.string.noInternetConnection))
        }

    }

    private fun checkIsUserLoggedIn() {
        movieDetailsViewModel.isUserLoggedIn()
    }

    private fun setUpOnClickListeners() {
        binding.apply {
            watchTrailer.setOnClickListener {
                startYoutubeActivity()
            }
            addToFavorites.setOnClickListener {
                if (userLogged == true) {
                    addCurrentMovieToFavoriteMovies()
                } else {
                    showToast(requireContext(), getString(R.string.youMustBeLoggedIn))
                }
            }
            removeFromFavorites.setOnClickListener {
                removeCurrentMovieFromFavorites(movieId)
            }
        }
    }

    private fun removeCurrentMovieFromFavorites(movieId: Int?) {
        movieDetailsViewModel.removeCurrentMovieFromFavoriteMovies(movieId)
    }

    private fun startYoutubeActivity() {
        startActivity(intent)
    }

    private fun setUpObservers() {
        binding.apply {
            movieDetailsViewModel.apply {
                isUserLoggedIn.observe(viewLifecycleOwner) {
                    userLogged = it
                    if(it == true) {
                        getFavoriteMovies()
                    }
                }
                castList.observe(viewLifecycleOwner) {
                    setUpCastRecyclerView(it as ArrayList<CastModelResults>)
                }
                videoEndPoint.observe(viewLifecycleOwner) {
                    setUpYoutubeIntent(it)
                }
                popUpMessageMutableLiveData.observe(viewLifecycleOwner) {
                    showSnackBar(requireView(), it)
                }
                movieDetailsMutableLiveData.observe(viewLifecycleOwner) {
                    it?.id?.let { movieIdNo -> movieId = movieIdNo }
                    it?.genre_ids?.let { genresIntList ->
                        setUpMovieGenresRecyclerView(setUpGenreList(genresIntList))
                    }
                    it?.title?.let { title ->
                        movieName.text = title.trim()
                    }
                    it?.release_date?.let { year ->
                        releaseDate.text = year.take(4)
                    }
                    it?.vote_average?.let { votes ->
                        rating.text = votes.toString()
                        ratingBar.rating = (votes / 2).toFloat()
                    }
                    it?.overview?.let { overviewText ->
                        overviewTv.text = overviewText.trim()
                    }
                    it?.poster_path?.let { uri ->
                        movieImage.loadImageWithGlide(uri, getProgressDrawable(requireContext()))
                    }
                }
                favoriteMoviesMutableLiveData.observe(viewLifecycleOwner) {
                    it?.let {
                        movieId?.let { movieId ->
                            checkIfFirebaseContainsCurrentMovie(
                                it,
                                movieId
                            )
                        }
                    }
                }
            }
        }
    }

    private fun checkIfFirebaseContainsCurrentMovie(
        favoriteMoviesList: ArrayList<MovieDetailsResults?>,
        movieId: Int
    ) {
        if (favoriteMoviesList.toString().contains(movieId.toString())) {
            binding.apply {
                removeFromFavorites.visibility = View.VISIBLE
                addToFavorites.visibility = View.GONE
            }
        } else {
            binding.apply {
                removeFromFavorites.visibility = View.GONE
                addToFavorites.visibility = View.VISIBLE
            }
        }
    }

    private fun setUpYoutubeIntent(videoEndPoint: String): Intent {
        if (videoEndPoint.isEmpty().not()) {
            intent = Intent(requireContext(), YoutubeActivity::class.java)
            intent.putExtra("videoId", videoEndPoint)
        }
        return intent
    }

    private fun setUpCastRecyclerView(list: ArrayList<CastModelResults>) {
        val adapter = CastRecyclerViewAdapter(requireView(), requireContext(), arrayListOf())
        adapter.list.apply {
            clear()
            addAll(list)
            binding.apply {
                castRecyclerview.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                castRecyclerview.adapter = adapter
            }
        }
    }

    private fun setUpGenreList(genresIdList: List<Int>): ArrayList<String> {
        val genreIdArrayList = ArrayList<String>()
        genresIdList.forEach { i ->
            val genreName = Genres.findGenreNameById(i)
            genreIdArrayList.add(genreName)
        }
        return genreIdArrayList
    }

    private fun setUpMovieGenresRecyclerView(list: ArrayList<String>) {
        val recyclerVAdapter =
            GenresRecyclerViewAdapter(requireView(), requireContext(), arrayListOf())
        recyclerVAdapter.list.apply {
            clear()
            addAll(list)
        }
        binding.apply {
            genresRecyclerView.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = recyclerVAdapter
            }
        }
    }

    private fun addCurrentMovieToFavoriteMovies() {
        movieDetailsViewModel.addCurrentMovieToFavoriteMovies()
    }

    private fun getMovieDetailsArgs(): MovieDetailsResults? {
        return arguments?.getParcelable<MovieDetailsResults>("movieDetails")
    }

    private fun setUpViewModel() {
        movieDetailsViewModel.apply {
            setUpMovieDetails(getMovieDetailsArgs())
        }
    }

}