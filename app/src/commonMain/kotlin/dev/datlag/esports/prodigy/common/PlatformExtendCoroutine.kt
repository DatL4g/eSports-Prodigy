package dev.datlag.esports.prodigy.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher

expect val Dispatchers.DeviceMain: MainCoroutineDispatcher
expect val Dispatchers.DeviceIO: CoroutineDispatcher