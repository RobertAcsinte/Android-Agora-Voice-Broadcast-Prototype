package com.example.agoratest

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MainActivityViewModel: ViewModel() {
    val joinedState = mutableStateOf(false)
    val messages = mutableStateListOf<String>()

    val hasInternetPermission = mutableStateOf(false)
    val hasRecordAudioPermission = mutableStateOf(false)
    val hasModifyAudioPermission = mutableStateOf(false)
    val hasWifiStatePermission = mutableStateOf(false)
    val hasNetworkStatePermission = mutableStateOf(false)
    val hasReadPhoneState = mutableStateOf(false)
    val hasBluetoothPermission = mutableStateOf(false)

    fun onPermissionResult(
        acceptedInternetPermission: Boolean,
        acceptedRecordAudioPermission: Boolean,
        acceptedModifyAudioPermission: Boolean,
        acceptedWifiStatePermission: Boolean,
        acceptedNetworkStatePermission: Boolean,
        acceptedReadPhoneStatePermission: Boolean,
        acceptedBluetoothPermission: Boolean,
    ) {
        hasInternetPermission.value = acceptedInternetPermission
        hasRecordAudioPermission.value = acceptedRecordAudioPermission
        hasModifyAudioPermission.value = acceptedModifyAudioPermission
        hasWifiStatePermission.value = acceptedWifiStatePermission
        hasNetworkStatePermission.value = acceptedNetworkStatePermission
        hasReadPhoneState.value = acceptedReadPhoneStatePermission
        hasBluetoothPermission.value = acceptedBluetoothPermission
    }

    fun onJoinAudio() {
        joinedState.value = true
    }

    fun onLeaveAudio() {
        joinedState.value = false
    }
}