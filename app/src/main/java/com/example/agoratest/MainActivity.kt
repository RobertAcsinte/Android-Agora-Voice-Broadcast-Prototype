package com.example.agoratest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agoratest.ui.theme.AgoraTestTheme
import io.agora.rtc2.*
import kotlin.random.Random



// An integer that identifies the local user | random just for testing
val UID = Random.nextInt(0, 9999)


@ExperimentalUnsignedTypes
class MainActivity : ComponentActivity() {

    lateinit var sharedViewModel: MainActivityViewModel
    // Agora engine instance
    private lateinit var agoraEngine: RtcEngine

    private fun setupVoiceSDKEngine() {
        try {
            val config = RtcEngineConfig().apply {
                mContext = baseContext
                mAppId = ID_APP
                mEventHandler = mRtcEventHandler
            }
            agoraEngine = RtcEngine.create(config)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    //if you are broadcast and audience join, no event for some reason | if you are audience you get events also for broadcast | quite buggy
    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserJoined(uid: Int, elapsed: Int) {
            sharedViewModel.messages.add("Remote user joined $uid")
        }

        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            sharedViewModel.onJoinAudio()
            sharedViewModel.messages.add("Joined Channel $channel")
            sharedViewModel.messages.add("Local user $uid")
        }
        override fun onUserOffline(uid: Int, reason: Int) {
            sharedViewModel.messages.add("Remote user offline $uid $reason")
        }
        override fun onLeaveChannel(stats: RtcStats) {
            sharedViewModel.onLeaveAudio()
        }
    }

    private fun joinChannel(clientRole: Int) {
        val options = ChannelMediaOptions().apply {
            autoSubscribeAudio = true
            clientRoleType = clientRole
            channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING
        }
        agoraEngine.joinChannel(TOKEN, CHANNEL_NAME, UID, options)
    }

    private fun joinChannelBroadcast() {
        joinChannel(Constants.CLIENT_ROLE_BROADCASTER)
    }

    private fun joinChannelAudience() {
        joinChannel(Constants.CLIENT_ROLE_AUDIENCE)
    }

    private fun leaveChannel() {
        agoraEngine.leaveChannel()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupVoiceSDKEngine()

        setContent {
            sharedViewModel = viewModel()
            AgoraTestTheme() {
                Surface(
                    color = MaterialTheme.colors.background,
                    modifier = Modifier.padding(16.dp)
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "join_audio_screen"
                    ) {
                        composable(route = "join_audio_screen") {
                            JoinAudioScreen(
                                sharedViewModel = sharedViewModel,
                                joinBroadcast = ::joinChannelBroadcast,
                                joinAudience = ::joinChannelAudience,
                                onNavigate = navController::navigate
                            )
                        }
                        composable(route = "audio_room_screen") {
                            AudioRoomScreen(
                                sharedViewModel = sharedViewModel,
                                leave = ::leaveChannel,
                                onNavigate = navController::navigate
                            )
                        }
                        composable(route = "room_screen") {
                            RoomScreen(onNavigate = navController::navigate)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        agoraEngine.leaveChannel()
        RtcEngine.destroy();
    }
}