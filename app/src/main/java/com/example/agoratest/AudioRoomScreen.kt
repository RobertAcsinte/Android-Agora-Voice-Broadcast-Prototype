package com.example.agoratest

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AudioRoomScreen(
    leave: () -> Unit,
    onNavigate: (String) -> Unit,
    sharedViewModel: MainActivityViewModel,
) {
    Column {
        LazyColumn {
            items(sharedViewModel.messages) { item ->
                Text(text = item)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            leave()
            sharedViewModel.onLeaveAudio()
            onNavigate("join_audio_screen")
        }) {
            Text(text = "Leave")
        }
    }
}