package com.example.myweather

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ListView
import android.widget.ImageView
import android.widget.TextView

class SelectCity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_city)

        var selectedCity: String? = null
        var selectedCityCode: String? = null

        val backButton = findViewById<ImageView>(R.id.title_back)
        backButton.setOnClickListener {
//            val intent = Intent()
////            intent.putExtra("data_return", selectedCityCode)
////            intent.putExtra("city_return", selectedCity)
//            setResult(RESULT_OK, intent)
//            finish()

            // 获取SharedPreferences对象
            val preferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)

            // 读取城市名称和编码，默认为北京
            selectedCity = preferences.getString("cityname", "北京")
            selectedCityCode = preferences.getString("citycode", "101010100")

            // 输出日志，便于调试
            Log.d("abc", selectedCity.toString())
            Log.d("abc", selectedCity.toString())

            // 将城市编码返回给上一个Activity
            val intent = Intent().apply {
                putExtra("data_return", selectedCityCode)
            }
            setResult(RESULT_OK, intent)

            // 结束当前Activity
            finish()
        }

        val dbHelper = MyDatabaseHelper(this) // 实例化数据库帮助类
        val data = dbHelper.queryData() // 调用查询数据的方法

        // 获取ListView实例
        val cityListView = findViewById<ListView>(R.id.city_list)
        // 创建城市数据列表
        val newItems = mutableListOf<City>(
            City(getString(R.string.BeiJing), "101010100"),
            City(getString(R.string.ShangHai), "101020100"),
            City(getString(R.string.Guangzhou), "101280101"),
            City(getString(R.string.ShenZhen), "101280601")
        )
        // 实例化适配器并设置为ListView的适配器
        val cityAdapter = CityAdapter(this, newItems)
        cityListView.adapter = cityAdapter

        val title_name = findViewById<TextView>(R.id.title_name)

// 获取SharedPreferences对象
        val preferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
// 读取城市名称，默认为北京
        val cityname = preferences.getString("cityname", "北京")
// 输出日志，便于调试
        Log.d("abc", cityname.toString())
// 更新界面显示当前城市名称
        title_name.setText(getString(R.string.current_city)+"："+cityname)


        // 设置项点击监听器
        cityListView.setOnItemClickListener { _, _, position, _ ->
            // 根据点击位置获取城市对象
            val city = newItems[position]
            // 获取城市名称和编码
            selectedCity = city.name
            selectedCityCode = city.code // 设置选定的城市代码
            Log.d("abc", "cityName: $selectedCity  cityCode: $selectedCityCode")

            val name: TextView = findViewById(R.id.title_name)
            name.text = getString(R.string.current_city)+"："+selectedCity
            // 获取SharedPreferences对象
            val preferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
// 开启编辑模式
            val editor = preferences.edit()
// 将城市名称和编码保存到SharedPreferences
            editor.putString("cityname", selectedCity)
            editor.putString("citycode", selectedCityCode)
// 应用更改
            editor.apply()
        }
        // 获取EditText控件实例
        val editText = findViewById<EditText>(R.id.search_edit)

        // 为EditText添加文本更改监听器
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 文本改变之前调用，可以用于准备处理更改
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 文本改变时调用，主要用于实时搜索或其他响应用户输入的操作
                val text = s.toString() // 获取当前输入框中的文本
                Log.d("abc", "onTextChanged->New text: $text") // 打印当前文本用于调试
                // 用户输入文本时动态查询匹配的城市列表并更新ListView
                newItems.clear() // 清空当前列表
                val citylist = dbHelper.searchCitiesByPinyin(text) // 根据输入文本搜索城市

                // 处理查询结果
                for (item in citylist) {
                    Log.d("abc", "$item") // 打印查询结果用于调试
                    newItems.add(item) // 将查询到的城市添加到列表中
                }
                cityAdapter.notifyDataSetChanged() // 通知适配器数据已更改，刷新ListView显示
            }

            override fun afterTextChanged(s: Editable?) {
                // 文本改变之后调用，可以用于后处理，如清理、格式化文本等
            }
        })



    }
}