package com.example.drinkapp.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.drinkapp.R

class WineGlassView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var fillPercentage: Float = 0f
    private var targetFillPercentage: Float = 0f

    private val glassPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.glass_outline)
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    private val winePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        shader = LinearGradient(
            0f, 0f, 0f, 200f,
            intArrayOf(
                ContextCompat.getColor(context, R.color.wine_dark),
                ContextCompat.getColor(context, R.color.wine_light)
            ),
            null,
            Shader.TileMode.CLAMP
        )
    }

    private val stemPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.glass_outline)
        style = Paint.Style.STROKE
        strokeWidth = 6f
    }

    private val basePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.glass_outline)
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    private var currentAnimator: ValueAnimator? = null

    fun setFillPercentage(percentage: Float, animate: Boolean = true) {
        targetFillPercentage = percentage.coerceIn(0f, 100f)

        if (animate) {
            currentAnimator?.cancel()
            currentAnimator = ValueAnimator.ofFloat(fillPercentage, targetFillPercentage).apply {
                duration = 500
                addUpdateListener { animator ->
                    fillPercentage = animator.animatedValue as Float
                    invalidate()
                }
                start()
            }
        } else {
            fillPercentage = targetFillPercentage
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val glassTop = height * 0.1f
        val glassBottom = height * 0.6f
        val stemTop = glassBottom
        val stemBottom = height * 0.85f
        val baseY = height * 0.9f

        val glassTopWidth = width * 0.3f
        val glassBottomWidth = width * 0.25f

        // Fill
        if (fillPercentage > 0) {
            val fillHeight = (glassBottom - glassTop) * (fillPercentage / 100f)
            val fillTop = glassBottom - fillHeight

            val fillTopRatio = (fillTop - glassTop) / (glassBottom - glassTop)
            val wineTopWidth = glassBottomWidth + (glassTopWidth - glassBottomWidth) * (1 - fillTopRatio)

            val winePath = Path().apply {

                moveTo(centerX - glassBottomWidth, glassBottom)  //bottom left
                lineTo(centerX + glassBottomWidth, glassBottom) // Bottom right
                lineTo(centerX + wineTopWidth, fillTop)// Top right
                lineTo(centerX - wineTopWidth, fillTop) // Top left
                close()
            }

            canvas.drawPath(winePath, winePaint)
        }

        val glassPath = Path().apply {
            moveTo(centerX - glassTopWidth, glassTop)  // Top left
            lineTo(centerX + glassTopWidth, glassTop)  // Top right
            lineTo(centerX + glassBottomWidth, glassBottom)  // Bottom right
            lineTo(centerX - glassBottomWidth, glassBottom)  // Bottom left
            close()
        }
        canvas.drawPath(glassPath, glassPaint)

        // Draw stem
        canvas.drawLine(centerX, stemTop, centerX, stemBottom, stemPaint)

        // Draw base
        val baseLeft = centerX - width * 0.4f
        val baseRight = centerX + width * 0.4f
        canvas.drawLine(baseLeft, baseY, baseRight, baseY, basePaint)

        // Draw small circles at connection points for better visual
        val connectionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ContextCompat.getColor(context, R.color.glass_outline)
            style = Paint.Style.FILL
        }

        // Connection between glass and stem
        canvas.drawCircle(centerX, glassBottom, 4f, connectionPaint)

        // Connection between stem and base
        canvas.drawCircle(centerX, stemBottom, 4f, connectionPaint)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = 120
        val desiredHeight = 150

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> minOf(desiredWidth, widthSize)
            else -> desiredWidth
        }

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> minOf(desiredHeight, heightSize)
            else -> desiredHeight
        }

        setMeasuredDimension(width, height)
    }
}