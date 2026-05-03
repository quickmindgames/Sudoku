package com.gamestudio.sudo.data.repository

import com.gamestudio.sudo.GameState
import com.gamestudio.sudo.GameStateManager
import com.gamestudio.sudo.domain.model.GameData
import com.gamestudio.sudo.domain.repository.GameRepository

class GameRepositoryImpl(
    private val gameStateManager: GameStateManager
) : GameRepository {

    override suspend fun saveGame(gameData: GameData) {
        // GameData is typealias of GameState, so we can pass it directly
        gameStateManager.saveGameState(gameData)
    }

    override suspend fun loadGame(): GameData? {
        var result: GameData? = null
        gameStateManager.getSavedGameState().collect { gameState ->
            result = gameState
        }
        return result
    }

    override suspend fun deleteGame() {
        gameStateManager.clearGameState()
    }
}

