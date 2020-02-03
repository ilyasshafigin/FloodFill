package ru.ilyasshafigin.floodfill.view

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import androidx.core.graphics.get
import ru.ilyasshafigin.floodfill.algorithm.DepthFloodFillAlgorithm
import ru.ilyasshafigin.floodfill.algorithm.FloodFillAlgorithm
import ru.ilyasshafigin.floodfill.algorithm.LinesFloodFillAlgorithm
import ru.ilyasshafigin.floodfill.algorithm.WideFloodFillAlgorithm

internal class FloodFillController {

    private val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bitmapSrcRect = Rect()
    private val bitmapDstRect = Rect()
    private var bitmapScale: Float = 1f

    private var bitmap: Bitmap
    private var itersPerFrame: Int = 1
    @FloodFillAlgorithmType
    private var algorithmType: Int = FloodFillAlgorithmType.DEFAULT_ALGORITHM

    private lateinit var algorithm: FloodFillAlgorithm

    init {
        bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565)
        initAlgorithm()
    }

    fun setField(bitmap: Bitmap) {
        algorithm.stop()
        this.bitmap = bitmap
    }

    fun setSpeed(speed: Int) {
        itersPerFrame = speed
    }

    fun setAlgorithm(@FloodFillAlgorithmType algorithmType: Int) {
        this.algorithmType = algorithmType
        algorithm.stop()
        initAlgorithm()
    }

    fun canTouch(): Boolean {
        return !algorithm.isStarted
    }

    fun onTap(x: Int, y: Int): Boolean {
        val point = convertToLocalPoint(Point(x, y))
        if (point != null) {
            val sourceColor = bitmap[point.x, point.y]
            val targetColor = if (Color.red(sourceColor) == 0) Color.WHITE else Color.BLACK
            algorithm.start(bitmap, point, targetColor)
            return true
        } else {
            return false
        }
    }

    fun onSizeChanged(width: Int, height: Int) {
        val bw = bitmap.width
        val bh = bitmap.height

        if (height * bw > width * bh) {
            bitmapScale = width.toFloat() / bw.toFloat()
            val h = (bitmapScale * bh).toInt()
            bitmapDstRect.set(0, (height - h) / 2, width, h + (height - h) / 2)
        } else {
            bitmapScale = height.toFloat() / bh.toFloat()
            val w = (bitmapScale * bw).toInt()
            bitmapDstRect.set((width - w) / 2, 0, (width - w) / 2 + w, height)
        }
        bitmapSrcRect.set(0, 0, bw, bh)
    }

    fun onDraw(canvas: Canvas): Boolean {
        var isRedraw = false

        if (algorithm.isStarted) {
            for (i in 0 until itersPerFrame) {
                if (algorithm.step()) {
                    isRedraw = true
                } else {
                    algorithm.stop()
                    isRedraw = false
                }
            }
        }

        canvas.drawBitmap(bitmap, bitmapSrcRect, bitmapDstRect, bitmapPaint)

        return isRedraw
    }

    fun onSave(savedState: FloodFillView.SavedState) {
        savedState.bitmap = bitmap
        savedState.itersPerFrame = itersPerFrame
        savedState.algorithmType = algorithmType
    }

    fun onRestore(savedState: FloodFillView.SavedState) {
        savedState.bitmap?.let { bitmap = it }
        itersPerFrame = savedState.itersPerFrame
        algorithmType = savedState.algorithmType
        initAlgorithm()
    }

    private fun initAlgorithm() {
        algorithm = when (algorithmType) {
            FloodFillAlgorithmType.WIDE_ALGORITHM -> WideFloodFillAlgorithm()
            FloodFillAlgorithmType.DEPTH_ALGORITHM -> DepthFloodFillAlgorithm()
            FloodFillAlgorithmType.LINES_ALGORITHM -> LinesFloodFillAlgorithm()
            else -> throw IllegalStateException("unsupported algorithm `$algorithmType`")
        }
    }

    private fun convertToLocalPoint(point: Point): Point? {
        val x = ((point.x - bitmapDstRect.left) / bitmapScale).toInt()
        val y = ((point.y - bitmapDstRect.top) / bitmapScale).toInt()
        if (
            x >= bitmapSrcRect.left && x < bitmapSrcRect.right &&
            y >= bitmapSrcRect.top && y < bitmapSrcRect.bottom
        ) {
            return Point(x, y)
        } else {
            return null
        }
    }
}