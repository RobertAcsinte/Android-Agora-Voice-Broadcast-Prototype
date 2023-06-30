package com.example.agoratest.states

data class PermissionState(
    val hasInternetPermission: Boolean = false,
    val hasRecordAudioPermission: Boolean = false,
    val hasModifyAudioPermission: Boolean = false,
    val hasWifiStatePermission: Boolean = false,
    val hasNetworkStatePermission: Boolean = false,
    val hasReadPhoneStatePermission: Boolean = false,
    val hasBluetoothPermission: Boolean = false,
)
