package com.example.ripoff

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.view.SurfaceView
import androidx.fragment.app.findFragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.logging.Handler
import java.util.logging.LogRecord

class GameView(context: Context, private val size: Point): SurfaceView(context), Runnable {

    private var gameThread = Thread(this)
    private var playing = false

    private var paused = true
    private var restart = false

    private var canvas: Canvas = Canvas()
    private val paint: Paint = Paint()

    private var spaceship: Spaceship = Spaceship(context, size.x, size.y)

    private var factory: AsteroidFactory = AsteroidFactory(context, size.x, size.y)
    private val asteroidDelay = 100f
    private var asteroidDelayCurr = asteroidDelay
    private var asteroidDelayMin = 15f
    private var asteroidGrowing = 2

    private var explFactory: ExplosionFactory = ExplosionFactory(context, size.x, size.y)

    private var score = 0

    private val backgroundColor = Color.rgb(0, 0, 0)
    private val textColor = Color.rgb(255, 255, 255)
    private val scoreTextSize = 150f
    private val livesTextSize = 60f

    private fun prepareLevel(){

    }

    fun moveForward(){
        if (!spaceship.moving){
                spaceship.moving = true
                spaceship.animMoving.play()
                spaceship.animStoping.stop()
        }
    }

    fun stopMovingForward(){
        if (spaceship.moving){
                spaceship.moving = false
                spaceship.animMoving.stop()
                spaceship.animStoping.play()
        }
    }

    fun spinLeft(){
        if (spaceship.spinning == spaceship.right){
            spaceship.thrustRightBack.stop()
            spaceship.thrustLeftTop.stop()
        }
        spaceship.spinning = spaceship.left
        spaceship.thrustLeftBack.start()
        spaceship.thrustRightTop.start()
    }

    fun spinRight(){
        if (spaceship.spinning == spaceship.left){
            spaceship.thrustLeftBack.stop()
            spaceship.thrustRightTop.stop()
        }
        spaceship.spinning = spaceship.right
        spaceship.thrustRightBack.start()
        spaceship.thrustLeftTop.start()
    }

    fun stopSpin(){
        spaceship.spinning = spaceship.stopped
        spaceship.thrustRightBack.stop()
        spaceship.thrustLeftTop.stop()
        spaceship.thrustLeftBack.stop()
        spaceship.thrustRightTop.stop()
    }

    override fun run() {
        var fps: Long = 1
        var lastAsteroid = System.currentTimeMillis()

        while (playing){

            if(restart){
                restartGame()
            }

            val startFrameTime = System.currentTimeMillis()

            if(!paused){
                val time = System.currentTimeMillis()
                var nextAsteroid = lastAsteroid + (1000*asteroidDelayCurr/fps).toLong()
                if (time > nextAsteroid) {
                    if(asteroidDelayCurr >= asteroidDelayMin)
                        asteroidDelayCurr -= asteroidGrowing
                    lastAsteroid = time
                    factory.createAsteroid()
                }

                detectCollision()
                update(fps)
            }

            draw(fps)

            val timeThisFrame = System.currentTimeMillis()  - startFrameTime
            if(timeThisFrame >= 1){
                fps = 1000/timeThisFrame
            }
        }
    }

    private fun update(fps: Long){
        spaceship.update(fps)
        factory.update(fps)
        explFactory.update(fps)
    }

    private fun detectCollision(){
        val iterator = factory.asteroids.iterator()
        while(iterator.hasNext()) {
            val it = iterator.next()
            if (it.borders.intersect(spaceship.borders)) {
                if (spaceship.hitable) {
                    hurtSpaceship()
                    iterator.remove()
                    explFactory.createExplosion(it.position.x, it.position.y, it.angle)

                    continue
                }
            }

            val bIterator = spaceship.bFactory.bullets.iterator()
            while (bIterator.hasNext()) {
                val bullet = bIterator.next()
                if (it.borders.intersect(bullet.borders)) {
                    iterator.remove()
                    bIterator.remove()
                    explFactory.createExplosion(it.position.x, it.position.y, it.angle)
                    score++
                    break
                }
            }
        }
    }

    private fun draw(fps: Long){
        if (holder.surface.isValid){
            canvas = holder.lockCanvas()

            canvas.drawColor(backgroundColor)

            val drawThrust = { it: MThrust ->
                if(it.bitmap != null){
                    canvas.save()
                    canvas.rotate(it.angle, it.x, it.y)
                    canvas.drawBitmap(it.bitmap!!, it.borders.left, it.borders.top, paint)
                    canvas.restore()
                }
            }

            //РИСОВАНИЕ ИГРОВЫХ ОБЪЕКТОВ
            canvas.save() //Saving the canvas and later restoring it so only this image will be rotated.
            canvas.rotate(spaceship.angle, spaceship.position.x, spaceship.position.y)
            canvas.drawBitmap(spaceship.bitmap, spaceship.borders.left, spaceship.borders.top, paint)
            drawThrust(spaceship.thrustLeftBack)
            drawThrust(spaceship.thrustLeftTop)
            drawThrust(spaceship.thrustRightBack)
            drawThrust(spaceship.thrustRightTop)
            canvas.restore()

            val iterator = factory.asteroids.iterator()
            while(iterator.hasNext()){
                val it = iterator.next()
                canvas.save()
                canvas.rotate(it.spin, it.position.x, it.position.y)
                canvas.drawBitmap(it.bitmap, it.borders.left, it.borders.top, paint)
                canvas.restore()

            }

            val bIterator = spaceship.bFactory.bullets.iterator()
            while(bIterator.hasNext()){
                val it = bIterator.next()
                canvas.save()
                canvas.rotate(it.angle, it.position.x, it.position.y)
                canvas.drawBitmap(it.bitmap, it.borders.left, it.borders.top, paint)
                canvas.restore()
            }

            val explIterator = explFactory.explosions.iterator()
            while(explIterator.hasNext()){
                val it = explIterator.next()
                canvas.save()
                canvas.rotate(it.angle, it.position.x, it.position.y)
                canvas.drawBitmap(it.bitmap, it.borders.left, it.borders.top, paint)
                canvas.restore()
            }

            //РИСОВАНИЕ НАДПИСЕЙ
            paint.color = textColor
            paint.textSize = 30f
            canvas.drawText("$fps", 30f, 30f, paint)
            paint.textSize = scoreTextSize
            paint.textAlign = Paint.Align.CENTER;
            canvas.drawText("$score", size.x/2f, scoreTextSize+10f, paint)
            paint.textSize = livesTextSize
            canvas.drawText("Lives: ${spaceship.lives}", size.x/2f, size.y-livesTextSize-80f, paint)



            holder.unlockCanvasAndPost(canvas)
        }
    }

    fun stop(){
        playing = false
        paused = true

        try {
            gameThread.join()
        } catch (e: InterruptedException){
        }
    }

    fun start(){
        gameThread = Thread(this)
        playing = true
        paused = false
        prepareLevel()
        gameThread.start()

    }

    fun hurtSpaceship(){
        spaceship.hit()
        if (spaceship.lives < 1){
            restart = true
        }

    }

    fun restartGame(){
        GlobalScope.launch(Dispatchers.Main) {
            stop()
            val action = GameDirections.actionGameToGameOver(score)
            findNavController().navigate(action)
        }
    }

}