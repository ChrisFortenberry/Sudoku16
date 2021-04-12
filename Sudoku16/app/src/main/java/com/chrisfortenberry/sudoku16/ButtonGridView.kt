package com.chrisfortenberry.sudoku16

import android.content.Context
import android.util.AttributeSet
import android.widget.GridView

class ButtonGridView(context: Context, attributeSet: AttributeSet) : GridView(context, attributeSet) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }
}