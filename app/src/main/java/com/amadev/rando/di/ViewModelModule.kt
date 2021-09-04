package com.amadev.rando.di

import com.amadev.rando.ui.dialogs.castDetails.CastDetailsViewModel
import com.amadev.rando.ui.dialogs.forgotPassword.ForgotPasswordDialogViewModel
import com.amadev.rando.ui.dialogs.logout.LogoutDialogViewModel
import com.amadev.rando.ui.fragments.categoryViewPager.nowPlaying.NowPlayingViewModel
import com.amadev.rando.ui.fragments.categoryViewPager.popularFragment.PopularFragmentViewModel
import com.amadev.rando.ui.fragments.categoryViewPager.topRated.TopRatedFragmentViewModel
import com.amadev.rando.ui.fragments.choice.ChoiceFragmentViewModel
import com.amadev.rando.ui.fragments.favorites.FavoritesFragmentViewModel
import com.amadev.rando.ui.fragments.mainFragment.MainFragmentViewModel
import com.amadev.rando.ui.fragments.movieDetails.MovieDetailsViewModel
import com.amadev.rando.ui.fragments.signin.SignInViewModel
import com.amadev.rando.ui.fragments.signinorsignup.SignInOrSignUpViewModel
import com.amadev.rando.ui.fragments.signup.SignUpViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        MainFragmentViewModel(
            get(),
            get(),
            get()
        )
    }

    viewModel {
        ChoiceFragmentViewModel(
            get(),
            get(),
            get(),
            get()
        )
    }

    viewModel {
        FavoritesFragmentViewModel(
            get(),
            get()
        )
    }

    viewModel { MovieDetailsViewModel(get(), get(), get()) }
    viewModel { TopRatedFragmentViewModel(get()) }
    viewModel { PopularFragmentViewModel(get()) }
    viewModel { NowPlayingViewModel(get()) }
    viewModel { SignUpViewModel(get(), get()) }
    viewModel { SignInViewModel(get(), get()) }
    viewModel { SignInOrSignUpViewModel() }
    viewModel { ForgotPasswordDialogViewModel(get(), get()) }
    viewModel { CastDetailsViewModel() }
    viewModel { LogoutDialogViewModel(get(), get()) }
}





