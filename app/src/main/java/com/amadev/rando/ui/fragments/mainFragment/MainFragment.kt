package com.amadev.rando.ui.fragments.mainFragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amadev.rando.adapter.MoviesRecyclerViewAdapter
import com.amadev.rando.adapter.UpcomingMoviesRecyclerViewAdapter
import com.amadev.rando.databinding.FragmentMainBinding
import com.amadev.rando.model.MovieDetailsResults
import com.amadev.rando.ui.fragments.categoryViewPager.nowPlaying.NowPlayingFragment
import com.amadev.rando.ui.fragments.categoryViewPager.popularFragment.PopularFragment
import com.amadev.rando.ui.fragments.categoryViewPager.topRated.TopRatedFragment
import com.amadev.rando.ui.onboarding.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.android.viewmodel.ext.android.viewModel

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val mainFragmentViewModel: MainFragmentViewModel by viewModel()
    private val categoryFragmentList = arrayListOf(
        TopRatedFragment(),
        PopularFragment(),
        NowPlayingFragment()
    )

    private val tabText = arrayOf("Top Rated", "Popular", "Now playing")


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

        getMovies()
        setUpObservers()
        setUpOnClickListeners()
        setUpSearchMoviesEditText()
//        setUpCategoryViewPager()
        setUpTabLayoutMediator()
    }

    private fun setUpTabLayoutMediator() {
        val vp = binding.categoryViewPager
        vp.adapter = ViewPagerAdapter(categoryFragmentList, childFragmentManager, lifecycle)
        TabLayoutMediator(binding.categoryTabLayout, vp) { tab, position ->
            tab.text = tabText[position]
        }.attach()
    }

    private fun setUpCategoryViewPager() {
        binding.categoryViewPager.adapter =
            ViewPagerAdapter(categoryFragmentList, childFragmentManager, lifecycle)
    }

    private fun searchMovies(query: String) {
        mainFragmentViewModel.getSearchedMovies(query)
    }

    private fun setUpSearchMoviesEditText() {
        binding.searchMoviesEditText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    if (s.isEmpty().not()) {
                        setUpSearchOnViewsVisibility()
                        Handler(Looper.myLooper()!!).postDelayed({
                            searchMovies(s.toString())
                        }, 1500)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setUpOnClickListeners() {
        binding.apply {
//            topRated.setOnClickListener {
//                getMovies()
//            }
            searchMoviesEditText.setOnClickListener {
                setUpSearchOnViewsVisibility()
            }
            searchOff.setOnClickListener {
                clearSearchedText()
                setUpSearchOffViewsVisibility()
            }

        }
    }

    private fun clearSearchedText() {
        binding.searchMoviesEditText.text.clear()
    }

    private fun setUpSearchOnViewsVisibility() {
        binding.apply {
            setUpViewVisibilityGone(upcoming)
            setUpViewVisibilityGone(upcomingRecyclerView)
//            setUpViewVisibilityGone(topRated)
//            setUpViewVisibilityGone(topRatedRecyclerView)
//            setUpViewVisibilityGone(mostPopularRecyclerView)
//            setUpViewVisibilityGone(popular)
//            setUpViewVisibilityGone(nowPlaying)
//            setUpViewVisibilityGone(nowPlayingRecyclerView)
            setUpViewVisibilityVisible(searchMoviesEditText)
            setUpViewVisibilityVisible(searchedMoviesRecyclerView)
            setUpViewVisibilityVisible(searchedMoviesResults)
            setUpViewVisibilityVisible(searchOff)
        }
    }

    private fun setUpSearchOffViewsVisibility() {
        binding.apply {
            setUpViewVisibilityVisible(upcoming)
            setUpViewVisibilityVisible(upcomingRecyclerView)
//            setUpViewVisibilityVisible(topRated)
//            setUpViewVisibilityVisible(topRatedRecyclerView)
//            setUpViewVisibilityVisible(mostPopularRecyclerView)
//            setUpViewVisibilityVisible(popular)
//            setUpViewVisibilityVisible(nowPlaying)
//            setUpViewVisibilityVisible(nowPlayingRecyclerView)
            setUpViewVisibilityGone(searchedMoviesRecyclerView)
            setUpViewVisibilityGone(searchedMoviesResults)
            setUpViewVisibilityGone(searchOff)
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
//                topRatedMoviesResultsLiveData.observe(viewLifecycleOwner) {
//                    setUpMoviesHorizontalRecyclerViewAdapter(it, topRatedRecyclerView)
//                }
//                popularMoviesResultsLiveData.observe(viewLifecycleOwner) {
//                    setUpMoviesHorizontalRecyclerViewAdapter(it, mostPopularRecyclerView)
//                }
                upcomingMoviesResultsLiveData.observe(viewLifecycleOwner) {
                    setUpUpcomingMoviesHorizontalRecyclerViewAdapter(it)
                }
//                nowPlayingMoviesLiveData.observe(viewLifecycleOwner) {
//                    setUpMoviesHorizontalRecyclerViewAdapter(it, nowPlayingRecyclerView)
//                }
                searchedMoviesLiveData.observe(viewLifecycleOwner) {
                    setUpSearchedMoviesRecyclerViewAdapter(it)
                }
            }
        }
    }

    private fun setUpSearchedMoviesRecyclerViewAdapter(list: ArrayList<MovieDetailsResults>) {
        val adapter = MoviesRecyclerViewAdapter(requireView(), requireContext(), arrayListOf())
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
            getTopRatedMovies()
            getPopularMovies()
            getUpcomingMovies()
            getNowPlayingMovies()
        }
    }

    private fun setUpMoviesHorizontalRecyclerViewAdapter(
        list: ArrayList<MovieDetailsResults>,
        recyclerView: RecyclerView
    ) {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val adapter = MoviesRecyclerViewAdapter(requireView(), requireContext(), arrayListOf())
        adapter.list.apply {
            clear()
            addAll(list)
            binding.apply {
                recyclerView.layoutManager = layoutManager
                recyclerView.adapter = adapter
            }
        }
    }

    private fun setUpUpcomingMoviesHorizontalRecyclerViewAdapter(
        list: ArrayList<MovieDetailsResults>
    ) {
        val adapter =
            UpcomingMoviesRecyclerViewAdapter(requireView(), requireContext(), arrayListOf())

        adapter.list.apply {
            clear()
            addAll(list)
            binding.apply {
                upcomingRecyclerView.layoutManager = upcomingRecyclerView.getCarouselLayoutManager()
                upcomingRecyclerView.adapter = adapter
                upcomingRecyclerView.setInfinite(true)
                upcomingRecyclerView.setAlpha(true)
            }
        }
    }
}
