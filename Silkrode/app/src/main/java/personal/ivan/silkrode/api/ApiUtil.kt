package personal.ivan.silkrode.api

import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class ApiUtil<T, R> {

    // result live data
    private val resultLiveData =
        liveData<ApiStatus<R>> {

            // start with loading status
            emit(ApiStatus.loading())

            // TODO Retrofit throws exception if network is unavailable,
            //     maybe they will update in future, keep an eye on new release
            try {

                // get API response
                val apiRs = getApiResponse()

                // convert API response to actual needed model in background
                withContext(Dispatchers.IO) {
                    val convertedRs = convertResponse(apiRs = apiRs)
                    if (convertedRs != null) emit(ApiStatus.success(data = convertedRs))
                    else emit(ApiStatus.fail())
                }
            } catch (e: Exception) {

                // set fail when exception happened
                emit(ApiStatus.fail())
            }
        }

    /* ------------------------------ Public Functions */

    fun getLiveData() = resultLiveData

    /* ------------------------------ Override Functions */

    protected abstract suspend fun getApiResponse(): T

    protected abstract suspend fun convertResponse(apiRs: T): R?
}