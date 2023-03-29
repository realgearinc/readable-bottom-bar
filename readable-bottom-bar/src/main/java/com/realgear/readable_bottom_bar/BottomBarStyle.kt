package com.realgear.readable_bottom_bar

import android.graphics.Color
import android.graphics.Typeface
import android.view.animation.Interpolator
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.StyleRes
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.realgear.readable_bottom_bar.utils.*

object BottomBarStyle {
    data class Tab(
        // Type
        var selectedTabType: ReadableBottomBar.TabType = ReadableBottomBar.TabType.ICON,

        // Animations
        var tabAnimationSelected: ReadableBottomBar.TabAnimation = ReadableBottomBar.TabAnimation.SLIDE,
        var tabAnimation: ReadableBottomBar.TabAnimation = ReadableBottomBar.TabAnimation.SLIDE,
        var animationDuration: Int = 400,
        var animationInterpolator: Interpolator = FastOutSlowInInterpolator(),

        // Colors
        @ColorInt var tabColorSelected: Int = Color.BLACK,
        @ColorInt var tabColorDisabled: Int = Color.BLACK,
        @ColorInt var tabColor: Int = Color.BLACK,

        // Ripple
        var rippleEnabled: Boolean = false,
        @ColorInt var rippleColor: Int = Color.BLACK,

        // Text
        @StyleRes var textAppearance: Int = -1,
        var typeface: Typeface = Typeface.DEFAULT,
        var textSize: Int = 14.spPx,

        // Icon
        var iconSize: Int = 24.dpPx,

        // Badge
        var badge: Badge = Badge()
    )

    data class Badge(
        var animation: ReadableBottomBar.BadgeAnimation = ReadableBottomBar.BadgeAnimation.SCALE,
        var animationDuration: Int = 150,
        @ColorInt var backgroundColor: Int = Color.rgb(255, 12, 16),
        @ColorInt var textColor: Int = Color.WHITE,
        @Dimension var textSize: Int = 9.spPx
    )

    data class Indicator(
        @Dimension var indicatorHeight: Int = 3.dpPx,
        @Dimension var indicatorMargin: Int = 0,
        @ColorInt var indicatorColor: Int = Color.BLACK,
        var indicatorAppearance: ReadableBottomBar.IndicatorAppearance = ReadableBottomBar.IndicatorAppearance.SQUARE,
        var indicatorLocation: ReadableBottomBar.IndicatorLocation = ReadableBottomBar.IndicatorLocation.TOP,
        var indicatorAnimation: ReadableBottomBar.IndicatorAnimation = ReadableBottomBar.IndicatorAnimation.SLIDE,
        var indicatorRectStyle: ReadableBottomBar.IndicatorStyle = ReadableBottomBar.IndicatorStyle.NORMAL
    )

    enum class StyleUpdateType {
        TAB_TYPE,
        COLORS,
        ANIMATIONS,
        RIPPLE,
        TEXT,
        ICON,
        BADGE
    }
}