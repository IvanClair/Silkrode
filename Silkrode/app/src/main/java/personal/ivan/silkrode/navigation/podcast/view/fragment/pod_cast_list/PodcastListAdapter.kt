package personal.ivan.silkrode.navigation.podcast.view.fragment.pod_cast_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import personal.ivan.silkrode.api.Podcast
import personal.ivan.silkrode.databinding.VhPodcastBinding
import personal.ivan.silkrode.navigation.podcast.viewmodel.PodcastViewModel
import personal.ivan.silkrode.navigation.podcast.viewmodel.getPodcastList

class PodcastListAdapter(private val mViewModel: PodcastViewModel) :
    RecyclerView.Adapter<PodcastListAdapter.ViewHolder>() {

    /* ------------------------------ Override */

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) =
        ViewHolder(
            mBinding =
            VhPodcastBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int =
        mViewModel
            .getPodcastList()
            ?.size
            ?: 0

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        mViewModel
            .getPodcastList()
            ?.getOrNull(position)
            ?.also { holder.bind(data = it) }
    }

    /* ------------------------------ View Holder */

    class ViewHolder(private val mBinding: VhPodcastBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bind(data: Podcast) {
            mBinding.apply {
                // todo
                imageViewCover
                textViewArtistName.text = data.artistName
                textViewPodcastName.text = data.channelName
            }
        }
    }
}