package ru.ilyasshafigin.floodfill.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.get
import ru.ilyasshafigin.floodfill.algorithm.FloodFillAlgorithm
import ru.ilyasshafigin.floodfill.algorithm.SimpleFloodFillAlgorithm

class FloodFillView : View {

    private var bitmap: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565)
    private val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bitmapSrcRect = Rect()
    private val bitmapDstRect = Rect()
    private var bitmapScale: Float = 1f
    private var algorithm: FloodFillAlgorithm = SimpleFloodFillAlgorithm()
    private var itersPerFrame: Int = 1

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
        super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
        super(context, attrs, defStyleAttr, defStyleRes)

    fun setField(bitmap: Bitmap) {
        algorithm.stop()
        this.bitmap = bitmap
        requestLayout()
    }

    fun setSpeed(speed: Int) {
        itersPerFrame = speed
    }

    fun setAlgorithm(algorithm: FloodFillAlgorithm) {
        this.algorithm.stop()
        this.algorithm = algorithm
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (algorithm.isStarted) {
            return true
        }

        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val point = bitmap.convertToLocalPoint(Point(x.toInt(), y.toInt()))
                if (point != null) {
                    val sourceColor = bitmap[point.x, point.y]
                    val targetColor = if (Color.red(sourceColor) == 0) Color.WHITE else Color.BLACK
                    algorithm.start(bitmap, point, targetColor)
                    invalidate()
                }
            }
        }
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val vw = width
        val vh = height
        val bw = bitmap.width
        val bh = bitmap.height

        if (vh * bw > vw * bh) {
            bitmapScale = vw.toFloat() / bw.toFloat()
            val h = (bitmapScale * bh).toInt()
            bitmapDstRect.set(0, (vh - h) / 2, vw, h + (vh - h) / 2)
        } else {
            bitmapScale = vh.toFloat() / bh.toFloat()
            val w = (bitmapScale * bw).toInt()
            bitmapDstRect.set((vw - w) / 2, 0, (vw - w) / 2 + w, vh)
        }
        bitmapSrcRect.set(0, 0, bw, bh)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

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

        if (isRedraw) {
            invalidate()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        algorithm.stop()
    }

    private fun Bitmap.convertToLocalPoint(point: Point): Point? {
        val x = ((point.x - bitmapDstRect.left) / bitmapScale).toInt()
        val y = ((point.y - bitmapDstRect.top) / bitmapScale).toInt()
        return if (x >= 0 && y >= 0 && x < width && y < height) Point(x, y) else null
    }
}