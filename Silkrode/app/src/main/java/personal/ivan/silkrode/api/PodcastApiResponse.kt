package personal.ivan.silkrode.api

import com.squareup.moshi.Json

/* ------------------------------ General API Response */

data class PodcastApiResponse<T>(val data: T?)

/* ------------------------------ Podcast List */

data class PodcastData(@field:Json(name = "podcast") val podcastList: List<Podcast>?)

data class Podcast(
    val id: String?,
    val artistName: String?,
    @field:Json(name = "artworkUrl100") val coverImgUrl: String?,
    @field:Json(name = "name") val channelName: String?
)

/* ------------------------------ Collection */

data class CollectionData(val collection: Collection?)

data class Collection(
    @field:Json(name = "artworkUrl600") val bigCoverImgUrl: String?,
    val collectionName: String?,
    @field:Json(name = "contentFeed") val contentFeedList: List<ContentFeed>?,
    val releaseDate: String?
) {
    companion object {
        const val DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    }
}

data class ContentFeed(
    val contentUrl: String?,
    val title: String?,
    @field:Json(name = "desc") val description: String?,
    val publishedDate: String?
) {
    companion object {
        const val DATE_PATTERN = "EEE, dd MMM yyyy HH:mm:ss Z"
    }
}

