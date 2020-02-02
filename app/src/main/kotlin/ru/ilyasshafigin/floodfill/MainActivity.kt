package ru.ilyasshafigin.floodfill

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.set
import kotlinx.android.synthetic.main.activity_main.*
import ru.ilyasshafigin.floodfill.algorithm.BasicFloodFillAlgorithm
import ru.ilyasshafigin.floodfill.algorithm.SimpleFloodFillAlgorithm
import ru.ilyasshafigin.floodfill.viewmodel.AlgorithmViewModel
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val defaultWidth = 64
        val defaultHeight = 64

        val algorithmList = listOf(
            AlgorithmViewModel("Простой, в ширину", SimpleFloodFillAlgorithm()),
            AlgorithmViewModel("Базовый, линиями", BasicFloodFillAlgorithm())
        )

        algorithmSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, algorithmList)
        algorithmSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(view: AdapterView<*>, itemView: View?, index: Int, id: Long) {
                floodFillView.setAlgorithm(algorithmList[index].algorithm)
            }

            override fun onNothingSelected(view: AdapterView<*>) {
                floodFillView.setAlgorithm(algorithmList[0].algorithm)
            }
        }

        widthEdit.setText(defaultWidth.toString(), TextView.BufferType.EDITABLE)
        widthEdit.setOnEditorActionListener { view, action, _ ->
            if (action == EditorInfo.IME_ACTION_NEXT) {
                heightEdit.requestFocus(View.FOCUS_UP)
                true
            } else {
                false
            }
        }
        heightEdit.setText(defaultHeight.toString(), TextView.BufferType.EDITABLE)
        heightEdit.setOnEditorActionListener { view, action, _ ->
            if (action == EditorInfo.IME_ACTION_DONE) {
                onGeneratePressed()
                true
            } else {
                false
            }
        }

        generateButton.setOnClickListener {
            onGeneratePressed()
        }

        speedSlider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                floodFillView.setSpeed(value.toInt())
            }
        }

        floodFillView.setField(createCircleBitmap(defaultWidth, defaultHeight))
    }

    private fun onGeneratePressed() {
        val width = widthEdit.text.toString().toIntOrNull() ?: 1
        val height = heightEdit.text.toString().toIntOrNull() ?: 1
        floodFillView.setField(createCircleBitmap(width, height))
    }

    private fun createRandomBitmap(width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                bitmap[x, y] = if (Math.random() < 0.5) Color.BLACK else Color.WHITE
            }
        }
        return bitmap
    }

    private fun createCircleBitmap(width: Int, height: Int): Bitmap {
        val px = width / 10
        val py = height / 10
        val hw = width / 2
        val hh = height / 2
        val r = min(hw - px, hh - py)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                if ((x - hw) * (x - hw) + (y - hh) * (y - hh) > r * r) {
                    bitmap[x, y] = Color.BLACK
                } else {
                    bitmap[x, y] = Color.WHITE
                }
            }
        }
        return bitmap
    }
}
