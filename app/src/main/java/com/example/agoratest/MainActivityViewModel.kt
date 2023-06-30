package com.example.agoratest

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.agoratest.states.PermissionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainActivityViewModel: ViewModel() {

    val messages = mutableStateListOf<String>()

    private val _permissionsState = MutableStateFlow(PermissionState())
    val permissionState = _permissionsState.asStateFlow()

    fun onPermissionResult(
        acceptedInternetPermission: Boolean,
        acceptedRecordAudioPermission: Boolean,
        acceptedModifyAudioPermission: Boolean,
        acceptedWifiStatePermission: Boolean,
        acceptedNetworkStatePermission: Boolean,
        acceptedReadPhoneStatePermission: Boolean,
        acceptedBluetoothPermission: Boolean,
    ) {
        _permissionsState.value = PermissionState(
            hasInternetPermission = acceptedInternetPermission,
            hasRecordAudioPermission = acceptedRecordAudioPermission,
            hasModifyAudioPermission = acceptedModifyAudioPermission,
            hasWifiStatePermission = acceptedWifiStatePermission,
            hasNetworkStatePermission = acceptedNetworkStatePermission,
            hasReadPhoneStatePermission = acceptedReadPhoneStatePermission,
            hasBluetoothPermission = acceptedBluetoothPermission,
        )
    }

}