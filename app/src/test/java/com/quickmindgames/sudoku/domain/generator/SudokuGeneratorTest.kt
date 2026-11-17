package com.quickmindgames.sudoku.domain.generator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class SudokuGeneratorTest {

    private val generator = SudokuGenerator()

    // ── Independent reference solver (does not reuse the generator's code) ──────────

    private fun referenceCount(grid: Array<IntArray>, limit: Int = 2): Int {
        var bestR = -1
        var bestC = -1
        var bestCandidates: List<Int>? = null
        for (r in 0..8) for (c in 0..8) if (grid[r][c] == 0) {
            val candidates = (1..9).filter { canPlace(grid, r, c, it) }
            if (bestCandidates == null || candidates.size < bestCandidates.size) {
                bestR = r; bestC = c; bestCandidates = candidates
            }
        }
        if (bestCandidates == null) return 1
        var count = 0
        for (n in bestCandidates) {
            grid[bestR][bestC] = n
            count += referenceCount(grid, limit - count)
            grid[bestR][bestC] = 0
            if (count >= limit) break
        }
        return count
    }

    private fun canPlace(g: Array<IntArray>, r: Int, c: Int, n: Int): Boolean {
        for (i in 0..8) if (g[r][i] == n || g[i][c] == n) return false
        val br = r / 3 * 3
        val bc = c / 3 * 3
        for (x in br until br + 3) for (y in bc until bc + 3) if (g[x][y] == n) return false
        return true
    }

    private fun Array<IntArray>.copy() = map { it.clone() }.toTypedArray()
    private fun Array<IntArray>.clueCount() = sumOf { row -> row.count { it != 0 } }
    private fun Array<IntArray>.pretty() = joinToString("\n") { it.joinToString("") }

    // ── Generator tests ─────────────────────────────────────────────────────────────

    private fun assertGeneratesUnique(clueRange: IntRange, runs: Int, maxAllowedClues: Int) {
        val random = Random(42) // fixed seed → reproducible failures
        repeat(runs) { run ->
            val target = clueRange.random(random)
            val (puzzle, solution) = generator.generate(target, random)

            assertEquals(
                "run $run (target $target clues) is not uniquely solvable:\n${puzzle.pretty()}",
                1,
                referenceCount(puzzle.copy())
            )
            for (r in 0..8) for (c in 0..8) {
                if (puzzle[r][c] != 0) assertEquals(solution[r][c], puzzle[r][c])
            }
            assertTrue(
                "run $run: solution grid is not valid",
                isCompleteValidGrid(solution)
            )
            val clues = puzzle.clueCount()
            assertTrue("run $run: $clues clues is below target $target", clues >= target)
            assertTrue("run $run: $clues clues exceeds $maxAllowedClues", clues <= maxAllowedClues)
        }
    }

    @Test
    fun breeze_puzzlesAreUnique() = assertGeneratesUnique(46..51, runs = 50, maxAllowedClues = 51)

    @Test
    fun pulse_puzzlesAreUnique() = assertGeneratesUnique(32..37, runs = 50, maxAllowedClues = 37)

    @Test
    fun rage_puzzlesAreUnique() = assertGeneratesUnique(26..31, runs = 50, maxAllowedClues = 32)

    // 22 clues is not always reachable, so allow a few extra clues.
    @Test
    fun elite_puzzlesAreUnique() = assertGeneratesUnique(22..26, runs = 50, maxAllowedClues = 30)

    @Test
    fun elite_generationIsFastEnough() {
        val random = Random(7)
        generator.generate(24, random) // warm-up
        val runs = 20
        val start = System.nanoTime()
        repeat(runs) { generator.generate(22, random) }
        val averageMs = (System.nanoTime() - start) / 1_000_000.0 / runs
        println("Average Elite generation: %.1f ms".format(averageMs))
        assertTrue("Elite generation too slow: $averageMs ms", averageMs < 300)
    }

    // ── countSolutions tests ────────────────────────────────────────────────────────

    @Test
    fun countSolutions_knownUniquePuzzle() {
        val puzzle = parse(
            "530070000600195000098000060800060003400803001700020006060000280000419005000080079"
        )
        assertEquals(1, SudokuGenerator.countSolutions(puzzle))
    }

    @Test
    fun countSolutions_emptyGridHasMany() {
        assertEquals(2, SudokuGenerator.countSolutions(Array(9) { IntArray(9) }, limit = 2))
    }

    @Test
    fun countSolutions_conflictingGivensHaveNone() {
        val grid = Array(9) { IntArray(9) }
        grid[0][0] = 5
        grid[0][8] = 5
        assertEquals(0, SudokuGenerator.countSolutions(grid))
    }

    @Test
    fun countSolutions_doesNotModifyInput() {
        val puzzle = parse(
            "530070000600195000098000060800060003400803001700020006060000280000419005000080079"
        )
        val before = puzzle.copy()
        SudokuGenerator.countSolutions(puzzle)
        assertTrue(before.indices.all { before[it].contentEquals(puzzle[it]) })
    }

    @Test
    fun countSolutions_agreesWithReferenceSolver() {
        val random = Random(99)
        repeat(30) {
            val (_, solution) = generator.generate(81, random)
            val holes = solution.copy()
            (0 until 81).shuffled(random).take(random.nextInt(40, 60)).forEach {
                holes[it / 9][it % 9] = 0
            }
            assertEquals(
                referenceCount(holes.copy(), limit = 2),
                SudokuGenerator.countSolutions(holes, limit = 2)
            )
        }
    }

    private fun isCompleteValidGrid(g: Array<IntArray>): Boolean {
        val all = (1..9).toSet()
        for (i in 0..8) {
            if (g[i].toSet() != all) return false
            if ((0..8).map { g[it][i] }.toSet() != all) return false
            val br = i / 3 * 3
            val bc = i % 3 * 3
            if ((br until br + 3).flatMap { r -> (bc until bc + 3).map { c -> g[r][c] } }
                    .toSet() != all
            ) return false
        }
        return true
    }

    private fun parse(digits: String) =
        Array(9) { r -> IntArray(9) { c -> digits[r * 9 + c] - '0' } }
}
