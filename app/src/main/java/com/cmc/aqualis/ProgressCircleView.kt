package com.cmc.aqualis

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class ProgressCircleView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paintBackground = Paint().apply {
        color = 0xFFD3D3D3.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 20f
    }
    private val paintProgress = Paint().apply {
        color = 0xFF7ED321.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 20f
    }

    private var consumed = 0f
    private val dailyGoal = 1460f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val size = min(width, height)
        val padding = 50f
        val rect = RectF(padding, padding, size - padding, size - padding)

        canvas.drawArc(rect, 0f, 360f, false, paintBackground)
        val sweepAngle = (consumed / dailyGoal) * 360f
        canvas.drawArc(rect, -90f, sweepAngle, false, paintProgress)
    }

    fun updateProgress(newConsumed: Float) {
        consumed = newConsumed
        invalidate()
    }
}
