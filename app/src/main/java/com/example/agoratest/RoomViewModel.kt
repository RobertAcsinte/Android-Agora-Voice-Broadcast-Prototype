package com.example.agoratest

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class RoomViewModel: ViewModel() {
    private val _roomName = mutableStateOf<TextFieldState>(TextFieldState())
    val roomName: State<TextFieldState> = _roomName

    private val _onJoinEvent = MutableSharedFlow<String>()
    val onJoinEvent = _onJoinEvent.asSharedFlow()

    fun onRoomEnter(name: String) {
        _roomName.value = _roomName.value.copy(text = name)
    }

    fun onJoinRoom() {
        if(roomName.value.text.isBlank()) {
            _roomName.value = _roomName.value.copy(error = "Please enter a room name")
            return
        }
        viewModelScope.launch {
            _onJoinEvent.emit(roomName.value.text)
        }
    }
}