package com.nikhil.gaugeright.data.remote

import com.nikhil.gaugeright.data.model.Post
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    // https://jsonplaceholder.typicode.com/posts
    @GET("/posts")
    suspend fun uploadReadings(): Response<List<Post>>
}