package dev.datlag.esports.prodigy.network

import com.hadiyarajesh.flower_core.Resource

sealed class Status {

    object LOADING : Status()

    sealed class ERROR : Status() {
        object TOO_MANY_REQUESTS : ERROR()

        object BAD_REQUEST : ERROR()

        object INTERNAL : ERROR()

        companion object {
            fun create(statusCode: Int): ERROR {
                return when (statusCode) {
                    429 -> TOO_MANY_REQUESTS
                    400 -> BAD_REQUEST
                    else -> INTERNAL
                }
            }
        }
    }

    object SUCCESS : Status()

    companion object {
        fun create(status: Resource.Status<*>, emptySuccessAsError: Boolean = false): Status {
            return when (status) {
                is Resource.Status.Loading -> LOADING
                is Resource.Status.Error -> ERROR.create(status.statusCode)
                is Resource.Status.Success -> SUCCESS
                is Resource.Status.EmptySuccess -> if (emptySuccessAsError) ERROR.create(204) else SUCCESS
            }
        }
    }
}