package com.amadev.rando.ui.dialogs.informationDialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.amadev.rando.databinding.InformationDialogFragmentBinding

class InformationDialog(private val title: String, private val text: String) : DialogFragment() {

    private var _binding: InformationDialogFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = InformationDialogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDialogBackgroundColor()
        setUpTextViews()
        setTextViewVerticalMovementMethod(binding.text)
        setUpOnClickListeners()

    }

    private fun setUpOnClickListeners() {
        binding.apply {
            closeDialogBtn.setOnClickListener {
                closeDialog()
            }
        }
    }

    private fun closeDialog() {
        dialog?.dismiss()
    }

    private fun setUpTextViews() {
        binding.apply {
            title.text = this@InformationDialog.title
            text.text = this@InformationDialog.text
        }
    }

    private fun setDialogBackgroundColor() {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun setTextViewVerticalMovementMethod(view: TextView) {
        view.movementMethod = ScrollingMovementMethod.getInstance()
    }

}