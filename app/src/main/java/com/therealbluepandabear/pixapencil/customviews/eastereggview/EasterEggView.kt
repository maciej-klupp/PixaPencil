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

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.therealbluepandabear.pixapencil.R
import kotlin.random.Random

class EasterEggView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private lateinit var easterEggViewBitmap: Bitmap
    private lateinit var boundingRect: Rect

    private var pixelsArr = IntArray(0)
    private var hsvArr = FloatArray(3)

    private var hue = Random.nextInt(0, 361)

    private val handlerRunnable = Runnable {
        invalidate()
    }
    private val circlePaint = Paint()
    private val textPaint = Paint()
    private val bitmapRatio = 70

    private fun initPixelsArr() {
        pixelsArr = IntArray(easterEggViewBitmap.width * easterEggViewBitmap.height)
    }

    private fun getBitmapDimens(): Pair<Int, Int> {
        return Pair(measuredWidth / bitmapRatio, measuredHeight / bitmapRatio)
    }

    private fun initCirclePaint() {
        circlePaint.color = Color.HSVToColor(floatArrayOf(hue.toFloat(), 1f, 1f))
        circlePaint.style = Paint.Style.FILL
    }

    private fun initTextPaint() {
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = 180f
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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        handler.removeCallbacks(handlerRunnable)

        if (::easterEggViewBitmap.isInitialized) {
            easterEggViewBitmap.recycle()
        }

        val dimensions = getBitmapDimens()

        easterEggViewBitmap = Bitmap.createBitmap(dimensions.first, dimensions.second, Bitmap.Config.RGB_565)
        boundingRect = Rect(0, 0, measuredWidth, measuredHeight)

        initPixelsArr()
        initCirclePaint()
        initTextPaint()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            performClick()
            handler.removeCallbacks(handlerRunnable)

            randomizeHue()
            initCirclePaint()
            invalidate()
        }

        return true
    }

    override fun onDraw(canvas: Canvas) {
        if (::easterEggViewBitmap.isInitialized) {
            easterEggViewBitmap.getPixels(pixelsArr, 0, easterEggViewBitmap.width, 0, 0, easterEggViewBitmap.width, easterEggViewBitmap.height)

            for (i in pixelsArr.indices) {
                hsvArr[0] = hue.toFloat()
                hsvArr[1] = 1f
                hsvArr[2] = Random.nextInt(65, 101).toFloat() / 100f
                pixelsArr[i] = Color.HSVToColor(hsvArr)
            }

            easterEggViewBitmap = Bitmap.createBitmap(pixelsArr, easterEggViewBitmap.width, easterEggViewBitmap.height, Bitmap.Config.RGB_565)
            canvas.drawBitmap(easterEggViewBitmap, null, boundingRect, null)
            canvas.drawCircle(measuredWidth / 2f, measuredHeight / 2f, 250f, circlePaint)

            drawText(canvas)
            handler.postDelayed(handlerRunnable, 2500)
        }
    }
}