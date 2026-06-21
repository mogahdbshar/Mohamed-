package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.model.Channel
import com.example.repository.ChannelRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(private val repository: ChannelRepository) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _syncError = MutableStateFlow<String?>(null)
    val syncError: StateFlow<String?> = _syncError

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedChannel = MutableStateFlow<Channel?>(null)
    val selectedChannel: StateFlow<Channel?> = _selectedChannel

    // Read reactive streams from Room Database to allow instant zero-internet launches
    val allChannels: StateFlow<List<Channel>> = repository.allChannels
        .map { list ->
            list.sortedWith(compareByDescending<Channel> { 
                val nameUpper = it.name.uppercase()
                nameUpper.startsWith("AR") || it.name.any { char -> char in '\u0600'..'\u06FF' }
            }.thenBy { it.name })
        }
        .flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteChannels: StateFlow<List<Channel>> = repository.favoriteChannels
        .map { list ->
            list.sortedWith(compareByDescending<Channel> { 
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
        syncFromNetwork()
    }

    fun syncFromNetwork() {
        viewModelScope.launch {
            _isLoading.value = true
            _syncError.value = null
            
            val result = repository.syncChannels()
            result.onFailure { error ->
                _syncError.value = error.localizedMessage ?: "حدث خطأ غير معروف أثناء تحميل القنوات"
            }
            
            _isLoading.value = false
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

class MainViewModelFactory(private val repository: ChannelRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
