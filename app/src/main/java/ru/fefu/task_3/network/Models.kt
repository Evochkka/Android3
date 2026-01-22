package ru.fefu.task_3.network

import com.google.gson.annotations.SerializedName
import java.util.Locale

data class AnimeDto(
    val id: Int,
    val name: String,
    val russian: String?,
    val image: AnimeImageDto?,
    val score: String?,
    val kind: String?,
    val status: String?,
    val episodes: Int?,
    val description: String?,
    @SerializedName("aired_on") val airedOn: String?,
    val genres: List<GenreDto>?
) {
    val russianKind: String
        get() = when (kind) {
            "tv" -> "ТВ Сериал"
            "movie" -> "Фильм"
            "ova" -> "OVA"
            "ona" -> "ONA"
            "special" -> "Спешл"
            "music" -> "Клип"
            "tv_13" -> "ТВ (13 эп.)"
            "tv_24" -> "ТВ (24 эп.)"
            "tv_48" -> "ТВ (48 эп.)"
            else -> kind ?: ""
        }

    val russianStatus: String
        get() = when (status) {
            "released" -> "Вышло"
            "ongoing" -> "Онгоинг"
            "anons", "announced" -> "Анонс"
            else -> status ?: ""
        }

    val releaseYear: String
        get() = airedOn?.take(4) ?: ""

    val joinedGenres: String
        get() = genres?.joinToString(", ") { it.russian ?: it.name } ?: ""
}

data class AnimeImageDto(
    val original: String?,
    val preview: String?,
    val x96: String?,
    val x48: String?
)

data class GenreDto(
    val id: Int,
    val name: String,
    val russian: String?
)
