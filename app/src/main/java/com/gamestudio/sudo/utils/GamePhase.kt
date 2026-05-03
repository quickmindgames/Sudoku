package com.gamestudio.sudo.utils

/**
 * Game Phase Enumeration
 * Defines different states of a sudoku game
 */
enum class GamePhase {
    IDLE,      // Not playing
    PLAYING,   // Active game in progress
    PAUSED,    // Game paused
    WON,       // Game completed successfully
    LOST,      // Game over (too many mistakes)
    ABANDONED  // Game abandoned by user
}

