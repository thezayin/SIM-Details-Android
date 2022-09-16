package com.example.simdetails.ui.webview

import android.app.ProgressDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.simdetails.databinding.FragmentBrowserBinding
import com.example.simdetails.ui.base.BaseFragment

class BrowserFragment : BaseFragment<FragmentBrowserBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentBrowserBinding =
        FragmentBrowserBinding::inflate

    private val args: BrowserFragmentArgs by navArgs()
    lateinit var progress: ProgressDialog

    override fun onCreatedView() {
        binding.apply {
            icBack.setOnClickListener {
                findNavController().navigateUp()
            }
            webView.webViewClient = WebViewClient()
            val url: String = args.url
            webView.loadUrl(url)
            webView.settings.javaScriptEnabled = true
            webView.settings.builtInZoomControls = false
            webView.settings.javaScriptCanOpenWindowsAutomatically = true
            webView.settings.setSupportZoom(true)
            progress = ProgressDialog.show(activity, "please waite a moment", "Loading...", true)
            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    progress.dismiss()
                }
            }
        }
    }
}