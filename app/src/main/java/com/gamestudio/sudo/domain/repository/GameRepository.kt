package com.gamestudio.sudo.domain.repository

import com.gamestudio.sudo.domain.model.GameData

interface GameRepository {
    suspend fun saveGame(gameData: GameData)
    suspend fun loadGame(): GameData?
    suspend fun deleteGame()
}

