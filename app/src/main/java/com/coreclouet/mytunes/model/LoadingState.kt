package com.coreclouet.mytunes.model

data class LoadingState private constructor(val status: Status, val msg: String? = null) {
    companion object {
        val LOADED = LoadingState(Status.SUCCESS)
        val LOADING = LoadingState(Status.LOADING)
        fun error(msg: String?) = LoadingState(Status.ERROR, msg)
    }

    enum class Status {
        LOADING,
        SUCCESS,
        ERROR
    }
}