package com.quickmindgames.sudoku.domain.generator

import android.util.Log
import com.quickmindgames.sudoku.BuildConfig
import com.quickmindgames.sudoku.domain.model.Difficulty
import com.quickmindgames.sudoku.utils.DebugOptions
import com.quickmindgames.sudoku.utils.RemoteConfigManager
import kotlin.random.Random

/**
 * Sudoku Generator
 * Generates random Sudoku puzzles that always have exactly one solution.
 * Created by sagar.tahelyani on 22/02/26
 */
class SudokuGenerator {

    private val size = 9
    private val boxSize = 3

    /**
     * Generate a Sudoku puzzle with solution
     * @param difficulty The difficulty level
     * @return Pair of (puzzle grid, solution grid)
     */
    fun generate(difficulty: Difficulty): Pair<Array<IntArray>, Array<IntArray>> {
        val targetClues = cluesFor(difficulty)
        val result = generate(targetClues)
        val actualClues = result.first.sumOf { row -> row.count { it != 0 } }
        Log.d(
            TAG,
            "Created ${difficulty.label} puzzle: target clues $targetClues, actual clues $actualClues"
        )
        return result
    }

    /**
     * Generate a puzzle with a unique solution and as close to [targetClues] clues as possible.
     *
     * Cells are removed one at a time and a removal is kept only if the puzzle still has
     * exactly one solution. Very low targets (around 22) are not always reachable from a
     * given grid, so a few grids are tried and the one with the fewest clues is returned.
     */
    internal fun generate(
        targetClues: Int,
        random: Random = Random.Default
    ): Pair<Array<IntArray>, Array<IntArray>> {
        val target = targetClues.coerceIn(MIN_CLUES, size * size)
        var best: Pair<Array<IntArray>, Array<IntArray>>? = null
        var bestClues = Int.MAX_VALUE

        repeat(MAX_ATTEMPTS) {
            val solution = generateCompleteGrid(random)
            val puzzle = createUniquePuzzle(solution, target, random)
            val clues = puzzle.sumOf { row -> row.count { it != 0 } }
            if (clues < bestClues) {
                best = puzzle to solution
                bestClues = clues
            }
            if (clues <= target) return puzzle to solution
        }
        return best!!
    }

    /**
     * Number of clues for the difficulty, from Remote Config (or the debug override).
     */
    private fun cluesFor(difficulty: Difficulty): Int {
        if (BuildConfig.DEBUG) return DebugOptions.DEBUG_CLUES
        return when (difficulty) {
            Difficulty.Breeze -> pickClues(
                RemoteConfigManager.getBreezeMinClues(),
                RemoteConfigManager.getBreezeMaxClues()
            )

            Difficulty.Pulse -> pickClues(
                RemoteConfigManager.getPulseMinClues(),
                RemoteConfigManager.getPulseMaxClues()
            )

            Difficulty.Rage -> pickClues(
                RemoteConfigManager.getRageMinClues(),
                RemoteConfigManager.getRageMaxClues()
            )

            Difficulty.Elite -> pickClues(
                RemoteConfigManager.getEliteMinClues(),
                RemoteConfigManager.getEliteMaxClues()
            )
        }
    }

    /**
     * Generate a complete valid Sudoku grid
     */
    private fun generateCompleteGrid(random: Random): Array<IntArray> {
        val grid = Array(size) { IntArray(size) { 0 } }
        fillGrid(grid, random)
        return grid
    }

