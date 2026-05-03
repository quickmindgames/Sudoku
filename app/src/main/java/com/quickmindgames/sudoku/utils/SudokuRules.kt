package com.quickmindgames.sudoku.utils

import com.quickmindgames.sudoku.data.state.CellData

/**
 *
 * Created by sagar.tahelyani on 04/03/26
 *
 */
fun isWin(userGrid: List<List<CellData>>, solution: Array<IntArray>): Boolean {
    for (r in 0..8) {
        for (c in 0..8) {
            if (userGrid[r][c].value != solution[r][c]) {
                return false
            }
        }
    }
    return true
}

fun recomputeWrongCells(
    grid: List<List<CellData>>,
    solution: Array<IntArray>
): Set<Pair<Int, Int>> {
    val wrong = mutableSetOf<Pair<Int, Int>>()
    for (r in 0..8) {
        for (c in 0..8) {
            val v = grid[r][c].value
            if (v != 0 && v != solution[r][c]) {
                wrong.add(r to c)
            }
        }
    }
    return wrong
}