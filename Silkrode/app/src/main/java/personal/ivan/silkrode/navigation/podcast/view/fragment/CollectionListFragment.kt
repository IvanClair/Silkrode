package personal.ivan.silkrode.navigation.podcast.view.fragment

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
import personal.ivan.silkrode.databinding.FragmentCollectionListBinding
import personal.ivan.silkrode.di.AppViewModelFactory
import personal.ivan.silkrode.extension.showApiErrorAlert
import personal.ivan.silkrode.extension.switchLoadingProcess
import personal.ivan.silkrode.navigation.podcast.viewmodel.PodcastViewModel
import personal.ivan.silkrode.util.GlideUtil
import javax.inject.Inject

class CollectionListFragment : DaggerFragment() {

    // View Model
    @Inject
    lateinit var viewModelFactory: AppViewModelFactory
    private val mViewModel: PodcastViewModel by activityViewModels { viewModelFactory }

    // View Binding
    private lateinit var mBinding: FragmentCollectionListBinding

    // Glide
    @Inject
    lateinit var glideUtil: GlideUtil

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
        setToolbarTitle()
        initRecyclerView()
        observeLiveData()
        // request API
        mViewModel.requestCollectionApi(id = mArguments.id)
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

            // collection from API response
            collection.observe(
                viewLifecycleOwner,
                Observer {
                    loadCoverImage(url = it.bigCoverImgUrl ?: "")
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
     * Check Toolbar title when got API response
     */
    private fun checkToolbarTitleWhenGotApiResponse() {
        if (mExpanded) mBinding.collapsingToolbarLayout.title = " "
    }

    /**
     * Loading cover image
     */
    private fun loadCoverImage(url: String) {
        glideUtil.loadPodcastCover(
            imageView = mBinding.imageViewCover,
            url = url,
            completeListener = { checkToolbarTitleWhenGotApiResponse() })
    }

    /**
     * Initial collection feed list
     */
    private fun initRecyclerView() {
    }

    /**
     * Update collection feed list
     */
    private fun updateRecyclerView() {
    }

    /* ------------------------------ Navigation */

    /**
     * Navigate to [PlayFragment]
     */
    private fun navigateToPlay() {
    }
}