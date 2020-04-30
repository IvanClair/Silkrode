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

                // load from database
                val fromDb = loadFromDb()
                if (fromDb != null) {
                    emit(ApiStatus.success(data = fromDb))
                }

                // get API response
                val apiRs = loadFromNetwork()

                // convert API response to actual needed model in background
                withContext(Dispatchers.IO) {
                    val convertedRs = convertResponse(apiRs = apiRs)
                    when {
                        // load from network succeed
                        convertedRs != null -> {
                            saveToDb(convertedRs)
                            emit(ApiStatus.success(data = convertedRs))
                        }

                        // load from network failed, and did not save in database
                        fromDb == null -> emit(ApiStatus.fail())
                    }
                }
            } catch (e: Exception) {

                // set fail when exception happened
                emit(ApiStatus.fail())
            }
        }

    /* ------------------------------ Public Functions */

    fun getLiveData() = resultLiveData

    /* ------------------------------ Override Functions */

    protected abstract suspend fun loadFromDb(): R?

    protected abstract suspend fun loadFromNetwork(): T

    protected abstract suspend fun convertResponse(apiRs: T): R?

    protected abstract suspend fun saveToDb(data: R)
}