package personal.ivan.silkrode.navigation.podcast.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import dagger.android.support.DaggerFragment
import personal.ivan.silkrode.R
import personal.ivan.silkrode.di.AppViewModelFactory
import personal.ivan.silkrode.navigation.podcast.viewmodel.PodcastViewModel
import javax.inject.Inject

class ChannelListFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: AppViewModelFactory
    private val mViewMode: PodcastViewModel by activityViewModels { viewModelFactory }

    /* ------------------------------ Life Cycle */

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_channel_list, container, false)
    }
}