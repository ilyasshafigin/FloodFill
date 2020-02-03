package ru.ilyasshafigin.floodfill.viewmodel

import ru.ilyasshafigin.floodfill.view.FloodFillAlgorithmType

class AlgorithmViewModel(
    val name: String,
    @FloodFillAlgorithmType
    val type: Int
) {

    override fun toString(): String = name
}