package personal.ivan.silkrode.navigation.podcast.view.fragment.pod_cast_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import personal.ivan.silkrode.api.Podcast
import personal.ivan.silkrode.databinding.VhPodcastBinding
import personal.ivan.silkrode.navigation.podcast.viewmodel.PodcastViewModel
import personal.ivan.silkrode.navigation.podcast.viewmodel.getPodcastList
import personal.ivan.silkrode.util.GlideUtil
import javax.inject.Inject

class PodcastListAdapter @Inject constructor(private val mUtil: GlideUtil) :
    RecyclerView.Adapter<PodcastListAdapter.ViewHolder>() {

    // Data Source
    private val mDataSource = mutableListOf<Podcast>()

    // Listener
    private var listener: View.OnClickListener? = null

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

    override fun getItemCount(): Int = mDataSource.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        mDataSource
            .getOrNull(position)
            ?.also {
                holder.bind(
                    data = it,
                    glideUtil = mUtil,
                    index = position,
                    listener = listener
                )
            }
    }

    /**
     * Update data source from [PodcastViewModel]
     */
    fun updateDataSource(viewModel: PodcastViewModel) {
        viewModel
            .getPodcastList()
            ?.also {
                mDataSource.apply {
                    clear()
                    addAll(it)
                }
                notifyDataSetChanged()
            }
    }

    /**
     * Item click listener
     */
    fun setOnItemClickListener(listener: View.OnClickListener) {
        this.listener = listener
    }

    /* ------------------------------ View Holder */

    class ViewHolder(private val mBinding: VhPodcastBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bind(
            data: Podcast,
            glideUtil: GlideUtil,
            index: Int,
            listener: View.OnClickListener?
        ) {
            mBinding.apply {
                root.apply {
                    tag = index
                    setOnClickListener(listener)
                }
                glideUtil.loadPodcastCover(imageView = imageViewCover, url = data.coverImgUrl)
                textViewArtistName.text = data.artistName
                textViewPodcastName.text = data.channelName
            }
        }
    }
}