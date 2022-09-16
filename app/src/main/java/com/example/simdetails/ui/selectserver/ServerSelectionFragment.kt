package com.example.simdetails.ui.selectserver

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.simdetails.data.adapters.CustomAdapter
import com.example.simdetails.data.model.Title
import com.example.simdetails.databinding.FragmentServerSelectionBinding
import com.example.simdetails.ui.base.BaseFragment
import com.example.simdetails.utils.RecyclerviewClickListener
import com.example.simdetails.utils.Urls
import com.example.simdetails.utils.checkForInternet
import com.example.simdetails.utils.showBottomSheetDialog

class ServerSelectionFragment : BaseFragment<FragmentServerSelectionBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentServerSelectionBinding =
        FragmentServerSelectionBinding::inflate

    private val viewModel by viewModels<ServerViewModel>()

    private val urls = Urls()
    val server1: String = urls.server1
    val server2: String = urls.server2
    val server3: String = urls.server3
    val server4: String = urls.server4

    override fun onCreatedView() {
        clickedListeners()
        setUpRV()
        if (checkForInternet(requireContext())) {
            checkForInternet(requireContext())
        } else {
            showBottomSheetDialog()
        }
    }

    private fun clickedListeners() {

        binding.apply {
            icBack.setOnClickListener {
                findNavController().navigateUp()
            }
            recyclerview.addOnItemTouchListener(
            RecyclerviewClickListener(requireActivity(), recyclerview,
                object : RecyclerviewClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        if (position == 0) {
                            findNavController().navigate(
                                ServerSelectionFragmentDirections.actionServerSelectionFragmentToBrowserFragment(
                                    server1
                                )
                            )
                        }
                        if (position == 1) {
                            findNavController().navigate(
                                ServerSelectionFragmentDirections.actionServerSelectionFragmentToBrowserFragment(
                                    server2
                                )
                            )
                        }
                        if (position == 2) {
                            findNavController().navigate(
                                ServerSelectionFragmentDirections.actionServerSelectionFragmentToBrowserFragment(
                                    server3
                                )
                            )
                        }
                        if (position == 3) {
                            findNavController().navigate(
                                ServerSelectionFragmentDirections.actionServerSelectionFragmentToBrowserFragment(
                                    server4
                                )
                            )
                        }
                    }

                    override fun onItemLongClick(view: View?, position: Int) {
                        Toast.makeText(requireActivity(), "Please Click", Toast.LENGTH_SHORT)
                            .show()
                    }
                }))
        }
    }

    private fun setUpRV() {

        val data = ArrayList<Title>()
        data.add(
            Title("Server 1")
        )
        data.add(
            Title("Server 2")
        )
        data.add(
            Title("Server 3")
        )
        data.add(
            Title("Server 4")
        )


        val adapter = CustomAdapter(data)
        binding.recyclerview.adapter = adapter
    }
}