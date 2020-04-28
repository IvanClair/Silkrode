package personal.ivan.silkrode.navigation.podcast.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import dagger.android.support.DaggerAppCompatActivity
import personal.ivan.silkrode.R
import personal.ivan.silkrode.databinding.FragmentPlayBinding
import personal.ivan.silkrode.di.AppViewModelFactory
import personal.ivan.silkrode.extension.setTintForPlayerStatus
import personal.ivan.silkrode.extension.switchLoadingProcess
import personal.ivan.silkrode.navigation.podcast.viewmodel.PodcastViewModel
import javax.inject.Inject

class PodcastActivity : DaggerAppCompatActivity() {

    // View Model
    @Inject
    lateinit var viewModelFactory: AppViewModelFactory
    private val mViewModel: PodcastViewModel by viewModels { viewModelFactory }

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
        observeLiveData()

        mBinding.imageViewPauseOrPlay.setOnClickListener {
            mViewModel.playPodcast(url = "", forceUpdate = force)
            force = false
        }

        mBinding.imageViewForward.setOnClickListener {
            mViewModel.seekPodcast(30)
        }

        mBinding.imageViewReplay.setOnClickListener {
            mViewModel.seekPodcast(-30)
        }
    }

    private fun observeLiveData() {
        mViewModel.apply {

            // enable or disable audio controls
            audioControlsEnabled.observe(
                this@PodcastActivity,
                Observer {
                    mBinding.progressBarLoading switchLoadingProcess !it
                    setAudioControlTint(enabled = it)
                })

            // is playing or paused
            playOrPause.observe(
                this@PodcastActivity,
                Observer { changePlayOrPauseImage(playing = it) }
            )
        }
    }

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
}