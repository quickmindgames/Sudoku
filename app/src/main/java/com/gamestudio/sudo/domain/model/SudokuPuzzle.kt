package com.gamestudio.sudo.domain.model

import com.gamestudio.sudo.component.Difficulty

data class SudokuPuzzle(
    val originalGrid: List<List<Int>>,
    val solutionGrid: List<List<Int>>,
    val difficulty: Difficulty
)

