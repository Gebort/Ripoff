package com.example.ripoff

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class ExplosionFactory(private val context: Context, private val screenX: Int, private val screenY: Int) {

    val explosions = mutableListOf<Explosion>()

    private val width = screenX/12f
    private val height = screenY/7f

    private val framesAsteroid1: List<Bitmap> = listOf(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.asteroid1_blast1),
            width.toInt(),
            height.toInt(),
            false),
            Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.asteroid1_blast2),
                    width.toInt() ,
                    height.toInt() ,
                    false),
            Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.asteroid1_blast3),
                    width.toInt() ,
                    height.toInt() ,
                    false),
            Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.asteroid1_blast4),
                    width.toInt() ,
                    height.toInt() ,
                    false)
    )

    private val delaysAsteroid1 =  listOf(4f, 4f, 4f)

    fun createExplosion(x: Float, y: Float, angle: Float){

        val newExplosion = Explosion(
                context,
                width,
                height,
                x,
                y,
                angle,
                framesAsteroid1,
                delaysAsteroid1
                )

        explosions.add(newExplosion)
    }

    fun update(fps: Long){

        val iterator = explosions.iterator()
        while(iterator.hasNext()){
            val it = iterator.next()
            if(!it.done){
                it.update(fps)
            }
            else{
                iterator.remove()
            }

        }
    }
}