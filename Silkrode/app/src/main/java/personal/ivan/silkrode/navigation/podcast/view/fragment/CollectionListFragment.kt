package personal.ivan.silkrode.navigation.podcast.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import dagger.android.support.DaggerFragment
import personal.ivan.silkrode.R
import personal.ivan.silkrode.databinding.FragmentCollectionListBinding
import personal.ivan.silkrode.di.AppViewModelFactory
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
    val arguments by navArgs<CollectionListFragmentArgs>()

    /* ------------------------------ Life Cycle */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        mBinding.imageViewCover.transitionName = arguments.id
        initNavigation()
        initRecyclerView()
        observeLiveData()
    }

    /* ------------------------------ Observe LiveData */

    private fun observeLiveData() {
        mViewModel.apply {
        }
    }

    /* ------------------------------ UI */

    private fun initNavigation() {
        mBinding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initRecyclerView() {
    }

    private fun updateRecyclerView() {
    }

    /* ------------------------------ Navigation */

    private fun navigateToPlay() {
        findNavController().navigate(R.id.action_collectionListFragment_to_playFragment)
    }
}