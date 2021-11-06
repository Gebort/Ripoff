package com.example.ripoff

import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment

class Game : Fragment() {

    private var gameView: GameView? = null
    private lateinit var forwardBut: ImageButton
    private lateinit var leftBut: ImageButton
    private lateinit var rightBut: ImageButton
    private lateinit var pauseBut: ImageButton
    private lateinit var unpauseBut: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val size = Point()
        activity?.windowManager?.defaultDisplay?.getSize(size)

        gameView = activity?.let { GameView(it, size) }

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val i = inflater.inflate(R.layout.fragment_game, container, false)

        val layout: ConstraintLayout = i.findViewById(R.id.layout)
        layout.addView(gameView)

        forwardBut = i.findViewById(R.id.butForw)
        leftBut = i.findViewById(R.id.butLeft)
        rightBut = i.findViewById(R.id.butRight)
        pauseBut = i.findViewById(R.id.butPause)
        unpauseBut = i.findViewById(R.id.butUnpause)

        forwardBut.bringToFront()
        leftBut.bringToFront()
        rightBut.bringToFront()
        pauseBut.bringToFront()

        unpauseBut.isEnabled = false

        forwardBut.setOnTouchListener { _, event ->
            if(event.action == MotionEvent.ACTION_DOWN){
                gameView!!.moveForward()
            }
            else if(event.action == MotionEvent.ACTION_UP){
                gameView!!.stopMovingForward()
            }
            return@setOnTouchListener false
        }

        leftBut.setOnTouchListener { _, event ->
            if(event.action == MotionEvent.ACTION_DOWN){
                gameView!!.spinLeft()
            }
            else if(event.action == MotionEvent.ACTION_UP){
                gameView!!.stopSpin()
            }
            return@setOnTouchListener false
        }

        rightBut.setOnTouchListener { _, event ->
            if(event.action == MotionEvent.ACTION_DOWN){
                gameView!!.spinRight()
            }
            else if(event.action == MotionEvent.ACTION_UP){
                gameView!!.stopSpin()
            }
            return@setOnTouchListener false
        }

        pauseBut.setOnClickListener {
            pauseGame()
        }

        unpauseBut.setOnClickListener{
            unpauseGame()
        }

        return i
    }

    override fun onResume() {
        super.onResume()
        gameView?.start()
    }

    override fun onPause() {
        super.onPause()
        gameView?.stop()
    }

    fun pauseGame(){
        gameView!!.stop()
        unpauseBut.isEnabled = true
        leftBut.isClickable = false
        rightBut.isClickable = false
        forwardBut.isClickable = false
        pauseBut.isClickable = false
    }

    fun unpauseGame(){
        gameView!!.start()
        pauseBut.isClickable = true
        unpauseBut.isEnabled = false
        leftBut.isClickable = true
        rightBut.isClickable = true
        forwardBut.isClickable = true

    }

}