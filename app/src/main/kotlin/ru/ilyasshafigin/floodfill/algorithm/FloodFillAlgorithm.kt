package ru.ilyasshafigin.floodfill.algorithm

import android.graphics.Bitmap
import android.graphics.Point

interface FloodFillAlgorithm {

    /** Работает ли сейчас алгоритм? */
    val isStarted: Boolean

    /**
     * Запускает алгоритм на заполнение поля [field] цветом [replacementColor],
     * начиная с точки [point]
     */
    fun start(field: Bitmap, point: Point, replacementColor: Int)

    /**
     * Шаг алгоритма
     * @return `true` если алгоритм еще продолжает работать
     */
    fun step(): Boolean

    /**
     * Останавливает выполнение алгоритма
     */
    fun stop()
}