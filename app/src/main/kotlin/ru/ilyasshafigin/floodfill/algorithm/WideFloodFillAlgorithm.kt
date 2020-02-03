package ru.ilyasshafigin.floodfill.algorithm

import android.graphics.Bitmap
import android.graphics.Point
import androidx.core.graphics.get
import androidx.core.graphics.set
import java.util.*

class WideFloodFillAlgorithm : FloodFillAlgorithm {

    override var isStarted: Boolean = false
        private set
    private lateinit var field: Bitmap
    private var targetColor: Int = 0
    private var replacementColor: Int = 0
    private val stack = LinkedList<Point>()
    private lateinit var pixelsChecked: BooleanArray

    override fun start(field: Bitmap, point: Point, replacementColor: Int) {
        if (isStarted) return

        this.field = field
        this.replacementColor = replacementColor

        targetColor = field[point.x, point.y]
        pixelsChecked = BooleanArray(field.width * field.height)
        stack.clear()
        stack += point
        isStarted = true
    }

    override fun step(): Boolean {
        if (stack.isEmpty()) {
            return false
        }

        val p = stack.pop()
        field[p.x, p.y] = replacementColor

        checkPixelAndAddToStack(p.x + 1, p.y)
        checkPixelAndAddToStack(p.x - 1, p.y)
        checkPixelAndAddToStack(p.x, p.y + 1)
        checkPixelAndAddToStack(p.x, p.y - 1)

        return stack.isNotEmpty()
    }

    override fun stop() {
        isStarted = false
    }

    private fun checkPixelAndAddToStack(x: Int, y: Int) {
        if (x < 0 || x >= field.width || y < 0 || y >= field.height) return

        val index = field.width * y + x
        if (pixelsChecked[index]) return
        if (field[x, y] != targetColor) return

        pixelsChecked[index] = true
        stack += Point(x, y)
    }
}