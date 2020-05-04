package personal.ivan.silkrode.io.model

import androidx.annotation.StringDef

data class ApiStatus<out T>(
    @Status val status: String,
    val data: T?
) {

    companion object {

        // Status
        @StringDef(
            SUCCESS,
            FAIL,
            LOADING
        )
        @Retention(AnnotationRetention.SOURCE)
        annotation class Status

        const val SUCCESS = "SUCCESS"
        const val FAIL = "FAIL"
        const val LOADING = "LOADING"

        /**
         * Create success status
         */
        fun <T> success(data: T) = ApiStatus(
            status = SUCCESS,
            data = data
        )

        /**
         * Create fail status
         */
        fun fail() = ApiStatus(
            status = FAIL,
            data = null
        )

        /**
         * Create loading status
         */
        fun loading() = ApiStatus(
            status = LOADING,
            data = null
        )
    }
}