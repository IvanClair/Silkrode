package personal.ivan.silkrode.navigation.podcast.view.fragment.pod_cast_list

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import personal.ivan.silkrode.io.model.Podcast
import personal.ivan.silkrode.databinding.VhPodcastBinding
import personal.ivan.silkrode.di.GlideApp

class PodcastListAdapter : ListAdapter<Podcast, PodcastListAdapter.ViewHolder>(DiffCallback()) {

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

    /**
     * Item click listener
     */
    fun setOnItemClickListener(listener: OnPodcastItemClickListener?) {
        this.mListener = listener
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

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(
            data = getItem(position),
            listener = mListener
        )
    }

    /* ------------------------------ View Holder */

    class ViewHolder(private val mBinding: VhPodcastBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bind(
            data: Podcast,
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
                    GlideApp
                        .with(this)
                        .load(data.coverImgUrl)
                        .into(this)
                }
                textViewArtistName.text = data.artistName
                textViewPodcastName.text = data.channelName
            }
        }
    }

    /* ------------------------------ Diff */

    class DiffCallback : DiffUtil.ItemCallback<Podcast>() {
        override fun areItemsTheSame(
            oldItem: Podcast,
            newItem: Podcast
        ) = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: Podcast,
            newItem: Podcast
        ) = oldItem.hashCode() == newItem.hashCode()
    }
}