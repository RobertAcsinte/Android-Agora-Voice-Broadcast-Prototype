package com.example.agoratest

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext



@Composable
fun JoinAudioScreen(
    joinBroadcast: () -> Unit,
    joinAudience: () -> Unit,
    openAppPermissions: () -> Unit,
    sharedViewModel: MainActivityViewModel,
    onNavigate: (String) -> Unit,
) {
    val permissionState by sharedViewModel.permissionState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {
            sharedViewModel.onPermissionResult(

                acceptedInternetPermission = it[Manifest.permission.INTERNET] == true,
                acceptedRecordAudioPermission = it[Manifest.permission.RECORD_AUDIO] == true,
                acceptedModifyAudioPermission = it[Manifest.permission.MODIFY_AUDIO_SETTINGS] == true,
                acceptedWifiStatePermission = it[Manifest.permission.ACCESS_WIFI_STATE] == true,
                acceptedNetworkStatePermission = it[Manifest.permission.ACCESS_NETWORK_STATE] == true,
                acceptedReadPhoneStatePermission = it[Manifest.permission.READ_PHONE_STATE] == true,
                acceptedBluetoothPermission = it[Manifest.permission.BLUETOOTH] == true,
            )
        }
    )

    LaunchedEffect(key1 = true) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.INTERNET,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.BLUETOOTH,
            )
        )
    }

    if(permissionState.hasInternetPermission && permissionState.hasRecordAudioPermission && permissionState.hasModifyAudioPermission
        && permissionState.hasWifiStatePermission && permissionState.hasNetworkStatePermission
        && permissionState.hasReadPhoneStatePermission && permissionState.hasBluetoothPermission) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                joinBroadcast()
                onNavigate("audio_room_screen")
            }) {
                Text(text = "Join Broadcast")
            }
            Button(onClick = {
                joinAudience()
                onNavigate("audio_room_screen")
            }) {
                Text(text = "Join Audience")
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Please accept all permissions, restart the app and try again.")
            Button(onClick = {
                openAppPermissions()
            }) {
                Text(text = "Open App Permissions")
            }
        }
    }


}

