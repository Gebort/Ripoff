package com.example.ripoff
import android.graphics.Bitmap
import android.util.Log

class Animation(private val frames: List<Bitmap?>, private val delays: List<Float>, val callback: (Bitmap?) -> Unit, val callbackEnd: () -> Unit) {

    private var playing = false
    private var current = 0
    private var lastFrameTime: Long = 0

    fun play(){
        playing = true
        current = 0
        lastFrameTime = System.currentTimeMillis()
        callback(frames[current])
    }

    fun stop(){
        playing = false
    }

    fun update(fps: Long){
        if(playing) {
            if (current < frames.size-1) {
                    val time = System.currentTimeMillis()
                    val nextTime = lastFrameTime + 1000*delays[current].toLong()/fps
                    if (time > nextTime) {
                        lastFrameTime = System.currentTimeMillis()
                        current++
                        callback(frames[current])
                    }
            }
            else{
                stop()
                callbackEnd()
            }
        }
    }


}