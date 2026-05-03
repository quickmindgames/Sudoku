package com.gamestudio.sudo.data.source

/**
 * Sudoku Test Data (Legacy)
 * Contains sample sudoku puzzles - deprecated in favor of SudokuGenerator
 */
@Deprecated("Use SudokuGenerator for dynamic puzzle generation")
val easySudoku = arrayOf(
    listOf(5, 3, 4, 6, 7, 8, 9, 1, 2),
    listOf(6, 7, 2, 1, 9, 5, 3, 4, 8),
    listOf(1, 9, 8, 3, 4, 2, 5, 6, 7),

    listOf(8, 5, 9, 7, 6, 1, 4, 2, 3),
    listOf(4, 2, 6, 8, 5, 3, 7, 9, 1),
    listOf(7, 1, 3, 9, 2, 4, 8, 5, 6),

    listOf(9, 6, 1, 5, 3, 7, 2, 8, 0),
    listOf(2, 8, 7, 4, 1, 9, 6, 3, 0),
    listOf(3, 4, 5, 2, 8, 6, 1, 0, 0)
)

@Deprecated("Use SudokuGenerator for dynamic puzzle generation")
val mediumSudoku = arrayOf(
    listOf(5, 3, 0, 6, 7, 8, 9, 1, 0),
    listOf(6, 7, 2, 1, 0, 5, 3, 0, 8),
    listOf(1, 9, 8, 0, 4, 2, 0, 6, 7),

    listOf(8, 5, 9, 7, 0, 1, 4, 2, 0),
    listOf(4, 0, 6, 8, 5, 3, 0, 9, 1),
    listOf(7, 1, 0, 9, 2, 4, 8, 0, 6),

    listOf(9, 6, 1, 5, 3, 0, 2, 8, 4),
    listOf(2, 8, 7, 0, 1, 9, 6, 3, 0),
    listOf(3, 4, 5, 2, 0, 6, 1, 7, 9)
)

@Deprecated("Use SudokuGenerator for dynamic puzzle generation")
val hardSudoku = arrayOf(
    listOf(5, 0, 0, 6, 7, 0, 9, 1, 0),
    listOf(0, 7, 2, 0, 0, 5, 3, 0, 8),
    listOf(1, 0, 8, 0, 4, 0, 0, 6, 0),

    listOf(0, 5, 0, 7, 0, 1, 4, 0, 3),
    listOf(4, 0, 6, 0, 5, 0, 0, 9, 1),
    listOf(7, 1, 0, 9, 0, 4, 0, 0, 6),

    listOf(0, 6, 1, 5, 0, 7, 2, 0, 4),
    listOf(2, 0, 0, 0, 1, 9, 0, 3, 0),
    listOf(0, 4, 5, 2, 0, 6, 0, 0, 9)
)

@Deprecated("Use SudokuGenerator for dynamic puzzle generation")
val masterSudoku = arrayOf(
    listOf(5, 0, 0, 0, 7, 0, 0, 1, 0),
    listOf(0, 7, 0, 1, 0, 0, 3, 0, 0),
    listOf(1, 0, 8, 0, 0, 2, 0, 0, 7),

    listOf(0, 0, 9, 7, 0, 0, 4, 0, 0),
    listOf(0, 2, 0, 0, 5, 0, 0, 9, 0),
    listOf(7, 0, 0, 0, 2, 4, 0, 0, 6),

    listOf(0, 6, 0, 5, 0, 0, 2, 0, 0),
    listOf(2, 0, 0, 0, 1, 0, 0, 3, 0),
    listOf(0, 0, 5, 0, 0, 6, 0, 0, 9)
)

