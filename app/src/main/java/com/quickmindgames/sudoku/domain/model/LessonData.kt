package com.quickmindgames.sudoku.domain.model

/**
 * Represents a single lesson in Learning Mode.
 */
data class LessonData(
    val id: Int = 0, // Optional ID for future use
    val title: String,
    val body: String,
    val illustrationEmoji: String,
    val visualGrid: List<List<Int>> // Grid of digits (0 = empty for now)
)

/**
 * All 5 lessons — always shown in order, starting from the first.
 * No progress tracking by design.
 */
val LESSONS: List<LessonData> = listOf(
    LessonData(
        id = 1,
        title = "Row Rule",
        body = "Each row must contain the numbers 1–9 exactly once. " +
                "Look across any row — if a number is already there, it can't appear again.",
        illustrationEmoji = "➡️",
        // Single row with all 9 digits
        visualGrid = listOf(
            listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        )
    ),
    LessonData(
        id = 2,
        title = "Column Rule",
        body = "Each column must contain the numbers 1–9 exactly once. " +
                "Scan top to bottom — no repeats allowed in a column.",
        illustrationEmoji = "⬇️",
        // Single column with all 9 digits (represented as 9 rows, 1 column)
        visualGrid = listOf(
            listOf(1),
            listOf(2),
            listOf(3),
            listOf(4),
            listOf(5),
            listOf(6),
            listOf(7),
            listOf(8),
            listOf(9)
        )
    ),
    LessonData(
        id = 3,
        title = "3×3 Box Rule",
        body = "Each of the nine 3×3 boxes must contain the numbers 1–9 exactly once. " +
                "Check the thick-bordered box around a cell before placing a number.",
        illustrationEmoji = "🔲",
        // 3x3 box with all 9 digits
        visualGrid = listOf(
            listOf(1, 2, 3),
            listOf(4, 5, 6),
            listOf(7, 8, 9)
        )
    ),
    LessonData(
        id = 4,
        title = "Missing Number",
        body = "When a row, column, or box has 8 of 9 numbers filled, " +
                "the missing number is the only one that can go in the empty cell. Easy win!",
        illustrationEmoji = "🔍",
        // A row with 8 numbers and one empty (0)
        visualGrid = listOf(
            listOf(1, 2, 3, 4, 5, 6, 7, 8, 0)
        )
    ),
    LessonData(
        id = 5,
        title = "Cross Checking",
        body = "Combine the row, column, and box rules together. " +
                "Eliminate numbers that already appear in the same row, column, or box — " +
                "the last remaining option is your answer.",
        illustrationEmoji = "✨",
        // A 3x3 grid showing all numbers 1-9
        visualGrid = listOf(
            listOf(1, 2, 3),
            listOf(4, 5, 6),
            listOf(7, 8, 9)
        )
    )
)