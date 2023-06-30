package com.example.agoratest

fun audioSourceMessage(code: Int): String {
    return when (code) {
        -1 -> "Audio route default."
        0 -> "Audio route headset with microphone."
        1 -> "Audio route earpiece."
        2 -> "Audio route headset without microphone."
        3 -> "Audio route speaker of the device."
        4 -> "Audio route external speaker."
        5 -> "Audio route bluetooth."
        else -> "Unknown audio source."
    }
}