package ru.ilyasshafigin.floodfill.algorithm

import android.graphics.Bitmap
import android.graphics.Point
import androidx.core.graphics.get
import androidx.core.graphics.set
import java.util.*

class BasicFloodFillAlgorithm : FloodFillAlgorithm {

    override var isStarted: Boolean = false
        private set
    private lateinit var field: Bitmap
    private var replacementColor: Int = 0
    private var targetColor: Int = 0
    private val stack = LinkedList<Point>()
    private lateinit var pixelsChecked: BooleanArray
    private var x: Int = 0
    private var y: Int = 0
    private var spanUp = false
    private var spanDown = false

    override fun start(field: Bitmap, point: Point, replacementColor: Int) {
        if (isStarted) return

        this.field = field
        this.replacementColor = replacementColor

        targetColor = field[point.x, point.y]
        pixelsChecked = BooleanArray(field.width * field.height)
        stack.clear()
        stack += point
        x = -1
        y = -1
        spanUp = false
        spanDown = false
        isStarted = true
    }

    override fun step(): Boolean {
        if (x < 0 || x >= field.width || field[x, y] != targetColor) {
            if (stack.isEmpty()) {
                return false
            }

            val point = stack.pop()
            x = point.x
            y = point.y

            while (x > 0 && field[x - 1, y] == targetColor) x--

            spanUp = false
            spanDown = false
        } else {
            field[x, y] = replacementColor

            if (!spanUp && y > 0 && field[x, y - 1] == targetColor) {
                stack += Point(x, y - 1)
                spanUp = true
            } else if (spanUp && y > 0 && field[x, y - 1] != targetColor) {
                spanUp = false
            }

            if (!spanDown && y < field.height - 1 && field[x, y + 1] == targetColor) {
                stack += Point(x, y + 1)
                spanDown = true
            } else if (spanDown && y < field.height - 1 && field[x, y + 1] != targetColor) {
                spanDown = false
            }
            x++
        }

        return true
    }

    override fun stop() {
        isStarted = false
    }
}