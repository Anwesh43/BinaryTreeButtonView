package com.anwesome.ui.binarytreebutton

import android.content.Context
import android.view.*
import android.graphics.*
/**
 * Created by anweshmishra on 09/10/17.
 */
class BinaryTreeButtonView(ctx:Context,var maxN:Int=4,var n:Int = 0):View(ctx) {
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
    data class BinaryTreeButton(var x:Float,var y:Float,var r:Float,var listener:()->Unit) {
        fun draw(canvas:Canvas,paint:Paint) {
            canvas.save()
            canvas.translate(x,y)
            paint.color = Color.GREEN
            canvas.drawCircle(0f,0f,r,paint)
            var color = Color.GRAY
            paint.color = Color.argb(120,Color.red(color),Color.green(color),Color.blue(color))
            canvas.save()
            canvas.scale(1f,1f)
            canvas.drawCircle(0f,0f,r,paint)
            canvas.restore()
            canvas.restore()
        }
        fun update() {

        }
        fun stopped():Boolean = true
        fun startUpdating() {

        }
        fun handleTap(x:Float,y:Float):Boolean = x>=this.x-r && x<=this.x+r && y>=this.y-r && y<=this.y+r
    }
}