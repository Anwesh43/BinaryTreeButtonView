package com.anwesome.ui.binarytreebutton

import android.content.Context
import android.view.*
import android.graphics.*
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Created by anweshmishra on 09/10/17.
 */
class BinaryTreeButtonView(ctx:Context,var maxN:Int=4):View(ctx) {
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
    data class BinaryTreeButton(var x:Float,var y:Float,var r:Float,var listener:()->Unit,var state:BinaryTreeState = BinaryTreeState()) {
        var left:BinaryTreeButton?=null
        var right:BinaryTreeButton?=null
        fun draw(canvas:Canvas,paint:Paint) {
            canvas.save()
            canvas.translate(x,y)
            paint.color = Color.GREEN
            canvas.drawCircle(0f,0f,r,paint)
            var color = Color.GRAY
            paint.color = Color.argb(120,Color.red(color),Color.green(color),Color.blue(color))
            canvas.save()
            canvas.scale(state.scale,state.scale)
            canvas.drawCircle(0f,0f,r,paint)
            canvas.restore()
            canvas.restore()
        }
        fun addLeft(newListener:()->Unit,h:Float) {
            left = BinaryTreeButton(x-2*r,y+h,r,newListener)
        }
        fun addRight(newListener:()->Unit,h:Float) {
            right = BinaryTreeButton(x+2*r,y+h,r,newListener)
        }
        fun update() {
            state.update()
        }
        fun stopped():Boolean {
            val condition = state.stopped()
            if(condition) {
                listener.invoke()
            }
            return condition
        }
        fun handleTap(x:Float,y:Float):Boolean = x>=this.x-r && x<=this.x+r && y>=this.y-r && y<=this.y+r
    }
    data class BinaryTreeState(var scale:Float = 0f,var deg:Float = 0f) {
        fun update() {
            scale = Math.sin(deg*Math.PI/180).toFloat()
            deg += 4.5f
            if(deg > 180f) {
                deg = 0f
            }
        }
        fun stopped():Boolean = deg == 0f
    }
    data class BinaryTree(var w:Float,var h:Float,var listeners:LinkedList<()->Unit>,var maxN:Int,var btns:ConcurrentLinkedQueue<BinaryTreeButton> = ConcurrentLinkedQueue()) {
        var tappedBtns:ConcurrentLinkedQueue<BinaryTreeButton> = ConcurrentLinkedQueue()
        init {
            if(listeners.size > 0) {
                var gap = (w/(2*(Math.pow(2.0,maxN.toDouble()).toFloat()-1)+1))
                var hGap = h/(maxN+1)
                var root = BinaryTreeButton(w/2,hGap,gap/2,listeners[0])
                listeners.removeAt(0)
                var queue = ArrayList<BinaryTreeButton>()
                btns.add(root)
                queue.add(root)
                while(listeners.size > 0) {
                    val tree = queue[0]
                    tree.addLeft(listeners[0],hGap)
                    queue.add(tree.left?:root)
                    btns.add(tree.left)
                    listeners.removeAt(0)
                    if(listeners.size > 1) {
                        tree.addRight(listeners[0],hGap)
                        listeners.removeAt(0)
                        btns.add(tree.right)
                        queue.add(tree.right?:root)
                    }
                    queue.removeAt(0)
                }
            }
        }
        fun draw(canvas:Canvas,paint:Paint) {
            btns.forEach { btn ->
                btn.draw(canvas,paint)
            }
        }
        fun update(stopCb:()->Unit) {
            tappedBtns.forEach { tappedBtn ->
                tappedBtn.update()
                if(tappedBtn.stopped()) {
                    tappedBtns.remove(tappedBtn)
                    if(tappedBtns.size == 0) {
                        stopCb()
                    }
                }
            }
        }
        fun handleTap(x:Float,y:Float,startCb:()->Unit) {
            btns.forEach{ btn ->
                if(btn.handleTap(x,y)) {
                    tappedBtns.add(btn)
                    if(tappedBtns.size == 1){
                        startCb()
                    }
                }
            }
        }
    }
}