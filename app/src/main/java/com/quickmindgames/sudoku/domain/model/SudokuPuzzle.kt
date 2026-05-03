package com.quickmindgames.sudoku.domain.model

import com.quickmindgames.sudoku.domain.model.Difficulty

data class SudokuPuzzle(
    val originalGrid: List<List<Int>>,
    val solutionGrid: List<List<Int>>,
    val difficulty: Difficulty
)

