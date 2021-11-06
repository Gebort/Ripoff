package com.example.ripoff

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.graphics.RectF

class Explosion(context: Context,
                val width: Float,
                val height: Float,
                val x: Float,
                val y: Float,
                val angle: Float,
                private val frames: List<Bitmap>,
                private val delays: List<Float>
                ) {

    var bitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.asteroid1)
    var done = false

    val animExpl: Animation

    val position = PointF(
            x, y
    )

    val borders = RectF(
            position.x - width / 2,
            position.y - height / 2,
            position.x + width / 2,
            position.y + height / 2
    )

    init {
        bitmap = Bitmap.createScaledBitmap(bitmap,
                width.toInt(),
                height.toInt(),
                false)
        animExpl = Animation(
                frames,
                delays,
                ::changeBitmap,
                ::endAnim
        )
        animExpl.play()
    }

    fun update(fps: Long) {
        animExpl.update(fps)
    }

    fun changeBitmap(newBitmap: Bitmap?){
        if (newBitmap != null) {
            bitmap = newBitmap
        }
    }

    fun endAnim(){
        done = true
    }


}