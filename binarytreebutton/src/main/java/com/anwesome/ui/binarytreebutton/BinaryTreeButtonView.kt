package com.anwesome.ui.binarytreebutton

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.view.*
import android.graphics.*
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Created by anweshmishra on 09/10/17.
 */
class BinaryTreeButtonView(ctx:Context,var maxN:Int=4,var listenerers:LinkedList<()->Unit> = LinkedList()):View(ctx) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val renderer = BinaryTreeRenderer(this)
    override fun onDraw(canvas:Canvas) {
        canvas.drawColor(Color.parseColor("#212121"))
        renderer.render(canvas,paint)
    }
    override fun onTouchEvent(event:MotionEvent):Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap(event.x,event.y)
            }
        }
        return true
    }
    data class BinaryTreeButton(var x:Float,var y:Float,var gap:Float,var r:Float,var listener:()->Unit,var state:BinaryTreeState = BinaryTreeState()) {
        var left:BinaryTreeButton?=null
        var right:BinaryTreeButton?=null
        fun draw(canvas:Canvas,paint:Paint) {
            canvas.save()
            canvas.translate(x,y)
            paint.color = Color.parseColor("#00E676")
            canvas.drawCircle(0f,0f,r,paint)
            drawEdges(canvas,paint,1f)
            var color = Color.parseColor("#ef5350")
            paint.color = Color.argb(200,Color.red(color),Color.green(color),Color.blue(color))
            canvas.save()
            canvas.scale(state.scale,state.scale)
            canvas.drawCircle(0f,0f,r,paint)
            canvas.restore()
            drawEdges(canvas,paint,state.scale)
            canvas.restore()
        }
        private fun drawEdges(canvas:Canvas,paint:Paint,scale:Float) {
            canvas.drawLine(0f,0f,((left?.x?:x)-x)*scale,((left?.y?:y)-y)*scale,paint)
            canvas.drawLine(0f,0f,((right?.x?:x)-x)*scale,((right?.y?:y)-y)*scale,paint)
        }
        fun addLeft(newListener:()->Unit,h:Float) {
            left = BinaryTreeButton(x-gap,y+h,gap*0.7f,r,newListener)
        }
        fun addRight(newListener:()->Unit,h:Float) {
            right = BinaryTreeButton(x+gap,y+h,gap*0.7f,r,newListener)
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
            deg += 9f
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
                var gap = w/(Math.pow(2.0,maxN-1.0)-1).toFloat()
                var hGap = h/(maxN+1)
                var root = BinaryTreeButton(w/2,gap/5,1.5f*gap,gap/5,listeners[0])
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
                    if(listeners.size > 0) {
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
    class BinaryTreeAnimator(var binaryTree:BinaryTree,var view:BinaryTreeButtonView,var animated:Boolean = false) {
        fun update() {
            if(animated) {
                binaryTree.update{animated = false}
                try {
                    Thread.sleep(50)
                    view.invalidate()
                }
                catch(ex:Exception) {

                }
            }
        }
        fun draw(canvas:Canvas,paint:Paint) {
            binaryTree.draw(canvas,paint)
        }
        fun handleTap(x:Float,y:Float) {
            binaryTree.handleTap(x,y,{
                animated = true
                view.postInvalidate()
            })
        }
    }
    class BinaryTreeRenderer(var view:BinaryTreeButtonView,var time:Int = 0) {
        var animator:BinaryTreeAnimator?=null
        fun render(canvas:Canvas,paint:Paint) {
            if(time == 0) {
                val w = canvas.width.toFloat()
                val h = canvas.height.toFloat()
                var binaryTree = BinaryTree(w,h,view.listenerers,view.maxN)
                animator = BinaryTreeAnimator(binaryTree,view)
                paint.strokeWidth = Math.min(w,h)/60
                paint.strokeCap = Paint.Cap.ROUND
            }
            animator?.draw(canvas,paint)
            animator?.update()
            time++
        }
        fun handleTap(x:Float,y:Float) {
            animator?.handleTap(x,y)
        }
    }
    companion object{
        var view:BinaryTreeButtonView?=null
        var added = false
        var parent:Activity?=null
        fun create(activity:Activity,vararg maxN:Int) {
            if(!added) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                view = BinaryTreeButtonView(activity)
                if(maxN.size == 1) {
                    view?.maxN = maxN[0]
                }
                parent = activity
            }
        }
        fun addListener(listener:()->Unit) {
            if(!added) {
                view?.listenerers?.add(listener)
            }
        }
        fun show() {
            if(!added) {
                parent?.setContentView(view)
                added = true
            }
        }
    }
}