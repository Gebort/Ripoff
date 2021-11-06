package com.example.ripoff

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs


class GameOver : Fragment() {

    private var score = 0
    private var record = 0

   private lateinit var restartBut: Button
   private lateinit var scoreText: TextView
   private lateinit var recordText: TextView
   private lateinit var newRecordText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args: GameOverArgs by navArgs()
        score = args.score

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        if (sharedPref != null) {
            record = sharedPref.getInt("record", 0)
            if (score > record){
                sharedPref.edit().putInt("record", score).apply()
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val i = inflater.inflate(R.layout.fragment_game_over, container, false)

        restartBut = i.findViewById(R.id.restartBut)
        scoreText = i.findViewById(R.id.scoreTextView)
        recordText = i.findViewById(R.id.recordTextView)
        newRecordText = i.findViewById(R.id.newRecodTextView)

        scoreText.text = "Score: $score"
        recordText.text = "Record: $record"

        if(score > record){
            newRecordText.visibility = View.VISIBLE

        }
        else{
            newRecordText.visibility = View.INVISIBLE
        }


        restartBut.setOnClickListener {
            val action = GameOverDirections.actionGameOverToGame()
            findNavController().navigate(action)
        }



        return i
    }


}