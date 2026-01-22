package ru.fefu.task_3.ui.viewmodel

import ru.fefu.task_3.network.AnimeDto

sealed interface ListUiState {
    data object Loading : ListUiState
    data class Error(val message: String) : ListUiState
    data class Success(
        val animes: List<AnimeDto>,
        val canLoadMore: Boolean = true
    ) : ListUiState
    data object Empty : ListUiState
}

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Error(val message: String) : DetailUiState
    data class Success(val anime: AnimeDto) : DetailUiState
}