package com.photogallery.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.photogallery.R
import com.photogallery.data.Downloader
import com.photogallery.databinding.FragmentPhotoGalleryBinding
import com.photogallery.domain.Photo
import com.photogallery.presentation.adapters.PhotoGalleryAdapter
import com.photogallery.presentation.utils.OnQueryTextListenerImpl
import com.photogallery.presentation.viewModels.PhotoGalleryFragmentViewModel
import java.lang.RuntimeException

class PhotoGalleryFragment : VisibleFragment() {

    private var _binding: FragmentPhotoGalleryBinding? = null
    private val binding: FragmentPhotoGalleryBinding
    get() = _binding ?: throw RuntimeException("binding is null")
    private val viewModel: PhotoGalleryFragmentViewModel by viewModels()
    private lateinit var adapter: PhotoGalleryAdapter
    private var downloader = Downloader<PhotoGalleryAdapter.PhotoHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(downloader.lifecycleObserver)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentPhotoGalleryBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSearchViewListeners()
        viewLifecycleOwner.lifecycle.addObserver(downloader.viewLifecycleObserver)
        adapter = PhotoGalleryAdapter(downloader){
            beginFragment(it)
        }
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(),3)
        binding.recyclerView.adapter = adapter
        viewModel.photoListLiveData.observe(viewLifecycleOwner){
            updateUI(it)
        }
        createPopupMenu()
        setSwitchItemListener()
    }

    private fun updateUI(photoList: List<Photo>){
        binding.progressBar.visibility = if(photoList.isNotEmpty()){
            View.GONE
        } else{
            View.VISIBLE
        }
        adapter.photoList = photoList
    }

    private fun setSearchViewListeners(){
        binding.searchView.apply {
            setOnQueryTextListener(object: OnQueryTextListenerImpl() {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let { viewModel.getSearchPhotos(it) }
                    clearFocus()
                    onActionViewCollapsed()
                    updateUI(emptyList())
                    switchItemVisibility()
                    return true
                }
            })
            setOnSearchClickListener { binding.linearLayout.visibility = View.GONE }
            setOnCloseListener { switchItemVisibility(); false }
        }
    }

    private fun setSwitchItemListener(){
        binding.switchWidget.isChecked = viewModel.preferences.getIsNotificationOn()
        binding.switchWidget.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                viewModel.createWorker(requireContext())
                showToast(getString(R.string.notifications_enabled))
            } else {
                viewModel.removeWorker(requireContext())
                showToast(getString(R.string.notifications_disabled))
            }
        }
    }

    private fun createPopupMenu(){
        val popupMenu = PopupMenu(requireContext(), binding.menu)
        popupMenu.menu.add("Clear")
        popupMenu.setOnMenuItemClickListener {
            when(it.toString()){
                "Clear" -> {
                    if(viewModel.clearQuery())
                        updateUI(emptyList())
                }
            }
            return@setOnMenuItemClickListener true
        }
        binding.menu.setOnClickListener{
            popupMenu.show()
        }
    }

    private fun showToast(expression: String){
        Toast.makeText(requireContext(), expression, Toast.LENGTH_SHORT).show()
    }

    private fun switchItemVisibility(){
        binding.linearLayout.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewLifecycleOwner.lifecycle.removeObserver(downloader.viewLifecycleObserver)
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(downloader.lifecycleObserver)
    }

    private fun beginFragment(uri: String){
        parentFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
            .replace(
                R.id.fragmentContainer,
                PhotoFragment.newInstance(uri))
            .addToBackStack(null)
            .commit()
    }

    companion object{
        fun newInstance(): PhotoGalleryFragment{
            return PhotoGalleryFragment()
        }
    }
}