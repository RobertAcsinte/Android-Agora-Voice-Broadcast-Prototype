package com.example.agoratest

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RoomScreen(
    onNavigate: (String) -> Unit,
    viewModel: RoomViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.onJoinEvent.collect {
            onNavigate("video_screen/$it")
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.End
    ) {
        TextField(
            value = viewModel.roomName.value.text,
            onValueChange = viewModel::onRoomEnter,
            isError = viewModel.roomName.value.error != null,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Enter room name")
            }
        )
        viewModel.roomName.value.error?.let { error ->
            Text(text = error, color = MaterialTheme.colors.error)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = viewModel::onJoinRoom) {
            Text(text = "Join")
        }
    }
}