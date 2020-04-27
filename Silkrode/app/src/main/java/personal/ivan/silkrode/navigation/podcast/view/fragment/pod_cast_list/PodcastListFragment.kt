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
import personal.ivan.silkrode.databinding.FragmentPodcastListBinding
import personal.ivan.silkrode.di.AppViewModelFactory
import personal.ivan.silkrode.extension.showApiErrorAlert
import personal.ivan.silkrode.extension.switchLoadingProcess
import personal.ivan.silkrode.navigation.podcast.view.fragment.collection_list.CollectionListFragment
import personal.ivan.silkrode.navigation.podcast.viewmodel.PodcastViewModel
import javax.inject.Inject

class PodcastListFragment : DaggerFragment() {

    // View Model
    @Inject
    lateinit var viewModelFactory: AppViewModelFactory
    private val mViewModel: PodcastViewModel by activityViewModels { viewModelFactory }

    // Adapter
    @Inject
    lateinit var podcastListAdapter: PodcastListAdapter

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

            // API status - loading
            apiLoading.observe(
                viewLifecycleOwner,
                Observer { mBinding.progressBarLoading switchLoadingProcess it })

            // API status - fail
            apiFail.observe(
                viewLifecycleOwner,
                Observer { context?.showApiErrorAlert() })

            // podcast list from API response
            podcastList.observe(
                viewLifecycleOwner,
                Observer { updateRecyclerView() })
        }
    }

    /* ------------------------------ UI */

    /**
     * Initial podcast list
     */
    private fun initRecyclerView() {
        mBinding.recyclerViewPodcast.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 2)

            // set up adapter
            adapter = podcastListAdapter
            podcastListAdapter.setOnItemClickListener(object :
                PodcastListAdapter.OnPodcastItemClickListener {
                override fun onClick(
                    imageView: ImageView,
                    id: String
                ) {
                    // request collection API
                    mViewModel.requestCollectionApi(id = id)
                    // navigate to next page
                    navigateToCollectionList(imageView = imageView, id = id)
                }
            })

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
        findNavController().navigate(
            PodcastListFragmentDirections.navigateToCollectionList(id = id),
            FragmentNavigatorExtras(imageView to id)
        )
    }
}