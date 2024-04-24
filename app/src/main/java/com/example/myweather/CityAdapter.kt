package com.example.myweather

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class CityAdapter(context: Context, private val cities: List<City>) : ArrayAdapter<City>(context, android.R.layout.simple_list_item_1, cities) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // 获取默认的视图
        val view = super.getView(position, convertView, parent)

        // 获取当前位置的城市对象
        val city = cities[position]
        // 设置列表项的文本为城市名称
        (view.findViewById(android.R.id.text1) as TextView).text = city.name

        return view
    }
}
