package personal.ivan.silkrode.navigation.podcast.model

import androidx.annotation.IntDef
import personal.ivan.silkrode.api.Collection
import personal.ivan.silkrode.api.ContentFeed
import personal.ivan.silkrode.util.DateFormatUtil

/* ------------------------------ Page Binding Model */

data class CollectionBindingModel(
    val coverImageUrl: String,
    val vhModelList: List<CollectionVhBindingModel>
) {

    companion object {
        const val DATE_PATTEN = "dd MMM yyyy"
    }

    constructor(
        data: Collection,
        util: DateFormatUtil
    ) : this(
        coverImageUrl = data.bigCoverImgUrl ?: "",
        vhModelList =
        mutableListOf<CollectionVhBindingModel>().apply {
            // information
            add(
                CollectionVhBindingModel.CollectionInfoVhBindingModel(
                    data = data,
                    util = util
                )
            )
            // content
            data.contentFeedList?.also { dataList ->
                addAll(dataList.map {
                    CollectionVhBindingModel.ContentVhBindingModel(
                        data = it,
                        util = util
                    )
                })
            }
        }
    )
}

/* ------------------------------ View Holder Binding Model */

sealed class CollectionVhBindingModel(@CollectionViewType val viewType: Int) {

    companion object {
        // View Type
        @IntDef(
            VIEW_TYPE_COLLECTION_INFORMATION,
            VIEW_TYPE_CONTENT_FEED
        )
        @Retention(AnnotationRetention.SOURCE)
        annotation class CollectionViewType

        const val VIEW_TYPE_COLLECTION_INFORMATION = 0
        const val VIEW_TYPE_CONTENT_FEED = 1
    }

    /**
     * Information
     */
    data class CollectionInfoVhBindingModel(
        val title: String,
        val releaseDate: String
    ) : CollectionVhBindingModel(viewType = VIEW_TYPE_COLLECTION_INFORMATION) {

        constructor(
            data: Collection,
            util: DateFormatUtil
        ) : this(
            title = data.collectionName ?: "",
            releaseDate =
            util.formatDateString(
                input = data.releaseDate ?: "",
                inputPattern = Collection.DATE_PATTERN,
                outputPattern = CollectionBindingModel.DATE_PATTEN
            )
        )
    }

    /**
     * Content
     */
    data class ContentVhBindingModel(
        val contentUrl: String,
        val title: String,
        val description: String,
        val publishDate: String
    ) : CollectionVhBindingModel(viewType = VIEW_TYPE_CONTENT_FEED) {

        companion object {
            const val DATE_PATTEN = "EEE, dd MMM yyyy"
        }

        constructor(
            data: ContentFeed,
            util: DateFormatUtil
        ) : this(
            contentUrl = data.contentUrl ?: "",
            title = data.title ?: "",
            description = data.description ?: "",
            publishDate =
            util.formatDateString(
                input = data.publishedDate ?: "",
                inputPattern = ContentFeed.DATE_PATTERN,
                outputPattern = DATE_PATTEN
            )
        )
    }
}