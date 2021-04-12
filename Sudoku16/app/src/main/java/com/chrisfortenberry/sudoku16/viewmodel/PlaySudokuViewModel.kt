package com.chrisfortenberry.sudoku16.viewmodel

import androidx.lifecycle.ViewModel
import com.chrisfortenberry.sudoku16.game.SudokuGame

class PlaySudokuViewModel : ViewModel() {
    val sudokuGame = SudokuGame()
}