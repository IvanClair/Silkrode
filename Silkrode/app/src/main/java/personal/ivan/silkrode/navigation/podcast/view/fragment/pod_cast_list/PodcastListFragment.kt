package personal.ivan.silkrode.navigation.podcast.view.fragment.pod_cast_list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import dagger.android.support.DaggerFragment
import personal.ivan.silkrode.R
import personal.ivan.silkrode.databinding.FragmentPodcastListBinding
import personal.ivan.silkrode.di.AppViewModelFactory
import personal.ivan.silkrode.navigation.podcast.viewmodel.PodcastViewModel
import personal.ivan.silkrode.navigation.podcast.viewmodel.setToolbarTitle
import javax.inject.Inject

class PodcastListFragment : DaggerFragment() {

    // View Model
    @Inject
    lateinit var viewModelFactory: AppViewModelFactory
    private val mViewModel: PodcastViewModel by activityViewModels { viewModelFactory }

    // Binding
    private lateinit var mBinding: FragmentPodcastListBinding

    // Adapter
    @Inject
    lateinit var podcastListAdapter: PodcastListAdapter

    /* ------------------------------ Life Cycle */

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding =
            FragmentPodcastListBinding.inflate(
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
        mViewModel setToolbarTitle R.string.title_podcast
        initRecyclerView()
        observeLiveData()
    }

    /* ------------------------------ Observe LiveData */

    private fun observeLiveData() {
        mViewModel.apply {

            // podcast list from API response
            podcastList.observe(
                viewLifecycleOwner,
                Observer { updateRecyclerView() })
        }
    }

    /* ------------------------------ UI */

    private fun initRecyclerView() {
        mBinding.recyclerViewPodcast.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 2)
            adapter =
                podcastListAdapter.also { adapter ->
                    adapter.setOnItemClickListener(View.OnClickListener {
                        val index = it.tag as Int
                        findNavController().navigate(R.id.action_podcastListFragment_to_collectionListFragment)
                        Log.i("", "")
                    })
                }
        }
    }

    private fun updateRecyclerView() {
        (mBinding.recyclerViewPodcast.adapter as? PodcastListAdapter)
            ?.updateDataSource(viewModel = mViewModel)
    }
}