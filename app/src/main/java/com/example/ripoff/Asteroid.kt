package com.example.ripoff

import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.RectF

class Asteroid(var bitmap: Bitmap,
               val width: Float,
               val height: Float,
                ) {

    var speedLin: Float = 0f
    var speedSpin: Float = 0f
    var angle: Float = 0f
    var spin: Float = 0f

    val position = PointF(
            -500f,
            -500f
    )

    val borders = RectF(
            position.x - width/2,
            position.y - height/2,
            position.x + width/2,
            position.y + height/2
    )

    fun update(fps: Long){
        spin += speedSpin/fps

        position.x = (position.x + speedLin * kotlin.math.sin(Math.toRadians(angle.toDouble()))).toFloat()
        position.y = (position.y + speedLin * -1*kotlin.math.cos(Math.toRadians(angle.toDouble()))).toFloat()

        borders.left = position.x - width/2
        borders.top = position.y - height/2
        borders.right = position.x + width/2
        borders.bottom =  position.y + height/2
    }

}