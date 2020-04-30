package personal.ivan.silkrode.navigation.podcast.view.fragment.collection_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import personal.ivan.silkrode.databinding.VhCollectionContentBinding
import personal.ivan.silkrode.databinding.VhCollectionInformationBinding
import personal.ivan.silkrode.navigation.podcast.model.CollectionVhBindingModel
import personal.ivan.silkrode.navigation.podcast.viewmodel.PodcastViewModel

class CollectionListAdapter : RecyclerView.Adapter<CollectionListAdapter.CollectionViewHolder>() {

    // Data Source
    // todo easy to extend, e.g. sort by season, topic ... etc
    private var mDataList: MutableList<CollectionVhBindingModel> = mutableListOf()

    // Listener
    private var mListener: View.OnClickListener? = null

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

    /* ------------------------------ Override */

    override fun getItemViewType(position: Int): Int =
        mDataList.getOrNull(position)
            ?.viewType
            ?: CollectionVhBindingModel.VIEW_TYPE_COLLECTION_INFORMATION

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CollectionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            // content
            CollectionVhBindingModel.VIEW_TYPE_CONTENT_FEED ->
                CollectionViewHolder.ContentViewHolder(
                    mBinding = VhCollectionContentBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                )

            // information
            else ->
                CollectionViewHolder.CollectionInfoViewHolder(
                    mBinding = VhCollectionInformationBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                )
        }
    }

    override fun getItemCount(): Int = mDataList.size

    override fun onBindViewHolder(
        holder: CollectionViewHolder,
        position: Int
    ) {
        mDataList
            .getOrNull(position)
            ?.also {
                when (holder) {
                    is CollectionViewHolder.CollectionInfoViewHolder -> holder.bind(data = it)
                    is CollectionViewHolder.ContentViewHolder -> holder.bind(
                        data = it,
                        index = position,
                        listener = mListener
                    )
                }
            }
    }

    /**
     * Update data source from [PodcastViewModel]
     */
    fun updateDataSource(viewModel: PodcastViewModel) {
        viewModel
            .getContentFeedList()
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
    fun setOnItemClickListener(listener: View.OnClickListener?) {
        this.mListener = listener
    }

    /* ------------------------------ View Holder */

    sealed class CollectionViewHolder(binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Information View Holder
         */
        class CollectionInfoViewHolder(private val mBinding: VhCollectionInformationBinding) :
            CollectionViewHolder(mBinding) {

            fun bind(data: CollectionVhBindingModel) {
                if (data is CollectionVhBindingModel.CollectionInfoVhBindingModel) {
                    mBinding.apply {
                        textViewCollectionName.text = data.title
                        textViewReleaseDate.text = data.releaseDate
                    }
                }
            }
        }

        /**
         * Content View Holder
         */
        class ContentViewHolder(private val mBinding: VhCollectionContentBinding) :
            CollectionViewHolder(mBinding) {

            fun bind(
                data: CollectionVhBindingModel,
                index: Int,
                listener: View.OnClickListener?
            ) {
                if (data is CollectionVhBindingModel.ContentVhBindingModel) {
                    mBinding.apply {
                        root.apply {
                            tag = index
                            setOnClickListener(listener)
                        }
                        textViewContentTitle.text = data.title
                        textViewContentPublishDate.text = data.publishDate
                    }
                }
            }
        }
    }
}