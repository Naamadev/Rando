package com.amadev.rando.di

import com.amadev.rando.ui.dialogs.castDetails.CastDetailsViewModel
import com.amadev.rando.ui.dialogs.forgotPassword.ForgotPasswordDialogViewModel
import com.amadev.rando.ui.dialogs.logout.LogoutDialogViewModel
import com.amadev.rando.ui.fragments.favoritesFragment.FavoritesFragmentViewModel
import com.amadev.rando.ui.fragments.mainFragment.MainFragmentViewModel
import com.amadev.rando.ui.fragments.movieDetailsFragment.MovieDetailsViewModel
import com.amadev.rando.ui.fragments.nowPlayingFragment.NowPlayingViewModel
import com.amadev.rando.ui.fragments.popularFragment.PopularFragmentViewModel
import com.amadev.rando.ui.fragments.signinFragment.SignInViewModel
import com.amadev.rando.ui.fragments.signinorsignupFragment.SignInOrSignUpViewModel
import com.amadev.rando.ui.fragments.signupFragment.SignUpViewModel
import com.amadev.rando.ui.fragments.topRatedFragment.TopRatedFragmentViewModel
import com.amadev.rando.ui.fragments.upcomingFragment.UpcomingFragmentViewModel
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
        FavoritesFragmentViewModel(
            get(),
            get(),
            get()
        )
    }

    viewModel { MovieDetailsViewModel(get(), get(), get(), get()) }
    viewModel { UpcomingFragmentViewModel(get()) }
    viewModel { TopRatedFragmentViewModel(get()) }
    viewModel { PopularFragmentViewModel(get()) }
    viewModel { NowPlayingViewModel(get()) }
    viewModel { SignUpViewModel(get(), get()) }
    viewModel { SignInViewModel(get(), get(), get()) }
    viewModel { SignInOrSignUpViewModel() }
    viewModel { ForgotPasswordDialogViewModel(get(), get()) }
    viewModel { CastDetailsViewModel() }
    viewModel { LogoutDialogViewModel(get(), get()) }
}





