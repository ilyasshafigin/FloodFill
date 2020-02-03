package ru.ilyasshafigin.floodfill.view

import androidx.annotation.IntDef

@IntDef(
    FloodFillAlgorithmType.WIDE_ALGORITHM,
    FloodFillAlgorithmType.DEPTH_ALGORITHM,
    FloodFillAlgorithmType.LINES_ALGORITHM
)
annotation class FloodFillAlgorithmType {

    companion object {

        const val WIDE_ALGORITHM = 0
        const val DEPTH_ALGORITHM = 1
        const val LINES_ALGORITHM = 2

        @FloodFillAlgorithmType
        const val DEFAULT_ALGORITHM = WIDE_ALGORITHM
    }
}