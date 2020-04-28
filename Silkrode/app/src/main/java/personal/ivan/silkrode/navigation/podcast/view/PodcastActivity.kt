package personal.ivan.silkrode.navigation.podcast.view

import android.os.Bundle
import android.transition.TransitionManager
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import dagger.android.support.DaggerAppCompatActivity
import personal.ivan.silkrode.R
import personal.ivan.silkrode.databinding.FragmentPlayBinding
import personal.ivan.silkrode.di.AppViewModelFactory
import personal.ivan.silkrode.extension.enableOrDisable
import personal.ivan.silkrode.extension.setTintForPlayerStatus
import personal.ivan.silkrode.navigation.podcast.viewmodel.PodcastViewModel
import personal.ivan.silkrode.util.DateFormatUtil
import javax.inject.Inject

class PodcastActivity : DaggerAppCompatActivity() {

    // View Model
    @Inject
    lateinit var viewModelFactory: AppViewModelFactory
    private val mViewModel: PodcastViewModel by viewModels { viewModelFactory }

    @Inject
    lateinit var dateUtil: DateFormatUtil

    // View Binding
//    private val mBinding: ActivityPodcastBinding by lazy {
//        ActivityPodcastBinding.inflate(layoutInflater)
//    }

    private val mBinding: FragmentPlayBinding by lazy {
        FragmentPlayBinding.inflate(layoutInflater)
    }

    private var force = true

    /* ------------------------------ Life Cycle */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        mViewModel.startPodcastService()

        mBinding.progressBarLoading enableOrDisable false
        initSeekBar()
        observeLiveData()

        mBinding.imageViewPauseOrPlay.setOnClickListener {
            mViewModel.playPodcast(url = "", forceUpdate = force)
            force = false
        }

        mBinding.imageViewForward.setOnClickListener {
            mViewModel.seekPodcast(seconds = 30, direct = false)
        }

        mBinding.imageViewReplay.setOnClickListener {
            mViewModel.seekPodcast(seconds = -30, direct = false)
        }
    }

    /* ------------------------------ Observe LiveData */

    private fun observeLiveData() {
        mViewModel.apply {

            // enable or disable audio controls
            audioControlsEnabled.observe(
                this@PodcastActivity,
                Observer {
                    mBinding.progressBarLoading enableOrDisable !it
                    switchProgressVisibility(enabled = it)
                    setAudioControlTint(enabled = it)
                })

            // is playing or paused
            playOrPause.observe(
                this@PodcastActivity,
                Observer { changePlayOrPauseImage(playing = it) }
            )

            // total duration of the podcast
            totalDuration.observe(
                this@PodcastActivity,
                Observer { setTotalDuration(it) })
        }
    }

    /* ------------------------------ UI */

    private fun setAudioControlTint(enabled: Boolean) {
        mBinding.apply {
            imageViewPauseOrPlay setTintForPlayerStatus enabled
            imageViewForward setTintForPlayerStatus enabled
            imageViewReplay setTintForPlayerStatus enabled
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
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    val duration = p0?.progress ?: 0
                    mViewModel.seekPodcast(seconds = duration, direct = true)
                    setCurrentDuration(duration = duration)
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
        mBinding.textViewCurrentDuration.text =
            dateUtil.formatPlayerDuration(duration = duration.toLong())

    }

    private fun setTotalDuration(duration: Int) {
        mBinding.apply {
            seekBarPodCast.max = duration
            textViewTotalDuration.text =
                dateUtil.formatPlayerDuration(duration = duration.toLong())
        }
    }
}