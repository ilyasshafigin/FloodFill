package ru.ilyasshafigin.floodfill.algorithm

import android.graphics.Bitmap
import android.graphics.Point

interface FloodFillAlgorithm {

    val isStarted: Boolean

    fun start(field: Bitmap, point: Point, targetColor: Int)

    fun step(): Boolean

    fun stop()
}