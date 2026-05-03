package com.quickmindgames.sudoku.domain.util

/**
 * Sudoku Validator
 * Validates moves and puzzle rules
 */

/**
 * Check if a number placement is valid according to Sudoku rules
 * Rules: Number must not appear in same row, column, or 3x3 box
 */
fun isValidMove(grid: List<List<Int>>, row: Int, col: Int, num: Int): Boolean {
    if (num == 0) return true

    // Check row
    for (c in 0 until 9) {
        if (c != col && grid[row][c] == num) return false
    }

    // Check column
    for (r in 0 until 9) {
        if (r != row && grid[r][col] == num) return false
    }

    // Check 3x3 box
    val boxRowStart = (row / 3) * 3
    val boxColStart = (col / 3) * 3

    for (r in boxRowStart until boxRowStart + 3) {
        for (c in boxColStart until boxColStart + 3) {
            if ((r != row || c != col) && grid[r][c] == num) return false
        }
    }

    return true
}

