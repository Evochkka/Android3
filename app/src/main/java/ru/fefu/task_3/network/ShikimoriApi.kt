package ru.fefu.task_3.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ShikimoriApi {

    @GET("api/animes")
    suspend fun getAnimes(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 10,
        @Query("order") order: String = "popularity",
        @Query("search") search: String? = null
    ): List<AnimeDto>

    @GET("api/animes/{id}")
    suspend fun getAnime(
        @Path("id") id: Int
    ): AnimeDto
}
