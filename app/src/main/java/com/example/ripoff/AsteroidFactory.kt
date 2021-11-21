package com.example.ripoff

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.util.*

class AsteroidFactory(private val context: Context, private val screenX: Int, private val screenY: Int) {

    val asteroids = mutableListOf<Asteroid>()
    var bitmaps: List<Bitmap> = listOf()

    private val width = screenX/12f
    private val height = screenY/7f

    private val speedMin = 1f
    private val speedMax = 5f
    private val linToSpin = 15f

    val left = 0
    val top = 1
    val right = 2
    val bottom = 3

    val r = Random()

    init{
        bitmaps = listOf(
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.asteroid1),
                width.toInt(),
                height.toInt(),
                false)
        )
    }

    fun createAsteroid(){
        val bitmapNumber = (bitmaps.indices).random()
        val speed = speedMin + r.nextFloat() * (speedMax - speedMin)
        val side = (0..3).random()
        var x = 0f
        var y = 0f
        var angle = 0f

        if(side == left){
            x = 0 - width
            y = r.nextFloat() * screenY
            angle = (45..135).random().toFloat()
        }
        if(side == top){
            x = r.nextFloat() * screenX
            y = 0 - height
            angle = (-225..-135).random().toFloat()
        }
        if(side == right){
            x = screenX + width
            y = r.nextFloat() * screenY
            angle = (-135..-45).random().toFloat()

        }
        if(side == bottom){
            x = r.nextFloat() * screenX
            y = screenY + height
            angle = (-45..45).random().toFloat()
        }

        val newAsteroid = Asteroid(
                bitmaps[bitmapNumber],
                width,
                height,
        )

        newAsteroid.speedLin = speed
        newAsteroid.speedSpin = speed*linToSpin
        newAsteroid.position.x = x
        newAsteroid.position.y = y
        newAsteroid.angle = angle

        asteroids.add(newAsteroid)
    }

    fun update(fps: Long){

        val iterator = asteroids.iterator()
        while(iterator.hasNext()){
            val it = iterator.next()
            it.update(fps)
            if (it.position.x > screenX + 2*it.width){
                iterator.remove()
            }
            else if (it.position.x < -2*it.width){
                iterator.remove()
            }
            else if (it.position.y > screenY + 2*it.height){
                iterator.remove()
            }
            else if (it.position.y < -2*it.height){
                iterator.remove()
            }
        }
    }
}