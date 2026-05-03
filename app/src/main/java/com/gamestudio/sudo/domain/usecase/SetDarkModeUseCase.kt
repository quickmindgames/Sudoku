package com.gamestudio.sudo.domain.usecase

import com.gamestudio.sudo.domain.repository.ThemeRepository

class SetDarkModeUseCase(
    private val themeRepository: ThemeRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        themeRepository.setDarkMode(enabled)
    }
}

