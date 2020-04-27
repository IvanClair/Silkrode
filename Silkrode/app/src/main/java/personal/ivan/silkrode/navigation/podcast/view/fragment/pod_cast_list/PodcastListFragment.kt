package personal.ivan.silkrode.navigation.podcast.view.fragment.pod_cast_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.support.DaggerFragment
import personal.ivan.silkrode.R
import personal.ivan.silkrode.databinding.FragmentPodcastListBinding
import personal.ivan.silkrode.di.AppViewModelFactory
import personal.ivan.silkrode.navigation.podcast.viewmodel.PodcastViewModel
import javax.inject.Inject

class PodcastListFragment : DaggerFragment() {

    // View Model
    @Inject
    lateinit var viewModelFactory: AppViewModelFactory
    private val mViewModel: PodcastViewModel by activityViewModels { viewModelFactory }

    // Binding
    private lateinit var mBinding: FragmentPodcastListBinding

    // Adapter
    @Inject
    lateinit var podcastListAdapter: PodcastListAdapter

    /* ------------------------------ Life Cycle */

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding =
            FragmentPodcastListBinding.inflate(
                inflater,
                container,
                false
            )
        return mBinding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        observeLiveData()
    }

    /* ------------------------------ Observe LiveData */

    private fun observeLiveData() {
        mViewModel.apply {

            // API status - loading
            apiLoading.observe(
                viewLifecycleOwner,
                Observer { switchLoadingProgress(enable = it) })

            // API status - fail
            apiFail.observe(
                viewLifecycleOwner,
                Observer { showApiError() })

            // podcast list from API response
            podcastList.observe(
                viewLifecycleOwner,
                Observer { updateRecyclerView() })
        }
    }

    /* ------------------------------ UI */

    /**
     * Control loading progress bar
     */
    private fun switchLoadingProgress(enable: Boolean) {
        mBinding.progressBarLoading.visibility = if (enable) View.VISIBLE else View.GONE
    }

    /**
     * API error dialog
     */
    private fun showApiError() {
        MaterialAlertDialogBuilder(mBinding.root.context)
            .setTitle(R.string.alert_title)
            .setMessage(R.string.alert_message)
            .setPositiveButton(R.string.label_ok, null)
            .show()
    }

    /**
     * Initial podcast list
     */
    private fun initRecyclerView() {
        mBinding.recyclerViewPodcast.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 2)
            adapter =
                podcastListAdapter.also { adapter ->

                    // view holder click listener
                    adapter.setOnItemClickListener(object :
                        PodcastListAdapter.OnPodcastItemClickListener {
                        override fun onClick(
                            imageView: ImageView,
                            id: String
                        ) {
                            navigateToCollectionList(imageView = imageView, id = id)
                        }
                    })

                    // return transition
                    viewTreeObserver.addOnPreDrawListener {
                        startPostponedEnterTransition()
                        true
                    }
                }
        }
    }

    /**
     * Update podcast list
     */
    private fun updateRecyclerView() {
        (mBinding.recyclerViewPodcast.adapter as? PodcastListAdapter)
            ?.updateDataSource(viewModel = mViewModel)
    }

    /* ------------------------------ Navigation */

    private fun navigateToCollectionList(
        imageView: ImageView,
        id: String
    ) {
        findNavController().navigate(
            PodcastListFragmentDirections.navigateToCollectionList(id = id),
            FragmentNavigatorExtras(imageView to id)
        )
    }
}