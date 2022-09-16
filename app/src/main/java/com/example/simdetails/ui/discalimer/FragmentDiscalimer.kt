package com.example.simdetails.ui.discalimer

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.simdetails.R
import com.example.simdetails.databinding.FragmentDiscalimerBinding

class FragmentDiscalimer : Fragment(R.layout.fragment_discalimer) {
    private lateinit var _binding: FragmentDiscalimerBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDiscalimerBinding.bind(view)
        _binding.icBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}