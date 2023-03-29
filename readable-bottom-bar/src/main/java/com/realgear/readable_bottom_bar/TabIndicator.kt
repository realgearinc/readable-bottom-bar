package com.realgear.readable_bottom_bar

import android.animation.ObjectAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.util.FloatProperty
import android.util.Log
import android.util.Property
import androidx.recyclerview.widget.RecyclerView
import com.realgear.readable_bottom_bar.utils.fixDurationScale

internal class TabIndicator(
    private val bottomBar: ReadableBottomBar,
    private val parent: RecyclerView,
    private val adapter: TabAdapter
) : RecyclerView.ItemDecoration() {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val animator = ObjectAnimator().apply {
        target = this@TabIndicator
        setProperty(CURRENT_LEFT_PROPERTY)
        fixDurationScale()
    }

    private var lastSelectedIndex: Int = RecyclerView.NO_POSITION
    private var currentLeft: Float = 0f

    private val TAG : String = javaClass.simpleName

    private val indicatorRect = RectF()

    private var currentOffset: Float = 0.0F
    private var isSettling : Boolean = false

    var enableAnimation : Boolean = true

    private val shouldRender: Boolean
        get() = bottomBar.indicatorStyle.indicatorAppearance != ReadableBottomBar.IndicatorAppearance.INVISIBLE

    init {
        applyStyle()
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (adapter.selectedIndex == RecyclerView.NO_POSITION || !shouldRender) {
            return
        }

        val isAnimating = animator.isRunning
        val animatedFraction = animator.animatedFraction
        val lastView = parent.getChildAt(lastSelectedIndex)
        val newView = parent.getChildAt(adapter.selectedIndex) ?: return

        val newViewWidth = newView.width.toFloat()
        val newViewLeft = newView.left.toFloat()

        var currentWidth = newViewWidth

        when (bottomBar.indicatorAnimation) {
            ReadableBottomBar.IndicatorAnimation.SLIDE -> {
                if (isAnimating && lastView != null) {
                    val lastViewWidth = lastView.width.toFloat()
                    currentWidth =
                        lastViewWidth + (newViewWidth - lastViewWidth) * animatedFraction
                } else {
                    currentLeft = newViewLeft
                }

                drawIndicator(c, currentLeft, currentWidth)
            }
            ReadableBottomBar.IndicatorAnimation.FADE -> {
                if (isAnimating && lastView != null) {
                    val newAlpha = 255f * animatedFraction
                    val lastAlpha = 255f - newAlpha

                    drawIndicator(
                        c,
                        lastView.left.toFloat(),
                        lastView.width.toFloat(),
                        lastAlpha.toInt()
                    )
                    drawIndicator(
                        c,
                        newViewLeft,
                        newViewWidth,
                        newAlpha.toInt()
                    )
                } else {
                    drawIndicator(c, newViewLeft, newViewWidth)
                }
            }
            else -> {
                drawIndicator(c, newViewLeft, newViewWidth)
            }
        }
    }

    private fun drawIndicator(c: Canvas, viewLeft: Float, viewWidth: Float, alpha: Int = 255) {
        val indicatorMargin = bottomBar.indicatorStyle.indicatorMargin
        paint.alpha = alpha

        var indicatorLeft : Float = 0F
        var indicatorRight : Float = 0F

        if (isSettling) {
            indicatorLeft = viewLeft + indicatorMargin
            indicatorRight = (viewLeft + viewWidth) - indicatorMargin
        }
        else {
            currentOffset = currentOffset - adapter.selectedIndex
            indicatorLeft = (viewLeft + (viewWidth * (currentOffset))) + indicatorMargin
            indicatorRight = ((viewLeft + viewWidth) + (viewWidth * currentOffset)) - indicatorMargin
        }

        val indicatorHeight =  bottomBar.indicatorStyle.indicatorHeight.toFloat()

        when(bottomBar.indicatorStyle.indicatorAppearance) {
            ReadableBottomBar.IndicatorAppearance.SQUARE -> {
                val top: Float
                val bottom: Float

                when(bottomBar.indicatorStyle.indicatorLocation) {
                    ReadableBottomBar.IndicatorLocation.TOP -> {
                        top = 0f
                        bottom = indicatorHeight
                    }
                    ReadableBottomBar.IndicatorLocation.BOTTOM -> {
                        val parentHeight = parent.height.toFloat()
                        top = parentHeight - indicatorHeight
                        bottom = parentHeight
                    }
                }

                c.drawRect(indicatorLeft, top, indicatorRight, bottom, paint)
            }
            ReadableBottomBar.IndicatorAppearance.ROUND -> {
                // Canvas.drawRoundRect draws rectangle with all round corners.
                // To make bottom corners round, we can draw rectangle still with all round corners,
                // but hide top round corners by translating the rectangle to top for radius
                // (rx, ry arguments in Canvas.drawRoundRect).
                // In the same way, we can make top corners round, but we have to translate to bottom

                val top: Float
                val bottom: Float

                when(bottomBar.indicatorStyle.indicatorLocation) {
                    ReadableBottomBar.IndicatorLocation.TOP -> {
                        when(bottomBar.indicatorStyle.indicatorRectStyle) {
                            ReadableBottomBar.IndicatorStyle.NORMAL -> {
                                top = 0.0F
                                bottom = indicatorHeight
                            }
                            ReadableBottomBar.IndicatorStyle.HALVED -> {
                                top = -(indicatorHeight / 2)
                                bottom = (indicatorHeight / 2)
                            }
                        }

                    }
                    ReadableBottomBar.IndicatorLocation.BOTTOM -> {
                        val parentHeight = parent.height.toFloat()
                        when(bottomBar.indicatorStyle.indicatorRectStyle) {
                            ReadableBottomBar.IndicatorStyle.NORMAL -> {
                                top = parentHeight
                                bottom = indicatorHeight
                            }
                            ReadableBottomBar.IndicatorStyle.HALVED -> {
                                top = parentHeight - (indicatorHeight / 2)
                                bottom = parentHeight + (indicatorHeight / 2)
                            }
                        }
                    }
                }

                // The reason of using RectF is that Canvas.drawRoundRect(RectF, float, float) is available
                // only since API 21
                indicatorRect.set(indicatorLeft, top, indicatorRight, bottom)

                c.drawRoundRect(indicatorRect, indicatorHeight, indicatorHeight, paint)
            }
            else -> {
            }
        }
    }

    fun setSelectedIndex(lastIndex: Int, newIndex: Int, animate: Boolean) {
        if (animator.isRunning) {
            animator.cancel()
        }

        if (!shouldRender) {
            return
        }

        val newView = parent.getChildAt(newIndex)
        if (!animate || lastIndex == -1 || newView == null) {
            parent.invalidate()
            return
        }

        if (enableAnimation) {
            lastSelectedIndex = lastIndex

            animator.run {
                setFloatValues(currentLeft, newView.left.toFloat())
                duration = bottomBar.tabStyle.animationDuration.toLong()
                interpolator = bottomBar.tabStyle.animationInterpolator

                start()
            }
        }
    }

    fun setIndicatorOffset(offset : Float, settling : Boolean = false) {
        currentOffset = offset
        isSettling = settling
        parent.invalidate()
    }

    fun applyStyle() {
        paint.color = bottomBar.indicatorStyle.indicatorColor

        if (shouldRender) {
            parent.invalidate()
        }
    }

    companion object {
        private val CURRENT_LEFT_PROPERTY = if(Build.VERSION.SDK_INT >= 24) {
            object: FloatProperty<TabIndicator>("currentLeft") {
                override fun get(o: TabIndicator): Float = o.currentLeft
                override fun setValue(o: TabIndicator, value: Float) {
                    o.currentLeft = value
                    o.parent.invalidate()
                }
            }
        } else {
            object: Property<TabIndicator, Float>(Float::class.java, "currentLeft") {
                override fun get(o: TabIndicator): Float = o.currentLeft
                override fun set(o: TabIndicator, value: Float) {
                    o.currentLeft = value
                    o.parent.invalidate()
                }
            }
        }
    }
}