package personal.ivan.silkrode.navigation.podcast.view.fragment

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import personal.ivan.silkrode.R
import personal.ivan.silkrode.databinding.FragmentPlayBinding
import personal.ivan.silkrode.di.AppViewModelFactory
import personal.ivan.silkrode.navigation.podcast.model.CollectionVhBindingModel
import personal.ivan.silkrode.navigation.podcast.viewmodel.PodcastViewModel
import personal.ivan.silkrode.util.GlideUtil
import javax.inject.Inject

class PlayFragment : DaggerFragment() {

    // View Model
    @Inject
    lateinit var viewModelFactory: AppViewModelFactory
    private val mViewModel: PodcastViewModel by activityViewModels { viewModelFactory }

    // Glide
    @Inject
    lateinit var util: GlideUtil

    // View Binding
    private lateinit var mBinding: FragmentPlayBinding

    // Argument
    private val mArguments by navArgs<PlayFragmentArgs>()

    /* ------------------------------ Life Cycle */

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
        setNavBackIcon()
        setUpInformation()
        initAudioControl()
        playContent()
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
     * Set up title and description
     */
    private fun setUpInformation() {
        getData()?.also {
            mBinding.apply {
                util.loadPodcastCover(
                    imageView = imageViewCover,
                    url = mViewModel.getSelectedCoverImageUrl()
                )
                textViewContentTitle.text = it.title
                textViewContentDescription.text = it.description
                textViewContentPublishDate.text = it.publishDate
            }
        }
    }

    /**
     * Initial audio control UIs
     */
    private fun initAudioControl() {
        mBinding.apply {
            imageViewPauseOrPlay.setOnClickListener {  }
            imageViewReplay.setOnClickListener {  }
            imageViewForward.setOnClickListener {  }
        }
    }

    /* ------------------------------ Audio */

    /**
     * Play content
     */
    private fun playContent() {
        getData()?.also {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes
                            .Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    setDataSource(it.contentUrl)
                    prepare()
                    start()
                }
            }
        }
    }

    /* ------------------------------ Get Data */

    /**
     * Get selected content data
     */
    private fun getData(): CollectionVhBindingModel.ContentVhBindingModel? =
        mViewModel.getSelectedContent(mArguments.index)
}