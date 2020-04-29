package personal.ivan.silkrode.api

import retrofit2.http.GET

interface PodcastApiService {

    @GET("getcasts")
    suspend fun getPodcastList(): PodcastApiResponse<PodcastData>

    @GET("getcastdetail")
    suspend fun getCollection(): PodcastApiResponse<CollectionData>
}