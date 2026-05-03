package com.quickmindgames.sudoku.domain.model

enum class Difficulty(val label: String) {
    Breeze("Breeze"), // 46-51 clues
    Pulse("Pulse"), // 32-37 clues
    Rage("Rage"), // 26-31 clues
    Elite("Elite") // 22-26 clues
}