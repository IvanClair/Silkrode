package personal.ivan.silkrode.navigation.podcast.view

import android.os.Bundle
import androidx.activity.viewModels
import dagger.android.support.DaggerAppCompatActivity
import personal.ivan.silkrode.databinding.ActivityPodcastBinding
import personal.ivan.silkrode.di.AppViewModelFactory
import personal.ivan.silkrode.navigation.podcast.viewmodel.PodcastViewModel
import javax.inject.Inject

class PodcastActivity : DaggerAppCompatActivity() {

    // View Model
    @Inject
    lateinit var viewModelFactory: AppViewModelFactory
    private val mViewModel: PodcastViewModel by viewModels { viewModelFactory }

    // View Binding
    private val mBinding: ActivityPodcastBinding by lazy {
        ActivityPodcastBinding.inflate(layoutInflater)
    }

    /* ------------------------------ Life Cycle */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        observeLiveData()
    }

    /* ------------------------------ Observe LiveData */

    /**
     * Observe LiveData in [PodcastViewModel]
     */
    private fun observeLiveData() {
        mViewModel.apply {
        }
    }
}