package ru.ilyasshafigin.floodfill

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.set
import kotlinx.android.synthetic.main.activity_main.*
import ru.ilyasshafigin.floodfill.view.FloodFillAlgorithmType
import ru.ilyasshafigin.floodfill.viewmodel.AlgorithmViewModel
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val defaultWidth = 64
        val defaultHeight = 64
        val defaultSpeed = 20

        val algorithmList = listOf(
            AlgorithmViewModel(
                getString(R.string.wide_algorithm),
                FloodFillAlgorithmType.WIDE_ALGORITHM
            ),
            AlgorithmViewModel(
                getString(R.string.depth_algorithm),
                FloodFillAlgorithmType.DEPTH_ALGORITHM
            ),
            AlgorithmViewModel(
                getString(R.string.lines_algorithm),
                FloodFillAlgorithmType.LINES_ALGORITHM
            )
        )

        algorithmSpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, algorithmList)
        algorithmSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                view: AdapterView<*>,
                itemView: View?,
                index: Int,
                id: Long
            ) {
                floodFillView.setAlgorithm(algorithmList[index].type)
            }

            override fun onNothingSelected(view: AdapterView<*>) {
                floodFillView.setAlgorithm(FloodFillAlgorithmType.DEFAULT_ALGORITHM)
            }
        }

        widthEdit.setText(defaultWidth.toString(), TextView.BufferType.EDITABLE)
        widthEdit.setOnEditorActionListener { _, action, _ ->
            if (action == EditorInfo.IME_ACTION_NEXT) {
                heightEdit.requestFocus(View.FOCUS_UP)
                true
            } else {
                false
            }
        }
        heightEdit.setText(defaultHeight.toString(), TextView.BufferType.EDITABLE)
        heightEdit.setOnEditorActionListener { _, action, _ ->
            if (action == EditorInfo.IME_ACTION_DONE) {
                onGeneratePressed()
                hideKeyboard()
                true
            } else {
                false
            }
        }

        generateButton.setOnClickListener {
            onGeneratePressed()
        }

        speedSlider.value = defaultSpeed.toFloat()
        speedSlider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                floodFillView.setSpeed(value.toInt())
            }
        }

        floodFillView.setField(createCircleBitmap(defaultWidth, defaultHeight))
        floodFillView.setSpeed(defaultSpeed)
        floodFillView.setAlgorithm(FloodFillAlgorithmType.DEFAULT_ALGORITHM)
        floodFillView.requestLayout()
    }

    private fun onGeneratePressed() {
        val width = widthEdit.text.toString().toIntOrNull()
        if (width == null || width <= 0 || width > 512) {
            widthEdit.error = getString(R.string.width_error)
            return
        }

        val height = heightEdit.text.toString().toIntOrNull()
        if (height == null || height <= 0 || height > 512) {
            heightEdit.error = getString(R.string.height_error)
            return
        }

        floodFillView.setField(createCircleBitmap(width, height))
    }

    fun hideKeyboard() {
        currentFocus?.let { currentFocus ->
            val inputMethodManager =
                getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
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
        val px = width / 8
        val py = height / 8
        val hw = width / 2
        val hh = height / 2
        val r = min(hw - px, hh - py)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                if ((x - hw) * (x - hw) + (y - hh) * (y - hh) > r * r) {
                    bitmap[x, y] = if (Math.random() < 0.7) Color.BLACK else Color.WHITE
                } else {
                    bitmap[x, y] = Color.WHITE
                }
            }
        }
        return bitmap
    }
}
