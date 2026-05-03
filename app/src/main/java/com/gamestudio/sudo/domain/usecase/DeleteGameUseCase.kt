package com.gamestudio.sudo.domain.usecase

import com.gamestudio.sudo.domain.repository.GameRepository

class DeleteGameUseCase(
    private val gameRepository: GameRepository
) {
    suspend operator fun invoke() {
        gameRepository.deleteGame()
    }
}

