package personal.ivan.silkrode.navigation.podcast.view.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.android.support.DaggerFragment
import personal.ivan.silkrode.R
import personal.ivan.silkrode.databinding.FragmentPlayBinding
import personal.ivan.silkrode.di.AppViewModelFactory
import personal.ivan.silkrode.di.GlideApp
import personal.ivan.silkrode.extension.enableOrDisable
import personal.ivan.silkrode.extension.setTintForPlayerStatus
import personal.ivan.silkrode.navigation.podcast.viewmodel.PodcastViewModel
import personal.ivan.silkrode.util.DateFormatUtil
import javax.inject.Inject

class PlayFragment : DaggerFragment() {

    // View Model
    @Inject
    lateinit var viewModelFactory: AppViewModelFactory
    private val mViewModel: PodcastViewModel by activityViewModels { viewModelFactory }

    // Date Util
    @Inject
    lateinit var dateUtil: DateFormatUtil

    // View Binding
    private lateinit var mBinding: FragmentPlayBinding

    // Argument
    private val mArguments by navArgs<PlayFragmentArgs>()

    // Flag
    // indicate the url should force update current playing content or not
    private var mForceUpdate = true

    // indicate should this page keep update progress from view model or not
    // if not, it means the current playing content is not this one
    private var mPlayingContent = false
    private var mDragging = false

    // Timer
    private var mTimer: CountDownTimer? = null

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
        // initial flags
        initFlags()

        // initial UI
        mBinding.progressBarLoading enableOrDisable false
        setNavBackIcon()
        setUpInformation()
        initSeekBar()
        initAudioControls()
        switchForwardAndReplay(enabled = false)
        observeLiveData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mTimer?.cancel()
        mTimer = null
    }

    /* ------------------------------ Observe LiveData */

    private fun observeLiveData() {
        mViewModel.apply {

            // enable or disable audio controls
            audioControlsEnabled.observe(
                viewLifecycleOwner,
                Observer {
                    // only update if it is target content
                    if (mPlayingContent) {
                        mBinding.progressBarLoading enableOrDisable !it
                        switchProgressVisibility(enabled = it)
                        switchAudioControls(enabled = it)
                    }
                })

            // is playing or paused
            playOrPause.observe(
                viewLifecycleOwner,
                Observer {
                    // only update if it is target content
                    if (mPlayingContent) {
                        changePlayOrPauseImage(playing = it)
                    }
                }
            )

            // total duration of the podcast
            totalDuration.observe(
                viewLifecycleOwner,
                Observer {
                    if (mPlayingContent) {
                        setTotalDuration(duration = it)
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
     * Set up title and description
     */
    private fun setUpInformation() {
        with(mArguments.contentFeed) {
            mBinding.apply {
                GlideApp
                    .with(imageViewCover)
                    .load(mViewModel.getSelectedCollectionCoverImageUrl())
                    .into(imageViewCover)
                textViewContentTitle.text = title
                textViewContentDescription.text = description
                textViewContentPublishDate.text = publishDate
            }
        }
    }

    private fun initAudioControls() {
        mBinding.apply {
            imageViewPauseOrPlay.setOnClickListener {
                // reset playing content flag if this time is using force update
                if (mForceUpdate) {
                    mPlayingContent = true
                }

                mViewModel.playPodcast(
                    url = mArguments.contentFeed.contentUrl,
                    forceUpdate = mForceUpdate
                )

                // did force update,
                // change the flag to false,
                // next time would not force update
                mForceUpdate = false
            }

            imageViewForward.setOnClickListener {
                mViewModel.seekPodcast(seconds = 30, direct = false)
            }

            imageViewReplay.setOnClickListener {
                mViewModel.seekPodcast(seconds = -30, direct = false)
            }
        }
    }

    private fun switchAudioControls(enabled: Boolean) {
        mBinding.apply {
            imageViewPauseOrPlay setTintForPlayerStatus enabled
            imageViewPauseOrPlay.isEnabled = enabled
            switchForwardAndReplay(enabled = enabled)
        }
    }

    private fun switchForwardAndReplay(enabled: Boolean) {
        mBinding.apply {
            imageViewForward setTintForPlayerStatus enabled
            imageViewForward.isEnabled = enabled
            imageViewReplay setTintForPlayerStatus enabled
            imageViewReplay.isEnabled = enabled
        }
    }

    private fun changePlayOrPauseImage(playing: Boolean) {
        mBinding.imageViewPauseOrPlay.setImageResource(
            if (playing) R.drawable.pause_circle_filled_24px
            else R.drawable.play_arrow_24px
        )
    }

    private fun initSeekBar() {
        mBinding.apply {
            // disable progress related UIs
            switchProgressVisibility(enabled = false)

            // set up seek bar drag listener
            seekBarPodCast.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    p0: SeekBar?,
                    p1: Int,
                    p2: Boolean
                ) {
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    mDragging = true
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    mDragging = false
                    mViewModel.seekPodcast(
                        seconds = p0?.progress ?: 0,
                        direct = true
                    )
                }
            })
        }
    }

    private fun switchProgressVisibility(enabled: Boolean) {
        mBinding.apply {
            textViewCurrentDuration enableOrDisable enabled
            textViewTotalDuration enableOrDisable enabled
            seekBarPodCast enableOrDisable enabled
        }
    }

    private fun setCurrentDuration(duration: Int) {
        mBinding.apply {
            // avoid seek bar unexpected moving
            if (!mDragging) {
                seekBarPodCast.progress = duration
            }
            textViewCurrentDuration.text =
                dateUtil.formatPlayerDuration(duration = duration.toLong())
        }

    }

    private fun setTotalDuration(duration: Int) {
        mBinding.apply {
            seekBarPodCast.max = duration
            textViewTotalDuration.text =
                dateUtil.formatPlayerDuration(duration = duration.toLong())
            initTimer(duration = duration)
        }
    }

    /* ------------------------------ Timer */

    /**
     * Initial timer for counting the play time
     * Note : this is an infinite timer
     */
    private fun initTimer(duration: Int) {
        mTimer?.cancel()
        mTimer =
            object : CountDownTimer(duration.toLong(), 100) {
                override fun onTick(millisUntilFinished: Long) {
                    setCurrentDuration(duration = mViewModel.getCurrentPodcastDuration())
                }

                override fun onFinish() {
                    start()
                }
            }.apply { start() }
    }

    /* ------------------------------ Get Data */

    /**
     * Initial flags for logic
     */
    private fun initFlags() {
        val playingUrl = mViewModel.getCurrentPlayingUrl()
        val currentUrl = mArguments.contentFeed.contentUrl
        mForceUpdate = playingUrl != currentUrl
        mPlayingContent = playingUrl.isEmpty() || playingUrl == currentUrl
    }
}