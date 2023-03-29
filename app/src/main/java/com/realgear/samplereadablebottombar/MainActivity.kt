package com.realgear.samplereadablebottombar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.realgear.readable_bottom_bar.ReadableBottomBar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = PageAdapter(supportFragmentManager, lifecycle)
        adapter.addFragment(Page())
        adapter.addFragment(Page())
        adapter.addFragment(Page())

        val viewPager = findViewById<ViewPager2>(R.id.root_view_pager)
        viewPager.adapter = adapter

        val bottomBar = findViewById<ReadableBottomBar>(R.id.readable_bottom_bar);
        bottomBar.setupWithViewPager2(viewPager)
    }
}

class PageAdapter(fm : FragmentManager, lifecycle : Lifecycle)
    : FragmentStateAdapter(fm, lifecycle) {

    private val items = ArrayList<Fragment>()

    fun addFragment(fragment : Fragment) {
        items.add(fragment)
    }

    override fun getItemCount(): Int = items.size
    override fun createFragment(position: Int): Fragment = items[position]


}