package com.gamestudio.sudo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamestudio.sudo.component.Difficulty
import com.gamestudio.sudo.domain.model.GameData
import com.gamestudio.sudo.domain.model.SudokuPuzzle
import com.gamestudio.sudo.domain.usecase.DeleteGameUseCase
import com.gamestudio.sudo.domain.usecase.GeneratePuzzleUseCase
import com.gamestudio.sudo.domain.usecase.LoadGameUseCase
import com.gamestudio.sudo.domain.usecase.SaveGameUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GameViewModel(
    private val generatePuzzleUseCase: GeneratePuzzleUseCase,
    private val saveGameUseCase: SaveGameUseCase,
    private val loadGameUseCase: LoadGameUseCase,
    private val deleteGameUseCase: DeleteGameUseCase
) : ViewModel() {

    private val _currentPuzzle = MutableStateFlow<SudokuPuzzle?>(null)
    val currentPuzzle: StateFlow<SudokuPuzzle?> = _currentPuzzle

    fun generateNewPuzzle(difficulty: Difficulty) {
        viewModelScope.launch {
            _currentPuzzle.value = generatePuzzleUseCase(difficulty)
        }
    }

    fun saveCurrentGame(gameData: GameData) {
        viewModelScope.launch {
            saveGameUseCase(gameData)
        }
    }

    fun loadSavedGame(onGameLoaded: (GameData) -> Unit) {
        viewModelScope.launch {
            val gameData = loadGameUseCase()
            gameData?.let { onGameLoaded(it) }
        }
    }

    fun deleteCurrentGame() {
        viewModelScope.launch {
            deleteGameUseCase()
        }
    }
}

