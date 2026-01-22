package ru.fefu.task_3.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import ru.fefu.task_3.network.AnimeDto
import ru.fefu.task_3.network.ShikimoriApi

class AnimeRepository(private val api: ShikimoriApi) {

    suspend fun getAnimes(page: Int, search: String?): List<AnimeDto> {
        return api.getAnimes(page = page, search = search)
    }

    suspend fun getAnime(id: Int): AnimeDto {
        return api.getAnime(id)
    }

    suspend fun getAnimesByIds(ids: Set<Int>): List<AnimeDto> = withContext(Dispatchers.IO) {
        ids.map { id ->
            async {
                try {
                    api.getAnime(id)
                } catch (e: Exception) {
                    null
                }
            }
        }.awaitAll().filterNotNull()
    }
}
