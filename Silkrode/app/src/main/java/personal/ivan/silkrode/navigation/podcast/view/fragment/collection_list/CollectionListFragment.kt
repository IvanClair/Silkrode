package personal.ivan.silkrode.navigation.podcast.view.fragment.collection_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.google.android.material.appbar.AppBarLayout
import dagger.android.support.DaggerFragment
import personal.ivan.silkrode.R
import personal.ivan.silkrode.api.ApiStatus
import personal.ivan.silkrode.databinding.FragmentCollectionListBinding
import personal.ivan.silkrode.di.AppViewModelFactory
import personal.ivan.silkrode.extension.enableOrDisable
import personal.ivan.silkrode.navigation.podcast.view.fragment.PlayFragment
import personal.ivan.silkrode.navigation.podcast.viewmodel.PodcastViewModel
import personal.ivan.silkrode.util.GlideUtil
import javax.inject.Inject

class CollectionListFragment : DaggerFragment() {

    // View Model
    @Inject
    lateinit var viewModelFactory: AppViewModelFactory
    private val mViewModel: PodcastViewModel by activityViewModels { viewModelFactory }

    // Glide
    @Inject
    lateinit var glideUtil: GlideUtil

    // View Binding
    private lateinit var mBinding: FragmentCollectionListBinding

    // Argument
    private val mArguments by navArgs<CollectionListFragmentArgs>()

    // Flag
    private var mApiDataLoaded: Boolean = false
    private var mExpanded: Boolean = true

    /* ------------------------------ Life Cycle */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // override shared element transition
        sharedElementEnterTransition =
            TransitionInflater
                .from(context)
                .inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding =
            FragmentCollectionListBinding.inflate(
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
        // set up shared element for transition
        mBinding.imageViewCover.transitionName = mArguments.id
        setNavBackIcon()
        initRecyclerView()
        observeLiveData()
        // request collection API
        mViewModel.requestCollectionApi(id = mArguments.id)
    }

    /* ------------------------------ Observe LiveData */

    private fun observeLiveData() {
        mViewModel.apply {

            // collection from API response
            collectionBindingModel.observe(
                viewLifecycleOwner,
                Observer {
                    switchLoadingStatus(enable = it.status == ApiStatus.LOADING)
                    if (it.status == ApiStatus.SUCCESS) {
                        mApiDataLoaded = true
                        loadCoverImage(url = mViewModel.getSelectedCollectionCoverImageUrl())
                        setToolbarTitle()
                        updateRecyclerView()
                    }
                })
        }
    }

    /* ------------------------------ UI */

    /**
     * Set navigation back button listener
     */
    private fun setNavBackIcon() {
        mBinding.toolbar.apply {
            navigationIcon?.setTint(ContextCompat.getColor(context, R.color.navIconColor))
            setNavigationOnClickListener { findNavController().navigateUp() }
        }
    }

    /**
     * Show or hide loading progress
     */
    private fun switchLoadingStatus(enable: Boolean) {
        mBinding.progressBarLoading enableOrDisable enable
    }

    /**
     * Set Toolbar title on expand only
     */
    private fun setToolbarTitle() {
        var scrollRange = -1
        val title = getString(R.string.title_all_episodes)
        mBinding.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { barLayout, verticalOffset ->
            if (scrollRange == -1) {
                scrollRange = barLayout?.totalScrollRange ?: -1
            }

            when {
                // expand
                scrollRange + verticalOffset == 0 -> {
                    mBinding.collapsingToolbarLayout.title = title
                    mExpanded = true
                }

                // collapse
                mExpanded -> {
                    //careful there should a space between double quote otherwise it wont work
                    mBinding.collapsingToolbarLayout.title = if (mApiDataLoaded) " " else title
                    mExpanded = false
                }
            }
        })
    }

    /**
     * Loading cover image
     */
    private fun loadCoverImage(url: String) {
        glideUtil.loadPodcastCover(
            imageView = mBinding.imageViewCover,
            url = url
        )
    }

    /**
     * Initial collection feed list
     */
    private fun initRecyclerView() {
        mBinding.recyclerViewCollection.apply {

            // set up adapter
            adapter =
                CollectionListAdapter().apply {
                    setOnItemClickListener(View.OnClickListener {
                        if (CollectionListAdapter.allowClick()) {
                            navigateToPlay(index = it.tag as Int)
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
     * Update collection feed list
     */
    private fun updateRecyclerView() {
        (mBinding.recyclerViewCollection.adapter as? CollectionListAdapter)
            ?.updateDataSource(viewModel = mViewModel)
    }

    /* ------------------------------ Navigation */

    /**
     * Navigate to [PlayFragment]
     */
    private fun navigateToPlay(index: Int) {
        findNavController().navigate(CollectionListFragmentDirections.navigateToPlay(index = index))
    }
}