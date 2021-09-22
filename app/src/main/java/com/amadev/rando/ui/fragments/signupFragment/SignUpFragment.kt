package com.amadev.rando.ui.fragments.signupFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.amadev.rando.R
import com.amadev.rando.databinding.FragmentSignUpBinding
import com.amadev.rando.ui.dialogs.informationDialog.InformationDialog
import com.amadev.rando.util.Util.isNetworkAvailable
import com.amadev.rando.util.Util.showToast
import org.koin.android.viewmodel.ext.android.viewModel

class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val signUpViewModel: SignUpViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isNetworkAvailable(requireContext())) {
            setUpProgressBarVisibility()
            setUpObservers()
            setUpOnClickListeners()
            setUpOnBackPressedCallback()
        } else {
            showToast(requireContext(), getString(R.string.noInternetConnection))
        }
    }

    private fun setUpOnClickListeners() {
        binding.apply {
            signupBtn.setOnClickListener {

                if (bothBoxesChecked()) {
                    sendInputsToViewModel()
                } else {
                    showToast(requireContext(), getString(R.string.youMustCheckBothBoxes))
                }
            }
            privacyPolicyTextView.setOnClickListener {
                providePrivacyPolicyDialog()
            }
            termsOfUseTextView.setOnClickListener {
                provideTermsAncConditionsDialog()
            }
        }

    }

    private fun bothBoxesChecked(): Boolean {
        var boxesChecked = false
        binding.apply {
            if (privacyPolicyCheckBox.isChecked && termsNConditionsCheckBox.isChecked) {
                boxesChecked = true
            }
        }
        return boxesChecked
    }

    private fun provideTermsAncConditionsDialog() {
        val dialog = InformationDialog(
            getString(R.string.terms_n_conditions_title),
            getString(R.string.terms_n_conditionsText)
        )
        dialog.show(childFragmentManager, null)
    }

    private fun providePrivacyPolicyDialog() {
        val dialog = InformationDialog(
            getString(R.string.privacyPolicyTitle),
            getString(R.string.privacyPolicyText)
        )
        dialog.show(childFragmentManager, null)
    }

    private fun setUpProgressBarVisibility() {
        signUpViewModel.progressBarVisibleLiveData.observe(viewLifecycleOwner) {
            if (it == true) binding.progressBar.visibility = View.VISIBLE else {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun sendInputsToViewModel() {
        signUpViewModel.apply {
            validateInput(
                binding.usernameInput.text.toString(),
                binding.passwordInput.text.toString(),
                binding.passwordRepeatInput.text.toString()
            )
        }
    }

    private fun setUpObservers() {
        signUpViewModel.apply {
            popUpTextLiveData.observe(viewLifecycleOwner) {
                showToast(requireContext(), it)
            }
            accountSuccessfullyCreatedLiveData.observe(viewLifecycleOwner) {
                findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
            }
        }
    }

    private fun setUpOnBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_signUpFragment_to_signinOrSignUpFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

}