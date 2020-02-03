package ru.ilyasshafigin.floodfill.algorithm

import android.graphics.Bitmap
import android.graphics.Point
import androidx.core.graphics.get
import androidx.core.graphics.set
import java.util.*

class DepthFloodFillAlgorithm : FloodFillAlgorithm {

    override var isStarted: Boolean = false
        private set
    private lateinit var field: Bitmap
    private var targetColor: Int = 0
    private var replacementColor: Int = 0
    private val stacks = Array(4) { LinkedList<Point>() }
    private lateinit var pixelsChecked: BooleanArray

    override fun start(field: Bitmap, point: Point, replacementColor: Int) {
        if (isStarted) return

        this.field = field
        this.replacementColor = replacementColor

        targetColor = field[point.x, point.y]
        pixelsChecked = BooleanArray(field.width * field.height)
        stacks.forEach { it.clear() }
        stacks[LEFT].add(point)
        isStarted = true
    }

    override fun step(): Boolean {
        val stack = stacks.find { it.isNotEmpty() } ?: return false
        val p = stack.pop()
        field[p.x, p.y] = replacementColor
        checkPixelAndAddToStack(stacks[LEFT], p.x - 1, p.y)
        checkPixelAndAddToStack(stacks[TOP], p.x, p.y - 1)
        checkPixelAndAddToStack(stacks[RIGHT], p.x + 1, p.y)
        checkPixelAndAddToStack(stacks[BOTTOM], p.x, p.y + 1)
        return true
    }

    override fun stop() {
        isStarted = false
    }

    private fun checkPixelAndAddToStack(stack: LinkedList<Point>, x: Int, y: Int) {
        if (x < 0 || x >= field.width || y < 0 || y >= field.height) return

        val index = field.width * y + x
        if (pixelsChecked[index]) return
        if (field[x, y] != targetColor) return

        pixelsChecked[index] = true
        stack += Point(x, y)
    }

    companion object {

        private const val LEFT = 0
        private const val TOP = 1
        private const val RIGHT = 2
        private const val BOTTOM = 3
    }
}