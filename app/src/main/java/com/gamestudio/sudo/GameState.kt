package com.gamestudio.sudo

import com.gamestudio.sudo.component.Difficulty

/**
 * Represents the complete state of a Sudoku game
 */
data class GameState(
    val difficulty: Difficulty,
    val originalGrid: List<List<Int>>,
    val solutionGrid: List<List<Int>>,
    val userGrid: List<List<Int>>,
    val wrongCells: Set<Pair<Int, Int>>,
    val mistakes: Int,
    val score: Int,
    val timeSeconds: Int,
    val selectedCell: Pair<Int, Int>?
) {
    /**
     * Check if the game has been started (timer has ticked at least once)
     */
    fun hasStarted(): Boolean = timeSeconds > 0

    /**
     * Serialize to string for DataStore
     */
    fun serialize(): String {
        return buildString {
            // Difficulty
            append("DIFFICULTY:${difficulty.name}\n")

            // Original Grid (9 lines)
            append("ORIGINAL:\n")
            originalGrid.forEach { row ->
                append(row.joinToString(","))
                append("\n")
            }

            // Solution Grid (9 lines)
            append("SOLUTION:\n")
            solutionGrid.forEach { row ->
                append(row.joinToString(","))
                append("\n")
            }

            // User Grid (9 lines)
            append("USER:\n")
            userGrid.forEach { row ->
                append(row.joinToString(","))
                append("\n")
            }

            // Wrong Cells
            append("WRONG:${wrongCells.joinToString(";") { "${it.first},${it.second}" }}\n")

            // Mistakes
            append("MISTAKES:$mistakes\n")

            // Score
            append("SCORE:$score\n")

            // Time
            append("TIME:$timeSeconds\n")

            // Selected Cell
            append("SELECTED:${selectedCell?.let { "${it.first},${it.second}" } ?: "null"}\n")
        }
    }

    companion object {
        /**
         * Deserialize from string
         */
        fun deserialize(data: String): GameState? {
            return try {
                val lines = data.split("\n")
                var index = 0

                // Parse difficulty
                val difficultyLine = lines[index++]
                val difficulty = Difficulty.valueOf(difficultyLine.substringAfter("DIFFICULTY:"))

                // Parse original grid
                index++ // Skip "ORIGINAL:" header
                val originalGrid = mutableListOf<List<Int>>()
                repeat(9) {
                    originalGrid.add(lines[index++].split(",").map { it.toInt() })
                }

                // Parse solution grid
                index++ // Skip "SOLUTION:" header
                val solutionGrid = mutableListOf<List<Int>>()
                repeat(9) {
                    solutionGrid.add(lines[index++].split(",").map { it.toInt() })
                }

                // Parse user grid
                index++ // Skip "USER:" header
                val userGrid = mutableListOf<List<Int>>()
                repeat(9) {
                    userGrid.add(lines[index++].split(",").map { it.toInt() })
                }

                // Parse wrong cells
                val wrongLine = lines[index++]
                val wrongCells = wrongLine.substringAfter("WRONG:")
                    .split(";")
                    .filter { it.isNotEmpty() }
                    .map {
                        val parts = it.split(",")
                        parts[0].toInt() to parts[1].toInt()
                    }
                    .toSet()

                // Parse mistakes
                val mistakes = lines[index++].substringAfter("MISTAKES:").toInt()

                // Parse score
                val score = lines[index++].substringAfter("SCORE:").toInt()

                // Parse time
                val timeSeconds = lines[index++].substringAfter("TIME:").toInt()

                // Parse selected cell
                val selectedLine = lines[index++].substringAfter("SELECTED:")
                val selectedCell = if (selectedLine == "null") {
                    null
                } else {
                    val parts = selectedLine.split(",")
                    parts[0].toInt() to parts[1].toInt()
                }

                GameState(
                    difficulty = difficulty,
                    originalGrid = originalGrid,
                    solutionGrid = solutionGrid,
                    userGrid = userGrid,
                    wrongCells = wrongCells,
                    mistakes = mistakes,
                    score = score,
                    timeSeconds = timeSeconds,
                    selectedCell = selectedCell
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}

