package com.gamestudio.sudo.domain.usecase

import com.gamestudio.sudo.domain.repository.GameRepository

class LoadGameUseCase(
    private val gameRepository: GameRepository
) {
    suspend operator fun invoke() = gameRepository.loadGame()
}

