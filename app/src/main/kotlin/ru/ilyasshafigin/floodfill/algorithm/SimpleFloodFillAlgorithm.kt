package ru.ilyasshafigin.floodfill.algorithm

import android.graphics.Bitmap
import android.graphics.Point
import android.util.Log
import androidx.core.graphics.get
import androidx.core.graphics.set
import java.util.*

class SimpleFloodFillAlgorithm : FloodFillAlgorithm {

    override var isStarted: Boolean = false
        private set
    private lateinit var field: Bitmap
    private var replacementColor: Int = 0
    private var targetColor: Int = 0
    private val stack = LinkedList<Point>()

    override fun start(field: Bitmap, point: Point, targetColor: Int) {
        if (isStarted) return

        this.isStarted = true
        this.field = field
        this.replacementColor = field[point.x, point.y]
        this.targetColor = targetColor
        this.stack.clear()
        this.stack += point

        Log.d(TAG, "targetColor=#${String.format("#%06X", 0xFFFFFF and targetColor)}," +
            " replacementColor=#${String.format("#%06X", 0xFFFFFF and replacementColor)}")
    }

    override fun step(): Boolean {
        if (stack.isEmpty()) {
            return false
        }

        Log.d(TAG, "stack size: ${stack.size}")

        val p = stack.pop()
        field[p.x, p.y] = targetColor

        if (p.x + 1 < field.width && field[p.x + 1, p.y] == replacementColor) {
            stack += Point(p.x + 1, p.y)
        }

        if (p.x - 1 >= 0 && field[p.x - 1, p.y] == replacementColor) {
            stack += Point(p.x - 1, p.y)
        }

        if (p.y + 1 < field.height && field[p.x, p.y + 1] == replacementColor) {
            stack += Point(p.x, p.y + 1)
        }

        if (p.y - 1 >= 0 && field[p.x, p.y - 1] == replacementColor) {
            stack += Point(p.x, p.y - 1)
        }

        return stack.isNotEmpty()
    }

    override fun stop() {
        isStarted = false
    }

    companion object {

        private const val TAG = "SimpleFloodFillAlg"
    }
}