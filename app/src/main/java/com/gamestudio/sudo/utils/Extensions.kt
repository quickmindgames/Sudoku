package com.gamestudio.sudo.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

fun Modifier.clickableSingle(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: androidx.compose.ui.semantics.Role? = null,
    delayMillis: Long = 500L, // Minimum time between clicks
    onClick: () -> Unit
): Modifier = composed {
    val lastClickTime = remember { mutableLongStateOf(0L) }

    this.clickable(
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        interactionSource = remember { MutableInteractionSource() },
        indication = null // We keep indication null here because IconButton applies its own ripple
    ) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime.longValue >= delayMillis) {
            lastClickTime.longValue = currentTime
            onClick()
        }
    }
}