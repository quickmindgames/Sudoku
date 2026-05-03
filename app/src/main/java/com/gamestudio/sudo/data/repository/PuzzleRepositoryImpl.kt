package com.gamestudio.sudo.data.repository

import com.gamestudio.sudo.component.Difficulty
import com.gamestudio.sudo.domain.model.SudokuPuzzle
import com.gamestudio.sudo.domain.repository.PuzzleRepository
import com.gamestudio.sudo.SudokuGenerator as SudokuGeneratorClass

class PuzzleRepositoryImpl : PuzzleRepository {

    override fun generatePuzzle(difficulty: Difficulty): SudokuPuzzle {
        val result = SudokuGeneratorClass.instance.generate(difficulty)
        val puzzle = result.first
        val solution = result.second

        return SudokuPuzzle(
            originalGrid = puzzle.map { it.toList() },
            solutionGrid = solution.map { it.toList() },
            difficulty = difficulty
        )
    }
}


