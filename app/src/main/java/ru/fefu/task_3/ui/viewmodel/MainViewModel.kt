package ru.fefu.task_3.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.fefu.task_3.data.AnimeRepository
import ru.fefu.task_3.network.AnimeDto

class MainViewModel(private val repository: AnimeRepository) : ViewModel() {

    private val _listState = MutableStateFlow<ListUiState>(ListUiState.Loading)
    val listState: StateFlow<ListUiState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val detailState: StateFlow<DetailUiState> = _detailState.asStateFlow()

    private val _favorites = MutableStateFlow<Set<Int>>(emptySet())
    val favorites: StateFlow<Set<Int>> = _favorites.asStateFlow()

    private val _favoritesListState = MutableStateFlow<ListUiState>(ListUiState.Loading)
    val favoritesListState: StateFlow<ListUiState> = _favoritesListState.asStateFlow()

    private val _pageNumber = MutableStateFlow(1)
    val pageNumber: StateFlow<Int> = _pageNumber.asStateFlow()

    private var currentQuery: String? = null
    private var searchJob: Job? = null

    init {
        loadAnimes()
    }

    fun loadAnimes(reset: Boolean = false) {
        if (reset) {
            _pageNumber.value = 1
        }
        
        _listState.value = ListUiState.Loading

        viewModelScope.launch {
            try {
                val search = if (currentQuery.isNullOrBlank()) null else currentQuery
                val newItems = repository.getAnimes(_pageNumber.value, search)
                
                if (newItems.isEmpty() && _pageNumber.value == 1) {
                    _listState.value = ListUiState.Empty
                } else {
                    _listState.value = ListUiState.Success(newItems, canLoadMore = newItems.isNotEmpty())
                }
            } catch (e: Exception) {
                _listState.value = ListUiState.Error(e.localizedMessage ?: "Неизвестная ошибка")
            }
        }
    }

    fun nextPage() {
        _pageNumber.value += 1
        loadAnimes()
    }

    fun prevPage() {
        if (_pageNumber.value > 1) {
            _pageNumber.value -= 1
            loadAnimes()
        }
    }

    fun onSearchQueryChanged(query: String) {
        currentQuery = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            loadAnimes(reset = true)
        }
    }

    fun loadAnimeDetail(id: Int) {
        _detailState.value = DetailUiState.Loading
        viewModelScope.launch {
            try {
                val anime = repository.getAnime(id)
                _detailState.value = DetailUiState.Success(anime)
            } catch (e: Exception) {
                _detailState.value = DetailUiState.Error(e.localizedMessage ?: "Неизвестная ошибка")
            }
        }
    }

    fun toggleFavorite(id: Int) {
        _favorites.update { current ->
            if (current.contains(id)) current - id else current + id
        }
    }

    fun loadFavorites() {
        val ids = _favorites.value
        if (ids.isEmpty()) {
            _favoritesListState.value = ListUiState.Empty
            return
        }

        viewModelScope.launch {
            _favoritesListState.value = ListUiState.Loading
            try {
                val list = repository.getAnimesByIds(ids)
                if (list.isEmpty()) {
                    _favoritesListState.value = ListUiState.Empty
                } else {
                    _favoritesListState.value = ListUiState.Success(list, canLoadMore = false)
                }
            } catch (e: Exception) {
                _favoritesListState.value = ListUiState.Error(e.localizedMessage ?: "Неизвестная ошибка")
            }
        }
    }

    class Factory(private val repository: AnimeRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
