package com.amadev.rando.ui.fragments.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.amadev.rando.R
import com.amadev.rando.databinding.FragmentSignInBinding
import com.google.android.material.snackbar.Snackbar

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private lateinit var signInViewModel: SignInViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
//        return inflater.inflate(R.layout.fragment_sign_in, container, false)
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.let {
            signInViewModel = ViewModelProviders.of(this).get(SignInViewModel::class.java)
        }

        setUpViewModel()
        setUpObservers()
        setUpOnBackPressedCallback()
        setUpOnClickListeners()

    }

    private fun setUpViewModel() {
        signInViewModel.apply {
            loginAutomaticallyIfPossible()
        }
    }

    private fun setUpOnClickListeners() {
        binding.signinBtn.setOnClickListener {
            signInViewModel.validateInput(
                binding.usernameInput.text.toString(),
                binding.passwordInput.text.toString())
        }
    }


    private fun setUpObservers() {
        signInViewModel.apply {
            errorMessageLiveData.observe(viewLifecycleOwner) { binding.usernameInput.error = it }

            messageLiveData.observe(viewLifecycleOwner) {
                val snack = Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG)
                snack.show()
                binding.usernameInput.text.clear()
                binding.passwordInput.text.clear()
            }
            setUpProgressbarVisibility.observe(viewLifecycleOwner) {
                if (it == true) binding.progressbar.visibility = View.VISIBLE
                else binding.progressbar.visibility = View.GONE
            }
            loginSuccessfulLiveData.observe(viewLifecycleOwner) {
                if (it == true) findNavController().navigate(R.id.action_signInFragment_to_choiceFragment)
            }
            loginAutomaticallyIfPossibleLiveData.observe(viewLifecycleOwner) {
                if (it == true) findNavController().navigate(R.id.action_signInFragment_to_choiceFragment)
            }
        }
    }

    private fun setUpOnBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_signInFragment_to_signinOrSignUpFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }


}