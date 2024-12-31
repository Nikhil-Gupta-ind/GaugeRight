package com.nikhil.gaugeright.util

// Name of Notification Channel for verbose notifications of background work
val VERBOSE_NOTIFICATION_CHANNEL_NAME: CharSequence =
    "Verbose WorkManager Notifications"
const val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION =
    "Shows notifications whenever work starts"
val NOTIFICATION_TITLE: CharSequence = "Synchronizing readings"
const val CHANNEL_ID = "VERBOSE_NOTIFICATION"
const val NOTIFICATION_ID = 1

const val KEY_OUTPUT_URI = "KEY_OUTPUT_URI"
const val TAG_OUTPUT = "OUTPUT"

const val DELAY_TIME_MILLIS: Long = 3000