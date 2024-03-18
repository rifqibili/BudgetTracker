package com.example.budgettracker.plans
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.example.budgettracker.R

class DualColorProgressBar(context: Context, attrs: AttributeSet?) : ProgressBar(context, attrs) {

    private val mBackgroundPaint: Paint = Paint()
    private val mProgressPaint: Paint = Paint()
    private val mRectF: RectF = RectF()
    private var blueColor = ContextCompat.getColor(context, R.color.dark_blue)
    private var redColor = ContextCompat.getColor(context, R.color.red)

    init {
        // Настройка кисти для фона
        mBackgroundPaint.color = Color.WHITE

        // Настройка кисти для прогресса
        mProgressPaint.isAntiAlias = true
        mProgressPaint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Отрисовываем фон с закругленными углами
        mRectF.set(paddingLeft.toFloat(), paddingTop.toFloat(), width.toFloat() - paddingRight.toFloat(), height.toFloat() - paddingBottom.toFloat())
        canvas.drawRoundRect(mRectF, 10f, 10f, mBackgroundPaint) // Уменьшаем радиус закругления до 20 пикселей

        // Определяем ширину синей и красной частей прогресса
        val blueWidth = progress.coerceAtMost(100) * (width - paddingLeft - paddingRight) / max.toFloat()
        val redWidth = (progress.coerceIn(100, 150) - 100) * (width - paddingLeft - paddingRight) / max.toFloat()

        // Рисуем синюю часть с закругленными углами
        mProgressPaint.shader = LinearGradient(
            paddingLeft.toFloat(), paddingTop.toFloat(), (paddingLeft + blueWidth).coerceAtMost(width - paddingRight.toFloat()), height.toFloat() - paddingBottom.toFloat(),
            intArrayOf(blueColor, blueColor), null, Shader.TileMode.CLAMP
        )
        mRectF.set(paddingLeft.toFloat(), paddingTop.toFloat(), (paddingLeft + blueWidth).coerceAtMost(width - paddingRight.toFloat()), height.toFloat() - paddingBottom.toFloat())
        canvas.drawRoundRect(mRectF, 10f, 10f, mProgressPaint) // Уменьшаем радиус закругления до 20 пикселей

        // Рисуем красную часть с закругленными углами
        mProgressPaint.shader = LinearGradient(
            (paddingLeft + blueWidth).coerceAtLeast(paddingLeft.toFloat()), paddingTop.toFloat(), (paddingLeft + blueWidth + redWidth).coerceAtMost(width - paddingRight.toFloat()), height.toFloat() - paddingBottom.toFloat(),
            intArrayOf(redColor, redColor), null, Shader.TileMode.CLAMP
        )
        mRectF.set((paddingLeft + blueWidth).coerceAtLeast(paddingLeft.toFloat()), paddingTop.toFloat(), (paddingLeft + blueWidth + redWidth).coerceAtMost(width - paddingRight.toFloat()), height.toFloat() - paddingBottom.toFloat())
        canvas.drawRoundRect(mRectF, 10f, 10f, mProgressPaint) // Уменьшаем радиус закругления до 20 пикселей
    }
}
