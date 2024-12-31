package com.nikhil.gaugeright.data.remote

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(errorMessage: String?, data: T? = null) : Resource<T>(data, errorMessage)
//    class Loading<T> : Resource<T>()
}