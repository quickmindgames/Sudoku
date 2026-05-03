package com.gamestudio.sudo

/**
 *
 * Created by sagar.tahelyani on 21/02/26
 *
 */
data class SudokuGameState(
    val original: List<List<Int>>,
    val userGrid: MutableList<MutableList<Int>>,
    var mistakes: Int = 0
)