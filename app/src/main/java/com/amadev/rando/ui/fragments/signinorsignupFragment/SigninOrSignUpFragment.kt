package com.amadev.rando.ui.fragments.signinorsignupFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.amadev.rando.R
import com.amadev.rando.databinding.FragmentSigninOrSignUpBinding
import com.amadev.rando.util.Util
import com.amadev.rando.util.Util.isNetworkAvailable
import com.amadev.rando.util.Util.showToast
import com.google.android.material.snackbar.Snackbar
import org.koin.android.viewmodel.ext.android.viewModel

class SigninOrSignUpFragment : Fragment() {
    private var _binding: FragmentSigninOrSignUpBinding? = null
    private val binding get() = _binding!!

    private val signInOrSignUpViewModel: SignInOrSignUpViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSigninOrSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isNetworkAvailable(requireContext())) {
            setUpObservers()
            setUpViewModel()
            setUpOnBackPressedCallback()
            setUpOnClickListeners()
        } else {
            showToast(requireContext(), getString(R.string.noInternetConnection))
        }
    }

    private fun setUpObservers() {
        signInOrSignUpViewModel.apply {
            loginAutomaticallyIfPossibleLiveData.observe(viewLifecycleOwner) {
                if (it == true) {
                    findNavController().navigate(R.id.action_signinOrSignUpFragment_to_mainFragment)
                }
            }
            messageLiveData.observe(viewLifecycleOwner) {
                val snack = Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG)
                snack.show()
            }
        }
    }

    private fun setUpViewModel() {
        signInOrSignUpViewModel.apply {
            loginAutomaticallyIfPossible()
        }
    }

    private fun setUpOnClickListeners() {
        binding.skipBtn.setOnClickListener {
            findNavController().navigate(R.id.action_signinOrSignUpFragment_to_mainFragment)
        }

        binding.signinBtn.setOnClickListener {
            findNavController().navigate(R.id.action_signinOrSignUpFragment_to_signInFragment)
        }

        binding.signupBtn.setOnClickListener {
            findNavController().navigate(R.id.action_signinOrSignUpFragment_to_signUpFragment)
        }
    }

    private fun setUpOnBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

}