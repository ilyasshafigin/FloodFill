package ru.ilyasshafigin.floodfill.viewmodel

import ru.ilyasshafigin.floodfill.algorithm.FloodFillAlgorithm

class AlgorithmViewModel(
    val name: String,
    val algorithm: FloodFillAlgorithm
) {

    override fun toString(): String = name
}