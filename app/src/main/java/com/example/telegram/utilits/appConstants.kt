package com.example.telegram.utilits

import com.example.telegram.MainActivity

lateinit var APP_ACTIVITY:MainActivity

const val TYPE_MESSAGE_IMAGE="image"
const val TYPE_MESSAGE_TEXT="text"
const val TYPE_MESSAGE_VOICE="voice"
const val TYPE_MESSAGE_FILE="file"
const val TYPE_MESSAGE_VIDEO="video"

const val PICK_FILE_REQUEST_CODE=301
const val PICK_VIDEO_REQUEST_CODE=302

const val TYPE_CHAT="chat"
const val TYPE_GROUP="group"
const val TYPE_CHANNEL="channel"