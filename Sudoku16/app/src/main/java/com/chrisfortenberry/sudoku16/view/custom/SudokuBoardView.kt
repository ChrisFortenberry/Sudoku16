package com.chrisfortenberry.sudoku16.view.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.NonNull
import com.chrisfortenberry.sudoku16.R
import com.chrisfortenberry.sudoku16.game.Cell
import kotlin.math.min

class SudokuBoardView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private var sqrtSize = 4
    private var size = 16

    // These are set in onDraw
    private var cellSizePixels = 0f
    private var noteSizePixels = 0f

    private var selectedRow = -1
    private var selectedCol = -1

    private var selectedButton = -1

    private var listener: SudokuBoardView.OnTouchListener? = null

    private var cells: List<Cell>? = null

    private val thickLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = getThemeColor(context, R.attr.colorPrimaryDark)
        strokeWidth = 6F
    }

    private val thinLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = getThemeColor(context, R.attr.colorAccent)
        strokeWidth = 2F
    }

    private val selectedCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = getThemeColor(context, R.attr.colorPrimaryDark)
    }

    private val conflictingCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = getThemeColor(context, R.attr.colorPrimary)
    }

    private val textPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = getThemeColor(context, android.R.attr.textColor)
    }

    private val startingCellTextPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = getThemeColor(context, android.R.attr.textColor)
        typeface = Typeface.DEFAULT_BOLD
    }

    private val startingCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#acacac")
    }

    private val noteTextPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = getThemeColor(context, android.R.attr.textColor)
        typeface = Typeface.DEFAULT_BOLD
    }

    @ColorInt
    fun getThemeColor(
            @NonNull context:Context,
            @AttrRes attributeColor:Int
    ):Int {
        val value = TypedValue()
        context.theme.resolveAttribute(attributeColor, value, true)
        return value.data
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sizePixels = min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(sizePixels, sizePixels)
    }

    override fun onDraw(canvas: Canvas) {
        updateMeasurements(width)

        fillCells(canvas)
        drawLines(canvas)
        drawText(canvas)
    }

    private fun updateMeasurements(width: Int) {
        cellSizePixels = (width / size).toFloat()
        noteSizePixels = cellSizePixels / sqrtSize.toFloat()
        noteTextPaint.textSize = cellSizePixels / sqrtSize.toFloat()
        textPaint.textSize = cellSizePixels / 1.5f
        startingCellTextPaint.textSize = cellSizePixels / 1.5f
    }

    private fun fillCells(canvas: Canvas) {
        val selectedCell = if (selectedRow != -1 && selectedCol != -1) cells?.get(selectedRow * size + selectedCol) else null
        cells?.forEach {
            val r = it.row
            val c = it.col

            if (it.isStartingCell) {
                fillCell(canvas, r, c, startingCellPaint)
            }
            else if (r == selectedRow && c == selectedCol) {
                fillCell(canvas, r, c, selectedCellPaint)
            }
            else if (selectedCell != null) {
                if ( (it.value == selectedCell.value && it.value != -1) || it.notes.contains(selectedCell.value)) {
                    fillCell(canvas, r, c, conflictingCellPaint)
                }
            }
            else if (selectedButton != -1 && (it.value == selectedButton || it.notes.contains(selectedButton))) {
                fillCell(canvas, r, c, conflictingCellPaint)
            }
            /*else if (r == selectedRow || c == selectedCol) {
                fillCell(canvas, r, c, conflictingCellPaint)
            }
            else if (r / sqrtSize == selectedRow / sqrtSize && c / sqrtSize == selectedCol / sqrtSize) {
                fillCell(canvas, r, c, conflictingCellPaint)
            }*/
        }
    }

    private fun fillCell(canvas: Canvas, r: Int, c: Int, paint: Paint) {
        canvas.drawRect(
            c * cellSizePixels,
            r * cellSizePixels,
            (c + 1) * cellSizePixels,
            (r + 1) * cellSizePixels,
            paint
        )
    }

    private fun drawLines(canvas: Canvas) {
        var thickLines = mutableListOf<Int>()

        for (i in 1 until size) {
            if (i % sqrtSize == 0) {
                thickLines.add(i)
                continue
            }

            canvas.drawLine(
                i * cellSizePixels,
                0f,
                i * cellSizePixels,
                height.toFloat(),
                thinLinePaint
            )

            canvas.drawLine(
                0f,
                i * cellSizePixels,
                width.toFloat(),
                i * cellSizePixels,
                thinLinePaint
            )
        }

        thickLines.forEach() {
            canvas.drawLine(
                it * cellSizePixels,
                0f,
                it * cellSizePixels,
                height.toFloat(),
                thickLinePaint
            )

            canvas.drawLine(
                0f,
                it * cellSizePixels,
                width.toFloat(),
                it * cellSizePixels,
                thickLinePaint
            )
        }

        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), thickLinePaint)
    }

    private fun drawText(canvas: Canvas) {
        val values = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F")
        cells?.forEach { cell ->
            val value = cell.value
            val textBounds = Rect()

            if (value == -1) {
                // Draw notes
                cell.notes.forEach { note ->
                    val rowInCell = note / sqrtSize
                    val colInCell = note % sqrtSize
                    val valueString = values[note]
                    noteTextPaint.getTextBounds(valueString, 0, valueString.length, textBounds)
                    val textWidth = noteTextPaint.measureText(valueString)
                    val textHeight = textBounds.height()

                    canvas.drawText(
                        valueString,
                        (cell.col * cellSizePixels) + (colInCell * noteSizePixels) + (noteSizePixels / 2) - (textWidth / 2f),
                        (cell.row * cellSizePixels) + (rowInCell * noteSizePixels) + (noteSizePixels / 2) + (textHeight / 2f),
                        noteTextPaint
                    )
                }
            }
            else {
                val row = cell.row
                val col = cell.col
                val valueString = values[value]

                val paintToUse = if (cell.isStartingCell) startingCellTextPaint else textPaint
                paintToUse.getTextBounds(valueString, 0, valueString.length, textBounds)
                val textWidth = paintToUse.measureText(valueString)
                val textHeight = textBounds.height()

                canvas.drawText(
                    valueString,
                    (col * cellSizePixels) + (cellSizePixels / 2) - (textWidth / 2),
                    (row * cellSizePixels) + (cellSizePixels / 2) + (textHeight / 2),
                    paintToUse)
            }

        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                handleTouchEvent(event.x, event.y)
                true
            }
            else -> false
        }
    }

    private fun handleTouchEvent(x: Float, y: Float) {
        val possibleSelectedRow = (y / cellSizePixels).toInt()
        val possibleSelectedCol = (x / cellSizePixels).toInt()
        listener?.onCellTouched(possibleSelectedRow, possibleSelectedCol)
    }

    fun updateSelectedCellUI(row: Int, col: Int) {
        selectedRow = row
        selectedCol = col
        invalidate()
    }

    fun updateCells(cells: List<Cell>) {
        this.cells = cells
        invalidate()
    }

    fun updateHighlightedCellsUI(number: Int) {
        selectedButton = number
        invalidate()
    }

    fun registerListener(listener: SudokuBoardView.OnTouchListener) {
        this.listener = listener
    }

    interface OnTouchListener {
        fun onCellTouched(row: Int, col: Int)
    }
}