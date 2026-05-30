package com.quickmindgames.sudoku.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class MultipleEventsCutter private constructor() {
    private var lastClickTime = 0L

    fun processEvent(delayMillis: Long = 500L, event: () -> Unit) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime >= delayMillis) {
            lastClickTime = currentTime
            event()
        }
    }

    companion object {
        @Composable
        fun rememberMultipleEventsCutter(): MultipleEventsCutter {
            return remember { MultipleEventsCutter() }
        }
    }
}