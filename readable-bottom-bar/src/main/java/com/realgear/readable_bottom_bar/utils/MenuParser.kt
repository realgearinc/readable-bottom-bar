package com.realgear.readable_bottom_bar.utils

import android.content.Context
import android.os.Build
import android.view.MenuInflater
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import com.realgear.readable_bottom_bar.ReadableBottomBar

internal object MenuParser {
    fun parse(context: Context, @MenuRes resId: Int, exception: Boolean): Array<out ReadableBottomBar.Tab> {
        val p = PopupMenu(context, null)
        MenuInflater(context).inflate(resId, p.menu)
        val menu = p.menu

        val size = menu.size()
        return Array(size) { i ->
            val item = menu.getItem(i)
            if (exception) {
                if (item.title == null) {
                    throw Exception("Menu item attribute 'title' is missing")
                }

                if (item.icon == null) {
                    throw Exception("Menu item attribute 'icon' for tab named '${item.title}' is missing")
                }
            }

            val contentDescription = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                item.contentDescription?.toString() else item.title.toString()

            ReadableBottomBar.Tab(
                title = item.title.toString(),
                icon = item.icon!!,
                id = item.itemId,
                enabled = item.isEnabled,
                contentDescription = contentDescription
            )
        }
    }
}