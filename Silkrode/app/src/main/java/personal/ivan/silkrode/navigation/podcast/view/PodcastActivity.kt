package personal.ivan.silkrode.navigation.podcast.view

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import dagger.android.support.DaggerAppCompatActivity
import personal.ivan.silkrode.R
import personal.ivan.silkrode.databinding.ActivityPodcastBinding
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
    private val mBinding: ActivityPodcastBinding by lazy {
        ActivityPodcastBinding.inflate(layoutInflater)
    }

    /* ------------------------------ Life Cycle */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        mViewModel.startPodcastService()
    }
}