package personal.ivan.silkrode.api

import retrofit2.http.GET

interface PodcastService {

    @GET("getcasts")
    suspend fun getPodcastList(): PodcastApiResponse<PodcastData>

    @GET("getcastdetail")
    suspend fun getCastDetail(): PodcastApiResponse<CollectionData>
}