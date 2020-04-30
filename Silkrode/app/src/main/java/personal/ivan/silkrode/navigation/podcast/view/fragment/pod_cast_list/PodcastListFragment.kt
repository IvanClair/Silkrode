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
import dagger.android.support.DaggerFragment
import personal.ivan.silkrode.api.ApiStatus
import personal.ivan.silkrode.databinding.FragmentPodcastListBinding
import personal.ivan.silkrode.di.AppViewModelFactory
import personal.ivan.silkrode.extension.enableOrDisable
import personal.ivan.silkrode.extension.showApiErrorAlert
import personal.ivan.silkrode.navigation.podcast.view.fragment.collection_list.CollectionListFragment
import personal.ivan.silkrode.navigation.podcast.viewmodel.PodcastViewModel
import javax.inject.Inject

class PodcastListFragment : DaggerFragment() {

    // View Model
    @Inject
    lateinit var viewModelFactory: AppViewModelFactory
    private val mViewModel: PodcastViewModel by activityViewModels { viewModelFactory }

    // View Binding
    private lateinit var mBinding: FragmentPodcastListBinding

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

            // podcast list from API response
            podcastList.observe(
                viewLifecycleOwner,
                Observer {
                    switchLoadingStatus(enable = it.status == ApiStatus.LOADING)
                    when (it.status) {
                        ApiStatus.FAIL -> context?.showApiErrorAlert()
                        ApiStatus.SUCCESS -> updateRecyclerView()
                    }
                })
        }
    }

    /* ------------------------------ UI */

    /**
     * Show or hide loading progress
     */
    private fun switchLoadingStatus(enable: Boolean) {
        mBinding.progressBarLoading enableOrDisable enable
    }

    /**
     * Initial podcast list
     */
    private fun initRecyclerView() {
        mBinding.recyclerViewPodcast.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 2)

            // set up adapter
            adapter =
                PodcastListAdapter().apply {
                    setOnItemClickListener(object :
                        PodcastListAdapter.OnPodcastItemClickListener {
                        override fun onClick(
                            imageView: ImageView,
                            id: String
                        ) {
                            navigateToCollectionList(imageView = imageView, id = id)
                        }
                    })
                }

            // return transition
            postponeEnterTransition()
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
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

    /**
     * Navigate to [CollectionListFragment]
     */
    private fun navigateToCollectionList(
        imageView: ImageView,
        id: String
    ) {
        mViewModel.also {
            it.setCoverImageExpand(expand = true)
            it.requestCollectionApi(id = id)
        }
        findNavController().navigate(
            PodcastListFragmentDirections.navigateToCollectionList(id = id),
            FragmentNavigatorExtras(imageView to id)
        )
    }
}