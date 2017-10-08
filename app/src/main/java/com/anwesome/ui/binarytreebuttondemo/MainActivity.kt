package com.anwesome.ui.binarytreebuttondemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.anwesome.ui.binarytreebutton.BinaryTreeButtonView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BinaryTreeButtonView.create(this)
        addListeners()
        BinaryTreeButtonView.show()

    }
    private fun addListeners() {
        for(i in 1..15) {
            BinaryTreeButtonView.addListener { Toast.makeText(this,"${i} clicked",Toast.LENGTH_SHORT).show()}
        }
    }
}