    /**
     * Fill the grid using backtracking
     */
    private fun fillGrid(grid: Array<IntArray>, random: Random): Boolean {
        for (row in 0 until size) {
            for (col in 0 until size) {
                if (grid[row][col] == 0) {
                    // Create a shuffled list of numbers 1-9
                    val numbers = (1..9).shuffled(random)

                    for (num in numbers) {
                        if (isValid(grid, row, col, num)) {
                            grid[row][col] = num

                            if (fillGrid(grid, random)) {
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
     * Remove cells one at a time (in random order), keeping a removal only if the puzzle
     * still has exactly one solution. Stops once [targetClues] is reached or no further
     * cell can be removed.
     */
    private fun createUniquePuzzle(
        solution: Array<IntArray>,
        targetClues: Int,
        random: Random
    ): Array<IntArray> {
        val puzzle = solution.map { it.clone() }.toTypedArray()
        var clues = size * size

        val positions = (0 until size * size).shuffled(random)
        for (index in positions) {
            if (clues <= targetClues) break
            val row = index / size
            val col = index % size
            val value = puzzle[row][col]
            puzzle[row][col] = 0
            if (countSolutions(puzzle, limit = 2) == 1) {
                clues--
            } else {
                puzzle[row][col] = value
            }
        }
        return puzzle
    }

    companion object {
        /** 17 is the proven minimum number of clues for a uniquely solvable Sudoku. */
        private const val MIN_CLUES = 17
        private const val MAX_ATTEMPTS = 5
        private const val ALL_DIGITS = 0x3FE // bits 1..9

        /**
         * Singleton instance
         */
        val instance = SudokuGenerator()

        /**
         * Pick a clue count from a Remote Config range without ever throwing: a reversed
         * range (min > max) is swapped, and both ends are clamped to [MIN_CLUES]..81.
         */
        internal fun pickClues(min: Int, max: Int, random: Random = Random.Default): Int {
            val low = minOf(min, max).coerceIn(MIN_CLUES, 81)
            val high = maxOf(min, max).coerceIn(MIN_CLUES, 81)
            return (low..high).random(random)
        }

        /**
         * Count the solutions of [grid] (0 = empty cell), stopping once [limit] is reached.
         * Returns 0 if the givens already conflict. [grid] is not modified.
         */
        internal fun countSolutions(grid: Array<IntArray>, limit: Int = 2): Int {
            val cells = IntArray(81)
            val rows = IntArray(9)
            val cols = IntArray(9)
            val boxes = IntArray(9)
            for (r in 0 until 9) for (c in 0 until 9) {
                val value = grid[r][c]
                if (value == 0) continue
                val bit = 1 shl value
                val box = (r / 3) * 3 + c / 3
                if ((rows[r] or cols[c] or boxes[box]) and bit != 0) return 0
                cells[r * 9 + c] = value
                rows[r] = rows[r] or bit
                cols[c] = cols[c] or bit
                boxes[box] = boxes[box] or bit
            }
            return search(cells, rows, cols, boxes, limit)
        }

        private fun search(
            cells: IntArray,
            rows: IntArray,
            cols: IntArray,
            boxes: IntArray,
            limit: Int
        ): Int {
            // Pick the empty cell with the fewest candidates.
            var bestIndex = -1
            var bestMask = 0
            var bestCount = 10
            for (i in 0 until 81) {
                if (cells[i] != 0) continue
                val r = i / 9
                val c = i % 9
                val box = (r / 3) * 3 + c / 3
                val mask = ALL_DIGITS and (rows[r] or cols[c] or boxes[box]).inv()
                val count = Integer.bitCount(mask)
                if (count == 0) return 0
                if (count < bestCount) {
                    bestIndex = i
                    bestMask = mask
                    bestCount = count
                    if (count == 1) break
                }
            }
            if (bestIndex == -1) return 1 // grid full: one solution found

            val r = bestIndex / 9
            val c = bestIndex % 9
            val box = (r / 3) * 3 + c / 3
            var found = 0
            var remaining = bestMask
            while (remaining != 0) {
                val bit = remaining and -remaining
                remaining = remaining xor bit
                cells[bestIndex] = Integer.numberOfTrailingZeros(bit)
                rows[r] = rows[r] or bit
                cols[c] = cols[c] or bit
                boxes[box] = boxes[box] or bit

                found += search(cells, rows, cols, boxes, limit - found)

                cells[bestIndex] = 0
                rows[r] = rows[r] xor bit
                cols[c] = cols[c] xor bit
                boxes[box] = boxes[box] xor bit
                if (found >= limit) break
            }
            return found
        }

        private val TAG = "SudokuGenerator"
    }
}
