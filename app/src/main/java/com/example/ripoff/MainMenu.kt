package com.example.ripoff

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController

class MainMenu : Fragment() {

    private lateinit var playBut: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val i = inflater.inflate(R.layout.fragment_main_menu, container, false)

        playBut = i.findViewById(R.id.butPlay)

        playBut.setOnClickListener {
            val action = MainMenuDirections.actionMainMenuToGame()
            findNavController().navigate(action)
        }
        return i
    }
}