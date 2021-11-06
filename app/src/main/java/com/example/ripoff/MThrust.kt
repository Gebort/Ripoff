package com.example.ripoff

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF

class MThrust(context: Context, private val width: Float, private val height: Float) {

    var x = 0f
    var y = 0f
    var angle = 0f
    var bitmap: Bitmap? = null
    var working = false

    val borders = RectF(
            x - width/2,
            y - height/2,
            x + width/2,
            y + height/2
    )

    var animStart = Animation(
            listOf(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.spin1),
                    width.toInt(),
                    height.toInt(),
                    false),
                    Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.spin2),
                            width.toInt() ,
                            height.toInt() ,
                            false),
                    Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.spin3),
                            width.toInt() ,
                            height.toInt() ,
                            false)
            ),
            listOf(4f,4f),
            ::changeBitmap,
            ::endAnim
    )

    var animEnd = Animation(
            listOf(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.spin2),
                    width.toInt(),
                    height.toInt(),
                    false),
                    Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.spin1),
                            width.toInt() ,
                            height.toInt() ,
                            false),
                    null
            ),
            listOf(4f,4f),
            ::changeBitmap,
            ::endAnim
    )

    fun start(){
        animEnd.stop()
        animStart.play()
        working = true
    }

    fun stop(){
        animStart.stop()
        animEnd.play()
        working = false
    }

    fun changeBitmap(newBitmap: Bitmap?){
        bitmap = newBitmap
    }

    fun update(fps: Long){
        if (working)
            animStart.update(fps)
        else
            animEnd.update(fps)
    }

    fun endAnim(){

    }

}