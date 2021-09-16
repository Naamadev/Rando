package com.amadev.rando.ui.fragments.mainFragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.amadev.rando.R
import com.amadev.rando.adapter.MoviesRecyclerViewAdapter
import com.amadev.rando.adapter.UpcomingMoviesRecyclerViewAdapter
import com.amadev.rando.databinding.FragmentMainBinding
import com.amadev.rando.model.MovieDetailsResults
import com.amadev.rando.ui.dialogs.logout.LogoutDialog
import com.amadev.rando.util.Util.isNetworkAvailable
import com.amadev.rando.util.Util.showToast
import org.koin.android.viewmodel.ext.android.viewModel

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val mainFragmentViewModel: MainFragmentViewModel by viewModel()
    private val action = R.id.action_mainFragment_to_movieDetailsFragment
    private var isUserLoggedIn: Boolean = false

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var topRatedAdapter: MoviesRecyclerViewAdapter
    private lateinit var popularAdapter: MoviesRecyclerViewAdapter
    private lateinit var nowPlayingAdapter: MoviesRecyclerViewAdapter
    private lateinit var upcomingAdapter: UpcomingMoviesRecyclerViewAdapter

    companion object {
        const val DELAY_TIME = 1500L
        val currentPage = (0..10).random()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isNetworkAvailable(requireContext())) {
            setUpTopRatedRecyclerviewAdapter()
            setUpPopularRecyclerviewAdapter()
            setUpNowPlayingRecyclerviewAdapter()
            setUpUpcomingRecyclerviewAdapter()
            checkIsUserLoggedIn()
            getMovies()
            setUpObservers()
            setUpOnClickListeners()
            setUpSearchMoviesEditText()
            setUpOnBackPressedCallback()
        } else {
            showToast(requireContext(), getString(R.string.noInternetConnection))
        }
    }

    private fun setUpOnBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!isUserLoggedIn) {
                    navigateToSignInOrSignUpFragment()
                } else {
                    activity?.finishAffinity()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun navigateToSignInOrSignUpFragment() {
        findNavController().navigate(R.id.action_mainFragment_to_signinOrSignUpFragment)
    }

    private fun searchMovies(query: String) {
        mainFragmentViewModel.getSearchedMovies(query)
    }

    private fun setUpSearchMoviesEditText() {
        binding.searchMoviesEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    if (s.isEmpty().not()) {
                        Handler(Looper.myLooper()!!).postDelayed({
                            searchMovies(s.toString())
                        }, DELAY_TIME)
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setUpOnClickListeners() {
        binding.apply {
            search.setOnClickListener {
                setUpSearchOnViewsVisibility()
            }
            searchOff.setOnClickListener {
                clearSearchedText()
                setUpSearchOffViewsVisibility()

            }
            topRatedMore.setOnClickListener {
                navigateToTopRatedFragment()
            }
            upcomingMore.setOnClickListener {
                navigateToUpcomingFragment()
            }
            nowPlayingMore.setOnClickListener {
                navigateToNowPlayingFragment()
            }
            popularMore.setOnClickListener {
                navigateToPopularFragment()
            }
            logout.setOnClickListener {
                provideLogoutDialog()
            }
            favoriteMovies.setOnClickListener {
                if (isUserLoggedIn) {
                    navigateToFavoritesFragment()
                } else {
                    showToast(requireContext(), getString(R.string.youMustBeLoggedIn))
                }
            }

        }
    }

    private fun provideLogoutDialog() {
        val dialog = LogoutDialog()
        dialog.show(childFragmentManager, null)
    }

    private fun checkIsUserLoggedIn() {
        mainFragmentViewModel.isUserLoggedIn()
    }

    private fun navigateToUpcomingFragment() {
        findNavController().navigate(R.id.action_mainFragment_to_upcomingFragment)
    }

    private fun navigateToFavoritesFragment() {
        findNavController().navigate(R.id.action_mainFragment_to_favoritesFragment)
    }

    private fun navigateToPopularFragment() {
        findNavController().navigate(R.id.action_mainFragment_to_popularFragment)
    }

    private fun navigateToTopRatedFragment() {
        findNavController().navigate(R.id.action_mainFragment_to_topRatedFragment)
    }

    private fun navigateToNowPlayingFragment() {
        findNavController().navigate(R.id.action_mainFragment_to_nowPlayingFragment)
    }

    private fun clearSearchedText() {
        binding.searchMoviesEditText.text.clear()
    }

    private fun setUpSearchOnViewsVisibility() {
        binding.apply {
            setUpViewVisibilityGone(upcoming)
            setUpViewVisibilityGone(upcomingMore)
            setUpViewVisibilityGone(upcomingRecyclerView)
            setUpViewVisibilityGone(topRated)
            setUpViewVisibilityGone(topRatedMore)
            setUpViewVisibilityGone(topRatedRecyclerView)
            setUpViewVisibilityGone(popular)
            setUpViewVisibilityGone(popularMore)
            setUpViewVisibilityGone(popularRecyclerView)
            setUpViewVisibilityGone(nowPlaying)
            setUpViewVisibilityGone(nowPlayingMore)
            setUpViewVisibilityGone(nowPlayingRecyclerView)
            setUpViewVisibilityGone(search)
            setUpViewVisibilityGone(favoriteMovies)
            setUpViewVisibilityVisible(searchMoviesEditText)
            setUpViewVisibilityVisible(searchedMoviesRecyclerView)
            setUpViewVisibilityVisible(searchedMoviesResults)
            setUpViewVisibilityVisible(searchOff)
        }
    }

    private fun setUpSearchOffViewsVisibility() {
        binding.apply {
            setUpViewVisibilityVisible(upcoming)
            setUpViewVisibilityVisible(upcomingMore)
            setUpViewVisibilityVisible(upcomingRecyclerView)
            setUpViewVisibilityVisible(topRated)
            setUpViewVisibilityVisible(topRatedMore)
            setUpViewVisibilityVisible(topRatedRecyclerView)
            setUpViewVisibilityVisible(popular)
            setUpViewVisibilityVisible(popularMore)
            setUpViewVisibilityVisible(popularRecyclerView)
            setUpViewVisibilityVisible(nowPlaying)
            setUpViewVisibilityVisible(nowPlayingMore)
            setUpViewVisibilityVisible(nowPlayingRecyclerView)
            setUpViewVisibilityVisible(search)
            setUpViewVisibilityVisible(favoriteMovies)
            setUpViewVisibilityGone(searchedMoviesRecyclerView)
            setUpViewVisibilityGone(searchedMoviesResults)
            setUpViewVisibilityGone(searchOff)
            setUpViewVisibilityGone(searchMoviesEditText)
        }
    }

    private fun setUpViewVisibilityGone(view: View) {
        view.visibility = View.GONE
    }

    private fun setUpViewVisibilityVisible(view: View) {
        view.visibility = View.VISIBLE
    }

    private fun setUpObservers() {
        mainFragmentViewModel.apply {
            binding.apply {
                topRatedMoviesResultsLiveData.observe(viewLifecycleOwner) {
                    setUpMoviesHorizontalRecyclerViewAdapter(it, topRatedAdapter)
                }
                popularMoviesResultsLiveData.observe(viewLifecycleOwner) {
                    setUpMoviesHorizontalRecyclerViewAdapter(it, popularAdapter)
                }
                upcomingMoviesResultsLiveData.observe(viewLifecycleOwner) {
                    setUpUpcomingMoviesHorizontalRecyclerViewAdapter(it, upcomingAdapter)
                }
                nowPlayingMoviesLiveData.observe(viewLifecycleOwner) {
                    setUpMoviesHorizontalRecyclerViewAdapter(it, nowPlayingAdapter)
                }
                searchedMoviesLiveData.observe(viewLifecycleOwner) {
                    setUpSearchedMoviesRecyclerViewAdapter(it)
                }
                isUserLoggedIn.observe(viewLifecycleOwner) {
                    if (it) setUpViewVisibilityVisible(binding.logout)
                    this@MainFragment.isUserLoggedIn = it
                }
            }
        }
    }

    private fun setUpSearchedMoviesRecyclerViewAdapter(list: ArrayList<MovieDetailsResults>) {
        val adapter = MoviesRecyclerViewAdapter(requireView(), requireContext(), arrayListOf(), action)
        adapter.list.apply {
            clear()
            addAll(list)
            binding.apply {
                searchedMoviesRecyclerView.layoutManager =
                    GridLayoutManager(requireContext(), 3)
                searchedMoviesRecyclerView.adapter = adapter
            }
        }
    }


    private fun getMovies() {
        mainFragmentViewModel.apply {
            getTopRatedMovies(currentPage)
            getPopularMovies(currentPage)
            getUpcomingMovies(currentPage)
            getNowPlayingMovies(currentPage)
        }
    }


    private fun setUpTopRatedRecyclerviewAdapter() {
        linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        topRatedAdapter =
            MoviesRecyclerViewAdapter(requireView(), requireContext(), arrayListOf(), action)
        binding.apply {
            topRatedRecyclerView.apply {
                layoutManager = linearLayoutManager
                adapter = topRatedAdapter
            }
        }
    }

    private fun setUpPopularRecyclerviewAdapter() {
        linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        popularAdapter =
            MoviesRecyclerViewAdapter(requireView(), requireContext(), arrayListOf(), action)
        binding.apply {
            popularRecyclerView.apply {
                layoutManager = linearLayoutManager
                adapter = popularAdapter
            }
        }
    }

    private fun setUpNowPlayingRecyclerviewAdapter() {
        linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        nowPlayingAdapter =
            MoviesRecyclerViewAdapter(requireView(), requireContext(), arrayListOf(), action)
        binding.apply {
            nowPlayingRecyclerView.apply {
                layoutManager = linearLayoutManager
                adapter = nowPlayingAdapter
            }
        }
    }

    private fun setUpMoviesHorizontalRecyclerViewAdapter(
        list: ArrayList<MovieDetailsResults>,
        adapter: MoviesRecyclerViewAdapter
    ) {
        adapter.notifyDataSetChanged()
        adapter.list.apply {
            clear()
            addAll(list)
        }
    }

    private fun setUpUpcomingRecyclerviewAdapter() {
        upcomingAdapter =
            UpcomingMoviesRecyclerViewAdapter(requireView(), requireContext(), arrayListOf())
        binding.apply {
            upcomingRecyclerView.apply {
                layoutManager = upcomingRecyclerView.getCarouselLayoutManager()
                adapter = upcomingAdapter
                setInfinite(true)
                setAlpha(true)
            }
        }
    }

    private fun setUpUpcomingMoviesHorizontalRecyclerViewAdapter(
        list: ArrayList<MovieDetailsResults>,
        adapter: UpcomingMoviesRecyclerViewAdapter
    ) {
//        val adapter =
//            UpcomingMoviesRecyclerViewAdapter(requireView(), requireContext(), arrayListOf())
        adapter.notifyDataSetChanged()
        adapter.list.apply {
            clear()
            addAll(list)
//            binding.apply {
//                upcomingRecyclerView.layoutManager = upcomingRecyclerView.getCarouselLayoutManager()
//                upcomingRecyclerView.adapter = adapter
//                upcomingRecyclerView.setInfinite(true)
//                upcomingRecyclerView.setAlpha(true)
//            }
        }
    }
}
