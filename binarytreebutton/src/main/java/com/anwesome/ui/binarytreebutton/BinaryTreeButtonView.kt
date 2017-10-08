package com.anwesome.ui.binarytreebutton

import android.content.Context
import android.view.*
import android.graphics.*
/**
 * Created by anweshmishra on 09/10/17.
 */
class BinaryTreeButtonView(ctx:Context,var n:Int = 0):View(ctx) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    override fun onDraw(canvas:Canvas) {

    }
    override fun onTouchEvent(event:MotionEvent):Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}