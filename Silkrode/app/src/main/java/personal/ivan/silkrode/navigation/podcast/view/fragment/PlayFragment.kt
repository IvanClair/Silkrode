package personal.ivan.silkrode.navigation.podcast.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import dagger.android.support.DaggerFragment
import personal.ivan.silkrode.R
import personal.ivan.silkrode.databinding.FragmentPlayBinding
import personal.ivan.silkrode.di.AppViewModelFactory
import personal.ivan.silkrode.navigation.podcast.viewmodel.PodcastViewModel
import javax.inject.Inject

class PlayFragment : DaggerFragment() {

    // View Model
    @Inject
    lateinit var viewModelFactory: AppViewModelFactory
    private val mViewModel: PodcastViewModel by activityViewModels { viewModelFactory }

    // View Binding
    private lateinit var mBinding: FragmentPlayBinding

    // Argument
    private val mArguments by navArgs<PlayFragmentArgs>()

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
            FragmentPlayBinding.inflate(
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
        mBinding.textViewContentTitle.transitionName = mArguments.index.toString()
        mBinding.textViewContentTitle.text = mArguments.index.toString()
        setNavBackIcon()
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
}