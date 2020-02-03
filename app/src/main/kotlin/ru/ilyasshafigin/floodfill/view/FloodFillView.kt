package ru.ilyasshafigin.floodfill.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class FloodFillView : View {

    private val controller = FloodFillController()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
        super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
        super(context, attrs, defStyleAttr, defStyleRes)

    fun setField(bitmap: Bitmap) {
        controller.setField(bitmap)
        requestLayout()
    }

    fun setSpeed(speed: Int) {
        controller.setSpeed(speed)
    }

    fun setAlgorithm(@FloodFillAlgorithmType algorithmType: Int) {
        controller.setAlgorithm(algorithmType)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!controller.canTouch()) {
            return true
        }

        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (controller.onTap(x.toInt(), y.toInt())) {
                    invalidate()
                }
            }
        }
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        controller.onSizeChanged(measuredWidth, measuredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (controller.onDraw(canvas)) {
            invalidate()
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        return SavedState(super.onSaveInstanceState()).apply {
            controller.onSave(this)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            controller.onRestore(state)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    internal class SavedState : BaseSavedState {

        var bitmap: Bitmap? = null
        var itersPerFrame: Int = 0
        @FloodFillAlgorithmType
        var algorithmType: Int = FloodFillAlgorithmType.DEFAULT_ALGORITHM

        constructor(superState: Parcelable?) : super(superState)

        private constructor(parcel: Parcel) : super(parcel) {
            bitmap = parcel.readParcelable(Bitmap::class.java.classLoader)
            itersPerFrame = parcel.readInt()
            algorithmType = parcel.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeParcelable(bitmap, flags)
            out.writeInt(itersPerFrame)
            out.writeInt(algorithmType)
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {

            override fun createFromParcel(parcel: Parcel): SavedState = SavedState(parcel)

            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }
    }
}