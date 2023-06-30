package com.example.agoratest

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
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
import io.agora.rtc2.internal.LastmileProbeConfig
import kotlin.properties.Delegates
import kotlin.random.Random



// An integer that identifies the local user | random just for testing
val UID = Random.nextInt(0, 9999)


@ExperimentalUnsignedTypes
class MainActivity : ComponentActivity() {

    lateinit var sharedViewModel: MainActivityViewModel
    // Agora engine instance
    private lateinit var agoraEngine: RtcEngine
    private var audioCode by Delegates.notNull<Int>()

    private fun setupVoiceSDKEngine() {
        try {
            val config = RtcEngineConfig().apply {
                mContext = baseContext
                mAppId = BuildConfig.ID_APP
                mEventHandler = mRtcEventHandler
            }
            agoraEngine = RtcEngine.create(config)
//            agoraEngine.setEnableSpeakerphone(true)
            agoraEngine.setDefaultAudioRoutetoSpeakerphone(true)
            //need this for the callback about ping and quality
            //uplink and downLink should be values between 100000 and 5000000, i dont really understand this
            agoraEngine.startLastmileProbeTest(LastmileProbeConfig().apply {
                probeUplink = true
                probeDownlink = true
                expectedUplinkBitrate = 100000
                expectedDownlinkBitrate = 100000
            })
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }


    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        //only shows if a broadcaster joined
        //or only shows broadcasters already joined
        //it completely ignores audience joined or that is joining
        override fun onUserJoined(uid: Int, elapsed: Int) {
            sharedViewModel.messages.add("Broadcaster id in the channel: $uid")
            sharedViewModel.messages.add("Elapsed time to connect to broadcaster id: $elapsed ms")
            sharedViewModel.messages.add("------------------")
        }

        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            sharedViewModel.messages.add("Channel: $channel")
            sharedViewModel.messages.add("Your user id: $uid")
            sharedViewModel.messages.add("Elapsed time to connect to channel: $elapsed ms")
            sharedViewModel.messages.add("------------------")
        }

        //only shows if a broadcaster left
        //it completely ignores audience that is leaving
        override fun onUserOffline(uid: Int, reason: Int) {
            val reasonCode = if (reason == 0) "it went offline" else "because they lost connection"
            sharedViewModel.messages.add("Broadcaster id $uid left because $reasonCode")
            sharedViewModel.messages.add("------------------")
        }
        override fun onLeaveChannel(stats: RtcStats) {
            Log.d("ChannelInfo", "Connect time: " + stats.connectTimeMs.toString())
        }

        //only shows to the  broadcaster, not to audience, once joined channel
        //on every 2-5 secs
        override fun onLocalAudioStats(stats: LocalAudioStats?) {
            Log.d("ChannelInfo", "Sent Bitrate: " + stats?.sentBitrate)
            Log.d("ChannelInfo", "Sent Sample Rate: " + stats?.sentSampleRate)
            Log.d("ChannelInfo", "Audio Device Delay: " + stats?.audioDeviceDelay)
            Log.d("ChannelInfo", "Audio Playout Delay: " + stats?.audioPlayoutDelay)
        }

        //rtt is ping in ms, gets called after ~10 secs
        override fun onLastmileProbeResult(result: LastmileProbeResult?) {
            Log.d("ChannelInfo", "Ping: " + result?.rtt.toString())
        }

        //quality 0-unknown, 1-excellent, 2-good, 3-poor, 4-bad, 5-very bad, 6-down
        //gets called after ~10 secs
        override fun onLastmileQuality(quality: Int) {
            Log.d("ChannelInfo", "Quality: $quality")
        }

        override fun onAudioRouteChanged(routing: Int) {
            audioCode = routing
            sharedViewModel.messages.add(audioSourceMessage(routing))
            sharedViewModel.messages.add("------------------")
        }
    }

    private fun joinChannel(clientRole: Int) {
        val options = ChannelMediaOptions().apply {
            autoSubscribeAudio = true
            clientRoleType = clientRole
            channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING
        }
        agoraEngine.joinChannel(BuildConfig.TOKEN, BuildConfig.CHANNEL_NAME, UID, options)
    }

    private fun joinChannelBroadcast() {
        joinChannel(Constants.CLIENT_ROLE_BROADCASTER)
    }

    private fun joinChannelAudience() {
        joinChannel(Constants.CLIENT_ROLE_AUDIENCE)
    }

    private fun leaveChannel() {
        sharedViewModel.messages.clear()
        agoraEngine.leaveChannel()
    }

    private fun enableDisableSpeaker() {
        if(audioCode == 3) {
            agoraEngine.setEnableSpeakerphone(false)
        } else {
            agoraEngine.setEnableSpeakerphone(true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupVoiceSDKEngine()

        setContent {
            sharedViewModel = viewModel()
            AgoraTestTheme() {
                Surface(
                    color = MaterialTheme.colors.background,
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
                                onNavigate = navController::navigate,
                                openAppPermissions = ::openAppSystemSettings
                            )
                        }
                        composable(route = "audio_room_screen") {
                            AudioRoomScreen(
                                sharedViewModel = sharedViewModel,
                                leave = ::leaveChannel,
                                onNavigate = navController::navigate,
                                enableDisableSpeaker = ::enableDisableSpeaker
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

    private fun openAppSystemSettings() {
        startActivity(Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", packageName, null)
        })
    }
}