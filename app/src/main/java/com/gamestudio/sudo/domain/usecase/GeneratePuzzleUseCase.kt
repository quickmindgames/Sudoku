package com.gamestudio.sudo.domain.usecase

import com.gamestudio.sudo.component.Difficulty
import com.gamestudio.sudo.domain.model.SudokuPuzzle
import com.gamestudio.sudo.domain.repository.PuzzleRepository

class GeneratePuzzleUseCase(
    private val puzzleRepository: PuzzleRepository
) {
    operator fun invoke(difficulty: Difficulty): SudokuPuzzle {
        return puzzleRepository.generatePuzzle(difficulty)
    }
}

