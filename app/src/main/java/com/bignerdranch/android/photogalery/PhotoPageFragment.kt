package com.bignerdranch.android.photogalery

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.bignerdranch.android.photogalery.databinding.FragmentPhotoPageBinding

private const val ARG_URI = "photo_page_url"

class PhotoPageFragment: VisibleFragment() {

    private lateinit var uri: Uri
    private lateinit var binding: FragmentPhotoPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uri = arguments?.getParcelable(ARG_URI) ?: Uri.EMPTY
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPhotoPageBinding.inflate(inflater)
        binding.progressBar.max = 100
        binding.webView.apply {
            settings.javaScriptEnabled = true
            webChromeClient = object: WebChromeClient(){
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    if(newProgress == 100){
                        binding.progressBar.visibility = View.GONE
                    }else {
                        binding.progressBar.apply{
                            visibility = View.VISIBLE
                            progress = newProgress
                        }
                    }
                }
                override fun onReceivedTitle(view: WebView?, title: String?) {
                    (activity as AppCompatActivity).supportActionBar?.subtitle = title
                }
            }
            webViewClient = WebViewClient()
            loadUrl(uri.toString())
        }

        return binding.root
    }

    fun getWebView() : WebView{
        return  binding.webView
    }

    companion object{
        fun newInstance(uri: Uri?): PhotoPageFragment{
            return PhotoPageFragment().apply { arguments = Bundle().apply { putParcelable(ARG_URI,uri) } }
        }
    }

}