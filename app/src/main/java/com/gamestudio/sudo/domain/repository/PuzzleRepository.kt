package com.gamestudio.sudo.domain.repository

import com.gamestudio.sudo.component.Difficulty
import com.gamestudio.sudo.domain.model.SudokuPuzzle

interface PuzzleRepository {
    fun generatePuzzle(difficulty: Difficulty): SudokuPuzzle
}

