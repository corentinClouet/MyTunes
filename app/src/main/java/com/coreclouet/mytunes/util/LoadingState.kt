package com.coreclouet.mytunes.util

data class LoadingState constructor(val status: Status, val msg: String? = null) {
    companion object {
        val LOADED =
            LoadingState(Status.SUCCESS)
        val LOADING =
            LoadingState(Status.LOADING)
        fun error(msg: String?) = LoadingState(
            Status.ERROR,
            msg
        )
    }

    enum class Status {
        LOADING,
        SUCCESS,
        ERROR
    }
}