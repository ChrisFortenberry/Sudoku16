package com.chrisfortenberry.sudoku16.game

import androidx.lifecycle.MutableLiveData

class SudokuGame {

    var selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()
    var cellsLiveData = MutableLiveData<List<Cell>>()
    val isTakingNotesLiveData = MutableLiveData<Boolean>()
    val isDeletingLiveData = MutableLiveData<Boolean>()
    val numberPressedLiveData = MutableLiveData<Pair<Int, Int>>()

    private var selectedRow = -1
    private var selectedCol = -1
    private var isTakingNotes = false
    private var isDeleting = false
    private var numberPressed = -1
    private var sqrtSize = 4
    private var size = 16

    private val board: Board

    init {
        val cells = List(size * size) { i -> Cell(i / size, i % size, i % size)}
        cells[34].value = -1
        cells[34].notes = mutableSetOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
        board = Board(size, cells)

        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        cellsLiveData.postValue(board.cells)
        isTakingNotesLiveData.postValue(isTakingNotes)
        isDeletingLiveData.postValue(isDeleting)
        numberPressedLiveData.postValue(Pair(numberPressed, numberPressed))
    }

    fun handleInput(number: Int) {
        if (selectedRow == -1 || selectedCol == -1) {
            numberPressedLiveData.postValue(Pair(number, numberPressed))
            numberPressed = if (numberPressed == number) -1 else number
            if (isDeleting && numberPressed != -1) changeDeletingState()
        }
        else {
            val cell = board.getCell(selectedRow, selectedCol)
            if (cell.isStartingCell) return

            if (isTakingNotes) {
                cell.value = -1
                if (cell.notes.contains(number)) {
                    cell.notes.remove(number)
                } else {
                    cell.notes.add(number)
                }
            } else {
                cell.value = number
            }

            cellsLiveData.postValue(board.cells)
        }
    }

    fun updateSelectedCell(row: Int, col: Int) {
        if (row >= size || col >= size || row < 0 || col < 0) return
        if (numberPressed != -1) {
            val cell = board.getCell(row, col)
            if (cell.isStartingCell) return

            if (isTakingNotes) {
                cell.value = -1
                if (cell.notes.contains(numberPressed)) {
                    cell.notes.remove(numberPressed)
                } else {
                    cell.notes.add(numberPressed)
                }
            } else {
                cell.value = numberPressed
            }

            cellsLiveData.postValue(board.cells)
        }
        else if (isDeleting) {
            delete(row, col)
        }
        else {
            val cell = board.getCell(row, col)
            if (!cell.isStartingCell) {
                if (selectedRow == row && selectedCol == col) {
                    selectedRow = -1
                    selectedCol = -1
                } else {
                    selectedRow = row
                    selectedCol = col
                }
                selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
            }
        }
    }

    private fun updateCell(number: Int) {

    }

    private fun delete(row: Int, col: Int) {
        val cell = board.getCell(row, col)
        if (cell.notes.isNotEmpty()) {
            cell.notes.clear()
        }
        if (cell.value != -1) cell.value = -1

        cellsLiveData.postValue(board.cells)
    }

    fun changeNoteTakingState() {
        isTakingNotes = !isTakingNotes
        isTakingNotesLiveData.postValue(isTakingNotes)
        if (isTakingNotes && isDeleting) changeDeletingState()
    }

    fun changeDeletingState() {
        isDeleting = !isDeleting
        isDeletingLiveData.postValue(isDeleting)
        if (isDeleting) {
            selectedRow = -1
            selectedCol = -1
            selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
            if (isTakingNotes) changeNoteTakingState()
            if (numberPressed != -1) handleInput(numberPressed)
        }
    }

}