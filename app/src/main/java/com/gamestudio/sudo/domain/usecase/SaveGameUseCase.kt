package com.gamestudio.sudo.domain.usecase

import com.gamestudio.sudo.domain.model.GameData
import com.gamestudio.sudo.domain.repository.GameRepository

class SaveGameUseCase(
    private val gameRepository: GameRepository
) {
    suspend operator fun invoke(gameData: GameData) {
        gameRepository.saveGame(gameData)
    }
}

