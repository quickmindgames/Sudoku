package com.quickmindgames.sudoku.domain.util

import com.quickmindgames.sudoku.data.state.CellData
import java.util.Stack

/**
 * Undo/Redo Manager
 * Manages undo and redo operations for game state
 */
val undoStack = Stack<List<List<CellData>>>()
val redoStack = Stack<List<List<CellData>>>()

/**
 * Save current grid state to undo stack
 * Clears redo stack when new move is made
 */
fun saveUndoState(grid: androidx.compose.runtime.snapshots.SnapshotStateList<androidx.compose.runtime.snapshots.SnapshotStateList<CellData>>) {
    undoStack.push(grid.map { it.toList() })
    redoStack.clear()
}

/**
 * Undo last action
 */
fun undo(grid: androidx.compose.runtime.snapshots.SnapshotStateList<androidx.compose.runtime.snapshots.SnapshotStateList<CellData>>) {
    if (undoStack.isNotEmpty()) {
        redoStack.push(grid.map { it.toList() })
        val last = undoStack.pop()
        for (r in 0..8) for (c in 0..8) grid[r][c] = last[r][c]
    }
}

/**
 * Redo last undone action
 */
fun redo(grid: androidx.compose.runtime.snapshots.SnapshotStateList<androidx.compose.runtime.snapshots.SnapshotStateList<CellData>>) {
    if (redoStack.isNotEmpty()) {
        undoStack.push(grid.map { it.toList() })
        val next = redoStack.pop()
        for (r in 0..8) for (c in 0..8) grid[r][c] = next[r][c]
    }
}

/**
 * Clear all undo/redo history
 */
fun clearUndoRedoHistory() {
    undoStack.clear()
    redoStack.clear()
}
