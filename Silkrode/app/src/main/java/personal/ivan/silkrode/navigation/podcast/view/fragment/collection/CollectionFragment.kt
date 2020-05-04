package personal.ivan.silkrode.navigation.podcast.view.fragment.collection

import android.os.Bundle
import android.os.Handler
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
import personal.ivan.silkrode.databinding.FragmentCollectionBinding
import personal.ivan.silkrode.di.AppViewModelFactory
import personal.ivan.silkrode.di.GlideApp
import personal.ivan.silkrode.extension.enableOrDisable
import personal.ivan.silkrode.navigation.podcast.view.fragment.PlayFragment
import personal.ivan.silkrode.navigation.podcast.viewmodel.PodcastViewModel
import javax.inject.Inject
import kotlin.math.abs

class CollectionFragment : DaggerFragment() {

    // View Model
    @Inject
    lateinit var viewModelFactory: AppViewModelFactory
    private val mViewModel: PodcastViewModel by activityViewModels { viewModelFactory }

    // View Binding
    private lateinit var mBinding: FragmentCollectionBinding

    // Argument
    private val mArguments by navArgs<CollectionFragmentArgs>()

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
            FragmentCollectionBinding.inflate(
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewModel.setCoverImageExpand(expand = mExpanded)
    }

    /* ------------------------------ Observe LiveData */

    private fun observeLiveData() {
        mViewModel.apply {

            // app bar layout expand or collapse
            expandCollapsingToolBarLayout.observe(
                viewLifecycleOwner,
                Observer { switchAppBarLayoutExpandStatus(expand = it) })

            // collection from API response
            collectionBindingModel.observe(
                viewLifecycleOwner,
                Observer {
                    switchLoadingStatus(enable = it.status == ApiStatus.LOADING)
                    if (it.status == ApiStatus.SUCCESS) {
                        updateFromData()
                    }
                })
        }
    }

    private fun updateFromData() {
        mApiDataLoaded = true
        setToolbarTitle()
        updateRecyclerView()
        loadCoverImage()
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
     * Expand or collapse app bar layout at first time
     */
    private fun switchAppBarLayoutExpandStatus(expand: Boolean) {
        mBinding.appBarLayout.setExpanded(expand, true)
    }

    /**
     * Set Toolbar title on expand only
     */
    private fun setToolbarTitle() {
        val title = getString(R.string.title_all_episodes)
        mBinding.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) - appBarLayout.totalScrollRange == 0) {
                //  Collapsed
                mBinding.collapsingToolbarLayout.title = title
                mExpanded = false

            } else {
                //Expanded
                mBinding.collapsingToolbarLayout.title = if (mApiDataLoaded) " " else title
                mExpanded = true
            }
        })
    }

    /**
     * Loading cover image
     *
     * load the image for a short delay to wait transition
     */
    private fun loadCoverImage() {
        Handler().postDelayed({
            mBinding.imageViewCover.apply {
                GlideApp
                    .with(this)
                    .load(mViewModel.getSelectedCollectionCoverImageUrl())
                    .into(this)
            }
        }, 300)
    }

    /**
     * Initial collection feed list
     */
    private fun initRecyclerView() {
        mBinding.recyclerViewCollection.apply {

            // set up adapter
            adapter =
                ContentFeedListAdapter().apply {
                    setOnItemClickListener(View.OnClickListener {
                        navigateToPlay(index = it.tag as Int)
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
        (mBinding.recyclerViewCollection.adapter as? ContentFeedListAdapter)
            ?.submitList(mViewModel.getContentFeedList())
    }

    /* ------------------------------ Navigation */

    /**
     * Navigate to [PlayFragment]
     */
    private fun navigateToPlay(index: Int) {
        mViewModel
            .getContentFeed(index = index)
            ?.also {
                findNavController().navigate(
                    CollectionFragmentDirections.navigateToPlay(
                        contentFeed = it
                    )
                )
            }
    }
}