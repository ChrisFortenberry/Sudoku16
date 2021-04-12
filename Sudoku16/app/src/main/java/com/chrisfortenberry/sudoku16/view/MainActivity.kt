package com.chrisfortenberry.sudoku16.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.chrisfortenberry.sudoku16.R
import com.chrisfortenberry.sudoku16.game.Cell
import com.chrisfortenberry.sudoku16.view.custom.SudokuBoardView
import com.chrisfortenberry.sudoku16.viewmodel.PlaySudokuViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SudokuBoardView.OnTouchListener {

    private lateinit var viewModel: PlaySudokuViewModel
    private lateinit var numberButtons: List<Button>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sudokuBoardView.registerListener(this)

        viewModel = ViewModelProviders.of(this).get(PlaySudokuViewModel::class.java)
        viewModel.sudokuGame.selectedCellLiveData.observe(this, Observer { updateSelectedCellUI(it) })
        viewModel.sudokuGame.cellsLiveData.observe(this, Observer { updateCells(it) })
        viewModel.sudokuGame.isTakingNotesLiveData.observe(this, Observer { updateNoteTakingUI(it) })
        viewModel.sudokuGame.isDeletingLiveData.observe(this, Observer { updateDeletingUI(it) })
        viewModel.sudokuGame.numberPressedLiveData.observe(this, Observer { updateNumberPressedUI(it) })


        numberButtons = listOf(zeroButton, oneButton, twoButton, threeButton, fourButton, fiveButton,
                sixButton, sevenButton, eightButton, nineButton, aButton, bButton, cButton, dButton, eButton, fButton)

        numberButtons.forEachIndexed { index, button ->
            button.setOnClickListener { viewModel.sudokuGame.handleInput(index) }
        }

        notesButton.setOnClickListener { viewModel.sudokuGame.changeNoteTakingState() }
        deleteButton.setOnClickListener { viewModel.sudokuGame.changeDeletingState() }
    }

    private fun updateCells(cells: List<Cell>?) = cells?.let {
        sudokuBoardView.updateCells(cells)
    }

    private fun updateSelectedCellUI(cell: Pair<Int, Int>?) = cell?.let {
        sudokuBoardView.updateSelectedCellUI(cell.first, cell.second)
    }

    private fun updateNoteTakingUI(isNoteTaking: Boolean?) = isNoteTaking?.let {
        notesButton.apply {
            background = if (it) ContextCompat.getDrawable(context, R.drawable.border_selected)
            else ContextCompat.getDrawable(context, R.drawable.border)
        }
    }

    private fun updateDeletingUI(isDeleting: Boolean?) = isDeleting?.let {
        deleteButton.apply {
            background = if (it) ContextCompat.getDrawable(context, R.drawable.border_selected)
                            else ContextCompat.getDrawable(context, R.drawable.border)
        }
    }

    private fun updateNumberPressedUI(numbersPressed: Pair<Int, Int>?) = numbersPressed?.let {
        if (numbersPressed.first == numbersPressed.second && numbersPressed.first != -1) {
            numberButtons[numbersPressed.first].isSelected = false
            sudokuBoardView.updateHighlightedCellsUI(-1)
        }
        else if (numbersPressed.first != -1) {
            numberButtons[numbersPressed.first].isSelected = true
            if (numbersPressed.second != -1) numberButtons[numbersPressed.second].isSelected = false
            sudokuBoardView.updateHighlightedCellsUI(numbersPressed.first)
        }
    }

    override fun onCellTouched(row: Int, col: Int) {
        viewModel.sudokuGame.updateSelectedCell(row, col)
    }
}