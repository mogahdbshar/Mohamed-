package com.dstwrtv.app.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dstwrtv.app.model.Channel
import com.dstwrtv.app.repository.ChannelRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface SyncUiState {
    object Idle : SyncUiState
    object Loading : SyncUiState
    data class Success(val channelsCount: Int) : SyncUiState
    data class Error(val message: String) : SyncUiState
}

class MainViewModel(private val app: Application, private val repository: ChannelRepository) : ViewModel() {

    private val _syncState = MutableStateFlow<SyncUiState>(SyncUiState.Idle)
    val syncState: StateFlow<SyncUiState> = _syncState

    // Keep backwards compatibility for Composable UI screens reading isLoading & syncError
    val isLoading: StateFlow<Boolean> = _syncState
        .map { it is SyncUiState.Loading }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val syncError: StateFlow<String?> = _syncState
        .map { (it as? SyncUiState.Error)?.message }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedChannel = MutableStateFlow<Channel?>(null)
    val selectedChannel: StateFlow<Channel?> = _selectedChannel

    // Read reactive streams from Room Database to allow instant zero-internet launches
    val allChannels: StateFlow<List<Channel>> = repository.allChannels
        .map { list ->
            list.sortedWith(compareBy<Channel> { it.category }.thenByDescending {
                val nameUpper = it.name.uppercase()
                nameUpper.startsWith("AR") || it.name.any { char -> char in '\u0600'..'\u06FF' }
            }.thenBy { it.name })
        }
        .flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteChannels: StateFlow<List<Channel>> = repository.favoriteChannels
        .map { list ->
            list.sortedWith(compareBy<Channel> { it.category }.thenByDescending {
                val nameUpper = it.name.uppercase()
                nameUpper.startsWith("AR") || it.name.any { char -> char in '\u0600'..'\u06FF' }
            }.thenBy { it.name })
        }
        .flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Live search and filter stream
    val filteredChannels: StateFlow<List<Channel>> = combine(allChannels, _searchQuery) { channelsList, query ->
        if (query.isBlank()) {
            channelsList
        } else {
            channelsList.filter {
                it.name.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true)
            }
        }
    }.flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            val count = repository.getChannelsCount()
            if (count == 0) {
                syncFromNetwork()
            }
        }
    }

    fun syncFromNetwork(customUrl: String? = null, bypassCache: Boolean = false, onResult: ((Result<Int>) -> Unit)? = null) {
        viewModelScope.launch {
            _syncState.value = SyncUiState.Loading
            
            val result = repository.syncChannels(customUrl, bypassCache = bypassCache || (customUrl != null))
            result.onSuccess { loadedCount ->
                _syncState.value = SyncUiState.Success(loadedCount)
                onResult?.invoke(Result.success(loadedCount))
            }
            result.onFailure { error ->
                val errMsg = error.localizedMessage ?: "حدث خطأ غير معروف أثناء تحميل القنوات"
                _syncState.value = SyncUiState.Error(errMsg)
                onResult?.invoke(Result.failure(Exception(errMsg)))
            }
        }
    }

    fun toggleFavorite(channel: Channel) {
        viewModelScope.launch {
            repository.toggleFavorite(channel.url, !channel.isFavorite)
            
            // Sync selected channel state if current is modified
            val currentSelected = _selectedChannel.value
            if (currentSelected?.url == channel.url) {
                _selectedChannel.value = channel.copy(isFavorite = !channel.isFavorite)
            }
        }
    }

    fun selectChannel(channel: Channel?) {
        _selectedChannel.value = channel
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}

class MainViewModelFactory(private val app: Application, private val repository: ChannelRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(app, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
