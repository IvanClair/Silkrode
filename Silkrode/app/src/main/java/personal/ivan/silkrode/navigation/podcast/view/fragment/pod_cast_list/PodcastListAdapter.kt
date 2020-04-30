package personal.ivan.silkrode.navigation.podcast.view.fragment.pod_cast_list

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import personal.ivan.silkrode.api.Podcast
import personal.ivan.silkrode.databinding.VhPodcastBinding
import personal.ivan.silkrode.navigation.podcast.viewmodel.PodcastViewModel
import personal.ivan.silkrode.util.GlideUtil
import javax.inject.Inject

class PodcastListAdapter @Inject constructor(private val mUtil: GlideUtil) :
    RecyclerView.Adapter<PodcastListAdapter.ViewHolder>() {

    // Data Source
    // todo easy to extend, e.g. sort by artist, topic ... etc
    private val mDataList = mutableListOf<Podcast>()

    // Listener
    private var mListener: OnPodcastItemClickListener? = null

    /*
        Prevent fast double click
     */
    companion object {
        private var lastClickTime = 0L
        fun allowClick(): Boolean {
            val now = System.currentTimeMillis()
            if (now > lastClickTime + 500) {
                lastClickTime = now
                return true
            }
            return false
        }
    }

    /* ------------------------------ Interface */

    interface OnPodcastItemClickListener {
        fun onClick(imageView: ImageView, id: String)
    }

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

    override fun getItemCount(): Int = mDataList.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        mDataList
            .getOrNull(position)
            ?.also {
                holder.bind(
                    data = it,
                    glideUtil = mUtil,
                    listener = mListener
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
                mDataList.apply {
                    clear()
                    addAll(it)
                }
                notifyDataSetChanged()
            }
    }

    /**
     * Item click listener
     */
    fun setOnItemClickListener(listener: OnPodcastItemClickListener?) {
        this.mListener = listener
    }

    /* ------------------------------ View Holder */

    class ViewHolder(private val mBinding: VhPodcastBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bind(
            data: Podcast,
            glideUtil: GlideUtil,
            listener: OnPodcastItemClickListener?
        ) {
            mBinding.apply {
                root.setOnClickListener {
                    if (allowClick()) {
                        listener?.onClick(
                            imageView = imageViewCover,
                            id = data.id
                        )
                    }
                }
                imageViewCover.apply {
                    transitionName = data.id
                    glideUtil.loadPodcastCover(imageView = this, url = data.coverImgUrl)
                }
                textViewArtistName.text = data.artistName
                textViewPodcastName.text = data.channelName
            }
        }
    }
}