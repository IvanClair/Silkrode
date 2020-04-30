package personal.ivan.silkrode.api

import androidx.lifecycle.liveData

/**
 * @param S source from network or database
 * @param R result of output
 */
abstract class ApiUtil<S, R> {

    // Result LiveData
    private val resultLiveData =
        liveData<ApiStatus<R>> {

            // start with loading status
            emit(ApiStatus.loading())

            // TODO Retrofit throws exception if network is unavailable,
            //     maybe they will update in future, keep an eye on new release
            try {

                // load from database
                val dataFromDb = loadFromDb()
                if (dataFromDb != null) {
                    convertFromSource(source = dataFromDb)
                        ?.also { emit(ApiStatus.success(data = it)) }
                }

                // get from network
                val dataFromNetwork = loadFromNetwork()

                // convert API response to actual needed model
                val convertedData = convertFromSource(source = dataFromNetwork)
                when {
                    // load from network succeed
                    convertedData != null -> {
                        dataFromNetwork?.also { saveToDb(data = it) }
                        emit(ApiStatus.success(data = convertedData))
                    }

                    // load from network failed, and did not save in database before
                    dataFromDb == null -> emit(ApiStatus.fail())
                }

            } catch (e: Exception) {
                // set fail when exception happened
                emit(ApiStatus.fail())
            }
        }

    /* ------------------------------ Public Functions */

    fun getLiveData() = resultLiveData

    /* ------------------------------ Override Functions */

    protected abstract suspend fun loadFromDb(): S?

    protected abstract suspend fun loadFromNetwork(): S?

    protected abstract suspend fun convertFromSource(source: S?): R?

    protected abstract suspend fun saveToDb(data: S)
}