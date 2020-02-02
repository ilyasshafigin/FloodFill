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
    private var currentAlgorithm: FloodFillAlgorithm = SimpleFloodFillAlgorithm()
    private var bitmapScale: Float = 1f

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
        super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
        super(context, attrs, defStyleAttr, defStyleRes)

    fun setField(bitmap: Bitmap) {
        currentAlgorithm.stop()
        this.bitmap = bitmap
        requestLayout()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (currentAlgorithm.isStarted) {
            return true
        }

        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val point = bitmap.convertPointToLocal(Point(x.toInt(), y.toInt()))
                if (point != null) {
                    val sourceColor = bitmap[point.x, point.y]
                    val targetColor = if (Color.red(sourceColor) == 0) Color.WHITE else Color.BLACK
                    currentAlgorithm.start(bitmap, point, targetColor)
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

        canvas.drawBitmap(bitmap, bitmapSrcRect, bitmapDstRect, bitmapPaint)

        if (currentAlgorithm.step()) {
            invalidate()
        } else {
            currentAlgorithm.stop()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        currentAlgorithm.stop()
    }

    private fun Bitmap.convertPointToLocal(point: Point): Point? {
        val x = ((point.x - bitmapDstRect.left) / bitmapScale).toInt()
        val y = ((point.y - bitmapDstRect.top) / bitmapScale).toInt()
        return if (x >= 0 && y >= 0 && x < width && y < height) Point(x, y) else null
    }
}