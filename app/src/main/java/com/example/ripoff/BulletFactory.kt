package com.example.ripoff

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class BulletFactory(private val context: Context, private val screenX: Int, private val screenY: Int) {

    val bullets = mutableListOf<Bullet>()
    var bitmapBullet: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.bullet)

    private val width = screenX/48f
    private val height = screenY/24f

    private var speed = 40f

    init{
        bitmapBullet = Bitmap.createScaledBitmap(bitmapBullet,
                width.toInt() ,
                height.toInt() ,
                false)
    }

    fun createBullet(x: Float, y: Float, angle: Float){

        val newBullet = Bullet(
                context,
                bitmapBullet,
                width,
                height,
                screenX,
                screenY)

        newBullet.speedLin = speed
        newBullet.position.x = x
        newBullet.position.y = y
        newBullet.angle = angle

        bullets.add(newBullet)
    }

    fun update(fps: Long){

        val iterator = bullets.iterator()
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