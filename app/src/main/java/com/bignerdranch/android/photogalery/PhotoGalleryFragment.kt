package com.bignerdranch.android.photogalery

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.work.*
import com.bignerdranch.android.photogalery.databinding.FragmentPhotoGalleryBinding
import java.util.concurrent.TimeUnit

private const val POLL_WORK = "POLL_WORK"

class PhotoGalleryFragment : VisibleFragment() {

    private lateinit var binding: FragmentPhotoGalleryBinding
    private lateinit var adapter: AdapterAndHolder
    private lateinit var thumbnailDownloader: ThumbnailDownloader<AdapterAndHolder.Holder>
    private lateinit var switcherPolling: MenuItem
    private val photoGalleryViewModel: PhotoGalleryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        retainInstance = true
        val responseHandler = Handler(Looper.getMainLooper())
        thumbnailDownloader = ThumbnailDownloader(responseHandler)
        lifecycle.addObserver(thumbnailDownloader.fragmentLifecycleObserver)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewLifecycleOwner.lifecycle.addObserver(thumbnailDownloader.viewLifecycleObserver)
        binding = FragmentPhotoGalleryBinding.inflate(inflater)
        adapter = AdapterAndHolder(emptyList(), thumbnailDownloader,this)
        updateUI(emptyList())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photoGalleryViewModel.galleryItemLiveData.observe(viewLifecycleOwner) {
            updateUI(it)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_photo_gallery, menu)
        val searchItem = menu.findItem(R.id.menu_item_search)
        val searchView = searchItem.actionView as SearchView
        switcherPolling = menu.findItem(R.id.menu_item_toggle_polling)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { photoGalleryViewModel.fetchPhotos(it) }
                updateUI(emptyList())
                searchView.clearFocus()
                searchView.onActionViewCollapsed()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        searchView.setOnSearchClickListener {
            searchView.setQuery(photoGalleryViewModel.word, false)
        }

        updateSwitchName()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_clear -> {
                photoGalleryViewModel.fetchPhotos()
                updateUI(emptyList())
                true
            }
            R.id.menu_item_toggle_polling -> {
                if (QueryPreferences.isPolling(requireContext())) {
                    WorkManager.getInstance(requireContext()).cancelUniqueWork(POLL_WORK)
                    QueryPreferences.setIsPolling(requireContext(), false)
                    updateSwitchName()
                } else {
                    createWorker()
                    QueryPreferences.setIsPolling(requireContext(), true)
                    updateSwitchName()
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateUI(list: List<GalleryItem>) {
        adapter.photoList = list
        if (list.isEmpty()) {
            binding.recyclerView.layoutManager = GridLayoutManager(context, 1)
        } else {
            binding.recyclerView.layoutManager = GridLayoutManager(context, 3)
        }
        binding.recyclerView.adapter = adapter
    }

    private fun updateSwitchName() {
        if (QueryPreferences.isPolling(requireContext())) {
            switcherPolling.setTitle(R.string.stop_polling)
        } else {
            switcherPolling.setTitle(R.string.start_polling)
        }
    }

    private fun createWorker() {
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED).build()
        val periodicRequest = PeriodicWorkRequest.Builder(PollWorker::class.java, 15, TimeUnit.MINUTES).setConstraints(constraints).build()
        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(POLL_WORK, ExistingPeriodicWorkPolicy.KEEP, periodicRequest)
    }

    companion object {
        @JvmStatic
        fun newInstance() = PhotoGalleryFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewLifecycleOwner.lifecycle.removeObserver(thumbnailDownloader.viewLifecycleObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(thumbnailDownloader.fragmentLifecycleObserver)
    }
}