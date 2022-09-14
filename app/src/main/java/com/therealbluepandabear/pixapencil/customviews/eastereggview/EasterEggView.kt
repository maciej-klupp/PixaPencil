/*
 * PixaPencil
 * Copyright 2022  therealbluepandabear
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.therealbluepandabear.pixapencil.customviews.eastereggview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.res.ResourcesCompat
import com.therealbluepandabear.pixapencil.R
import com.therealbluepandabear.pixapencil.extensions.createMutableClone
import com.therealbluepandabear.pixapencil.extensions.setPixel
import com.therealbluepandabear.pixapencil.models.Coordinates
import kotlin.random.Random

class EasterEggView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private lateinit var easterEggViewBitmap: Bitmap
    private lateinit var boundingRect: Rect

    private var hue = Random.nextInt(0, 361)

    private val circlePaint = Paint()
    private val textPaint = Paint()
    private val bitmapRatio = 70

    private val maxAnimationTime = 100000f

    private val hueAnimation: ValueAnimator = ValueAnimator.ofInt(hue, 360).apply {
        duration = (maxAnimationTime * ((360f - hue.toFloat()) / 360f)).toLong()
        interpolator = AccelerateDecelerateInterpolator()
        addUpdateListener { valueAnimator ->
            hue = valueAnimator.animatedValue as Int

            initCirclePaint()
            invalidate()
        }

        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                this@apply.setIntValues(0, 360)
                this@apply.duration = maxAnimationTime.toLong()
                this@apply.start()
            }
        })
    }

    private var radius = 0
    private var textSize = 0

    private val circleAnimation: ValueAnimator = ValueAnimator.ofInt(0, 250).apply {
        duration = 700
        interpolator = DecelerateInterpolator()

        addUpdateListener { valueAnimator ->
            radius = valueAnimator.animatedValue as Int
            invalidate()
        }
    }

    private val versionTextAnimation: ValueAnimator = ValueAnimator.ofInt(0, 180).apply {
        duration = 780
        interpolator = DecelerateInterpolator()

        addUpdateListener { valueAnimator ->
            textSize = valueAnimator.animatedValue as Int
            initTextPaint()
            invalidate()
        }
    }

    private fun getBitmapDimens(): Pair<Int, Int> {
        return Pair(measuredWidth / bitmapRatio, measuredHeight / bitmapRatio)
    }

    private fun initCirclePaint() {
        circlePaint.setShadowLayer(70f,0f, 0f, Color.argb(100, 0, 0, 0))
        circlePaint.color = Color.HSVToColor(floatArrayOf(hue.toFloat(), 1f, 1f))
        circlePaint.style = Paint.Style.FILL
    }

    private fun initTextPaint() {
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = textSize.toFloat()
        textPaint.color = Color.WHITE
        textPaint.typeface = ResourcesCompat.getFont(this.context, R.font.manrope_medium)
    }

    private fun randomizeHue() {
        hue = Random.nextInt(0, 361)
    }

    private fun drawText(canvas: Canvas) {
        val x = measuredWidth / 2
        val y = (measuredHeight / 2 - (textPaint.descent() + textPaint.ascent()) / 2)

        canvas.drawText("0.2", x.toFloat(), y, textPaint)
    }

    private fun drawDiagonalLine(from: Coordinates, bitmap: Bitmap) {
        for (i in 0 until easterEggViewBitmap.width) {
            if (from.x + i < easterEggViewBitmap.width && from.y + i < easterEggViewBitmap.height) {
                bitmap.setPixel(Coordinates(from.x + i, from.y + i), Color.HSVToColor(floatArrayOf(hue.toFloat(), 1f, 1f)))
            }
        }
    }

    private fun drawDiagonalLines(bitmap: Bitmap) {
        for (i in 0 until bitmap.height) {
            if (i % 3 == 0) {
                drawDiagonalLine(Coordinates(0, i), bitmap)
            }
        }

        for (i in 0 until bitmap.width) {
            if (i % 3 == 0) {
                drawDiagonalLine(Coordinates(i, 0), bitmap)
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        hueAnimation.cancel()

        if (::easterEggViewBitmap.isInitialized) {
            easterEggViewBitmap.recycle()
        }

        val dimensions = getBitmapDimens()

        easterEggViewBitmap = Bitmap.createBitmap(dimensions.first, dimensions.second, Bitmap.Config.RGB_565)
        boundingRect = Rect(0, 0, measuredWidth, measuredHeight)

        initCirclePaint()
        initTextPaint()

        hueAnimation.start()
        circleAnimation.start()
        versionTextAnimation.start()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            hueAnimation.cancel()
            randomizeHue()
            initCirclePaint()
            hueAnimation.setIntValues(hue, 360)
            hueAnimation.duration = (maxAnimationTime * ((360f - hue.toFloat()) / 360f)).toLong()
            hueAnimation.start()
            invalidate()
        }

        return true
    }

    override fun onDraw(canvas: Canvas) {
        if (::easterEggViewBitmap.isInitialized) {

            easterEggViewBitmap.eraseColor(Color.HSVToColor(floatArrayOf(hue.toFloat(), 1f, 70f / 100)))
            canvas.drawBitmap(easterEggViewBitmap, null, boundingRect, null)

            val mutableClone = easterEggViewBitmap.createMutableClone()

            drawDiagonalLines(mutableClone)

            canvas.drawBitmap(easterEggViewBitmap, null, boundingRect, null)
            canvas.drawBitmap(mutableClone, null, boundingRect, null)
            canvas.drawCircle(measuredWidth / 2f, measuredHeight / 2f, radius.toFloat(), circlePaint)

            drawText(canvas)
        }
    }
}