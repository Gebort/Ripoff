package com.example.ripoff

import android.content.Context
import android.graphics.*
import android.graphics.drawable.AnimationDrawable
import android.util.Log
import java.lang.Math.*
import kotlin.math.roundToInt

class Spaceship(context: Context, private val screenX: Int, private val screenY: Int) {

    var bitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.spaceship_idle)
    var bFactory: BulletFactory = BulletFactory(context, screenX, screenY)

    private val width = screenX/12f
    private val height = screenY/7f

    var angle = 90f
    var moving = false
    val stopped = 0
    val left = -1
    val right = 1
    var spinning = stopped
    var hitable = true
    var lives = 3

    private val accLin = 10f
    private val accLinStop = 25f
    private var speedLin = 0f
    private val speedLinMax = 15f
    private val accSpin = 10f
    private val accSpinStop = 35f
    private var speedSpin = 0f
    private val speedSpinMax = 7f
    private val invul = 200f
    private var fireDelay = 25f

    private var invulStart: Long = 0
    private var lastBullet: Long = 0
    private var leftBullet =  false

    private var thrustWidth = width/5
    private var thrustHeight = height/5

    val thrustLeftTop = MThrust(context, thrustWidth, thrustHeight)
    val thrustRightTop = MThrust(context, thrustWidth, thrustHeight)
    val thrustLeftBack = MThrust(context, thrustWidth, thrustHeight)
    val thrustRightBack = MThrust(context, thrustWidth, thrustHeight)


    val position = PointF(
            screenX/2f,
            screenY/2f
    )

    val borders = RectF(
            position.x - width/2,
            position.y - height/2,
            position.x + width/2,
            position.y + height/2
    )

    var animMoving: Animation
    var animStoping: Animation

    init{
        bitmap = Bitmap.createScaledBitmap(bitmap,
            width.toInt() ,
            height.toInt() ,
            false)
        animMoving = Animation(
                listOf(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.spaceship_go1),
                                width.toInt(),
                                height.toInt(),
                                false),
                        Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.spaceship_go2),
                                width.toInt() ,
                                height.toInt() ,
                                false),
                        Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.spaceship_go3),
                                width.toInt() ,
                                height.toInt() ,
                                false)
                ),
                listOf(6f,4f),
                ::changeBitmap,
                ::endAnim
                )
        animStoping = Animation(
                listOf(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.spaceship_go3),
                        width.toInt(),
                        height.toInt(),
                        false),
                        Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.spaceship_go2),
                                width.toInt() ,
                                height.toInt() ,
                                false),
                        Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, R.drawable.spaceship_idle),
                                width.toInt() ,
                                height.toInt() ,
                                false)
                ),
                listOf(2f, 2f),
                ::changeBitmap,
                ::endAnim
        )

        thrustLeftTop.angle = -angle
        thrustRightTop.angle = angle
        thrustLeftBack.angle = -angle
        thrustRightBack.angle = angle

    }

    fun update(fps: Long) {

        if(!hitable){
            var invulEnd = invulStart + (invul*1000/fps).toLong()
            if(System.currentTimeMillis() > invulEnd){
                hitable = true
            }
        }

        var nextBullet = lastBullet + (fireDelay*1000/fps).toLong()
        var time = System.currentTimeMillis()
        if(time > nextBullet){
            lastBullet = time
            shoot()
        }

        bFactory.update(fps)

        val accS = accSpin/fps
        val accL = accLin/fps
        val accSSt = accSpinStop/fps
        val accLSt = accLinStop/fps

        if (spinning == left){
            speedSpin -= accS
            if (speedSpin < -1*speedSpinMax){
                speedSpin = -1*speedSpinMax
            }
        }
        else if (spinning == right){
            speedSpin += accS
            if (speedSpin > speedSpinMax){
                speedSpin = speedSpinMax
            }
        }
        else if (spinning == stopped){
            if (speedSpin >= -1*accSSt && speedSpin <= accSSt)
                speedSpin = 0f
            else if(speedSpin < 0)
                speedSpin += accSSt
            else if(speedSpin > 0)
                speedSpin -= accSSt
        }

        angle += speedSpin

        if (moving){
            animMoving.update(fps)
            speedLin += accL
            if (speedLin > speedLinMax){
                speedLin = speedLinMax
            }
        }
        else{
            animStoping.update(fps)
            speedLin -= accLSt
            if(speedLin < 0){
                speedLin = 0f
            }
        }

        position.x = (position.x + speedLin * kotlin.math.sin(toRadians(angle.toDouble()))).toFloat()
        position.y = (position.y + speedLin * -1*kotlin.math.cos(toRadians(angle.toDouble()))).toFloat()

        if (position.x > screenX - width){
            position.x = screenX - width
        }
        else if (position.x < width){
            position.x = width
        }
        if (position.y > screenY - height){
            position.y = screenY - height
        }
        else if (position.y < height){
            position.y = height
        }

        updateThrustPos(fps, thrustLeftBack, true, false)
        updateThrustPos(fps, thrustLeftTop, true, true)
        updateThrustPos(fps, thrustRightBack, false, false)
        updateThrustPos(fps, thrustRightTop, false, true)

        borders.left = position.x - width/2
        borders.top = position.y - height/2
        borders.right = position.x + width/2
        borders.bottom =  position.y + height/2

    }

    private fun changeBitmap(newBitmap: Bitmap?){
        if (newBitmap != null) {
            bitmap = newBitmap
        }
    }

    private fun endAnim(){

    }

    private fun updateThrustPos(fps: Long, it: MThrust, left: Boolean, top: Boolean){
        it.update(fps)

        if(left){
            it.x =  position.x - 30f
        }
        else{
            it.x = position.x + 30f
        }

        if(top){
            it.y = position.y - 30f
        }
        else{
            it.y = position.y + 45f
        }


        it.borders.left = it.x - thrustWidth/2
        it.borders.top = it.y - thrustHeight/2
        it.borders.right = it.x + thrustWidth/2
        it.borders.bottom =  it.y + thrustHeight/2
    }

    fun hit(){
        lives--
        hitable = false
        invulStart = System.currentTimeMillis()
    }

    fun shoot(){
        if(leftBullet){
            bFactory.createBullet(
                    (position.x + 50 * kotlin.math.cos(toRadians(angle%360.toDouble()))).toFloat(),
                    (position.y + 50 * kotlin.math.sin(toRadians(angle%360.toDouble()))).toFloat(),
                    1*angle
                    )
            leftBullet = false
        }
        else{
            bFactory.createBullet(
                    (position.x - 50 * kotlin.math.sin(toRadians(90 - angle%360.toDouble()))).toFloat(),
                    (position.y - 50 * kotlin.math.cos(toRadians(90 - angle%360.toDouble()))).toFloat(),
                    1*angle
            )
            leftBullet = true
        }



    }

}