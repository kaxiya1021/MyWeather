package com.example.myweather

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

class MyPagerAdapter(private val context: Context) : PagerAdapter() {
    private val views = arrayOf(
        LayoutInflater.from(context).inflate(R.layout.next_week_weather_1, null),
        LayoutInflater.from(context).inflate(R.layout.next_week_weather_2, null)
    )

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = views[position]
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

    override fun getCount(): Int {
        return views.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }
}
