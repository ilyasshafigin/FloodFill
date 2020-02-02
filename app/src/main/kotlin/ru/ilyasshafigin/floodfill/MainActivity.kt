package ru.ilyasshafigin.floodfill

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.set
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val defaultWidth = 64
        val defaultHeight = 64

        with(widthEdit) {
            setText(defaultWidth.toString(), TextView.BufferType.EDITABLE)
        }

        with(heightEdit) {
            setText(defaultHeight.toString(), TextView.BufferType.EDITABLE)
        }

        with(generateButton) {
            setOnClickListener {
                val width = widthEdit.text.toString().toIntOrNull() ?: 1
                val height = heightEdit.text.toString().toIntOrNull() ?: 1
                floodFillView.setField(createRandomBitmap(width, height))
            }
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
}
