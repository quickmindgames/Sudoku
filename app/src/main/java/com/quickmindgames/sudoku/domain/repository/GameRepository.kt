package com.quickmindgames.sudoku.domain.repository

import com.quickmindgames.sudoku.domain.model.GameData

interface GameRepository {
    suspend fun saveGame(gameData: GameData)
    suspend fun loadGame(): GameData?
    suspend fun deleteGame()
}

