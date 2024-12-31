package com.nikhil.gaugeright.data.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

object SafeApi {

    private val TAG = "API"
    suspend fun <T> call(apiToBeCalled: suspend () -> Response<T>): Resource<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<T> = apiToBeCalled()
                Log.d(TAG, "safeApiCall: $response")
                when(response.code()) {
                    in 200..300 -> {
                        Resource.Success(data = response.body()!!)
                    }
                    in 400..499 -> {
                        val errorResponse = try {
                            JSONObject(response.errorBody()!!.charStream().readText())
                        } catch (e: Exception) {
                            null
                        }
                        Resource.Error(
                            errorMessage = errorResponse?.getString("message")
                                ?: "Something went wrong"
                        )
                    }
                    500 -> {
                        Resource.Error(errorMessage = "Internal Server Error")
                    }
                    else -> {
                        Resource.Error(errorMessage = "Unknown Error")
                    }
                }
            } catch (e: HttpException) {
                Log.d(TAG, "safeApiCall1: $e")
                Resource.Error(errorMessage = e.message ?: "Something went wrong")
            } catch (e: IOException) {
                Log.d(TAG, "safeApiCall2: $e")
                Resource.Error(errorMessage = "Please check your network connection")
            } catch (e: Exception) {
                Log.d(TAG, "safeApiCall3: $e")
                Resource.Error(errorMessage = "Something went wrong")
            }
        }
    }
}