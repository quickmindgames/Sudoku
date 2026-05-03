package com.quickmindgames.sudoku.domain.generator

import android.util.Log
import com.quickmindgames.sudoku.domain.model.Difficulty
import com.quickmindgames.sudoku.utils.RemoteConfigManager
import kotlin.random.Random

/**
 * Sudoku Generator
 * Generates random valid Sudoku puzzles based on difficulty
 * Created by sagar.tahelyani on 22/02/26
 */
class SudokuGenerator {

    private val size = 9
    private val boxSize = 3
    private val TAG = "SudokuGenerator"

    /**
     * Generate a Sudoku puzzle with solution
     * @param difficulty The difficulty level
     * @return Pair of (puzzle grid, solution grid)
     */
    fun generate(difficulty: Difficulty): Pair<Array<IntArray>, Array<IntArray>> {
        // Generate a complete valid Sudoku grid
        val solution = generateCompleteGrid()

        // Create puzzle by removing numbers based on difficulty
        val puzzle = createPuzzle(solution, difficulty)

        return Pair(puzzle, solution)
    }

    /**
     * Generate a complete valid Sudoku grid
     */
    private fun generateCompleteGrid(): Array<IntArray> {
        val grid = Array(size) { IntArray(size) { 0 } }
        fillGrid(grid)
        return grid
    }

    /**
     * Fill the grid using backtracking
     */
    private fun fillGrid(grid: Array<IntArray>): Boolean {
        for (row in 0 until size) {
            for (col in 0 until size) {
                if (grid[row][col] == 0) {
                    // Create a shuffled list of numbers 1-9
                    val numbers = (1..9).shuffled(Random.Default)

                    for (num in numbers) {
                        if (isValid(grid, row, col, num)) {
                            grid[row][col] = num

                            if (fillGrid(grid)) {
                                return true
                            }

                            grid[row][col] = 0
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    /**
     * Check if placing num at position (row, col) is valid
     */
    private fun isValid(grid: Array<IntArray>, row: Int, col: Int, num: Int): Boolean {
        // Check row
        for (c in 0 until size) {
            if (grid[row][c] == num) return false
        }

        // Check column
        for (r in 0 until size) {
            if (grid[r][col] == num) return false
        }

        // Check 3x3 box
        val boxRow = (row / boxSize) * boxSize
        val boxCol = (col / boxSize) * boxSize
        for (r in boxRow until boxRow + boxSize) {
            for (c in boxCol until boxCol + boxSize) {
                if (grid[r][c] == num) return false
            }
        }

        return true
    }

    /**
     * Create a puzzle by removing numbers from the complete grid
     */
    private fun createPuzzle(solution: Array<IntArray>, difficulty: Difficulty): Array<IntArray> {
        val puzzle = solution.map { it.clone() }.toTypedArray()

        // Determine number of clues based on difficulty
        val clues = when (difficulty) {
            Difficulty.Breeze -> {
                val minBreezeClues = RemoteConfigManager.getBreezeMinClues()
                val maxBreezeClues = RemoteConfigManager.getBreezeMaxClues()
                (minBreezeClues..maxBreezeClues).random(Random.Default)
            }

            Difficulty.Pulse -> {
                val minPulseClues = RemoteConfigManager.getPulseMinClues()
                val maxPulseClues = RemoteConfigManager.getPulseMaxClues()
                (minPulseClues..maxPulseClues).random(Random.Default)
            }

            Difficulty.Rage -> {
                val minRageClues = RemoteConfigManager.getRageMinClues()
                val maxRageClues = RemoteConfigManager.getRageMaxClues()
                (minRageClues..maxRageClues).random(Random.Default)
            }

            Difficulty.Elite -> {
                val minEliteClues = RemoteConfigManager.getEliteMinClues()
                val maxEliteClues = RemoteConfigManager.getEliteMaxClues()
                (minEliteClues..maxEliteClues).random(Random.Default)
            }
        }
        Log.d(TAG, "Creating puzzle with difficulty: ${difficulty.label}, clues: $clues")

        // Calculate how many cells to remove
        val cellsToRemove = 81 - clues

        // Get all cell positions
        val positions = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until size) {
            for (c in 0 until size) {
                positions.add(Pair(r, c))
            }
        }

        // Shuffle and remove cells
        positions.shuffle(Random.Default)
        for (i in 0 until cellsToRemove) {
            val (r, c) = positions[i]
            puzzle[r][c] = 0
        }

        return puzzle
    }

    companion object {
        /**
         * Singleton instance
         */
        val instance = SudokuGenerator()
    }
}