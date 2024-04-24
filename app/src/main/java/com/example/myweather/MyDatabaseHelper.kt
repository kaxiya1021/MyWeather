package com.example.myweather

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "my_database.db" // 数据库文件名
        const val DATABASE_VERSION = 1 // 数据库版本
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 第一次使用数据库时创建表
        db.execSQL("CREATE TABLE my_table (id INTEGER PRIMARY KEY, name TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 更新数据库版本时调用，用于执行数据库迁移操作
        db.execSQL("DROP TABLE IF EXISTS my_table") // 删除旧表
        onCreate(db) // 创建新表
    }

    @SuppressLint("Range")
    fun queryData(): List<String> {
        val data = mutableListOf<String>() // 存储查询结果
        val db = readableDatabase // 获取数据库实例
        val cursor = db.rawQuery("SELECT * FROM city", null) // 执行查询操作

        if (cursor.moveToFirst()) {
            do {
                // 读取每一行的数据
                val name = cursor.getString(cursor.getColumnIndex("number"))
                data.add(name) // 添加到结果列表
            } while (cursor.moveToNext())
        }

        cursor.close() // 关闭游标
        db.close() // 关闭数据库连接

        return data // 返回查询结果
    }

    // 根据城市名称搜索城市信息
    fun searchCitiesByName(name: String): List<City> {
        val COLUMN_CITY = "city" // 城市名称列
        val COLUMN_CODE = "number" // 城市编码列

        val db = readableDatabase // 获取可读数据库实例
        // 执行查询操作，使用LIKE语句进行模糊匹配
        val cursor = db.query("city", arrayOf(COLUMN_CITY, COLUMN_CODE), "$COLUMN_CITY LIKE ?", arrayOf("%$name%"), null, null, null)
        val result = mutableListOf<City>() // 存储查询结果
        while (cursor.moveToNext()) {
            val cityName = cursor.getString(cursor.getColumnIndex(COLUMN_CITY))
            val cityCode = cursor.getString(cursor.getColumnIndex(COLUMN_CODE))
            result.add(City(cityName, cityCode)) // 创建City对象并添加到结果列表
        }
        cursor.close() // 关闭游标
        db.close() // 关闭数据库连接
        return result // 返回查询结果
    }

    fun searchCitiesByPinyin(pinyin: String): List<City> {
        val COLUMN_CITY = "city" // 城市名称列
        val COLUMN_CODE = "number" // 城市编码列
        val COLUMN_PINYIN = "allpy" // 城市名称拼音列

        val db = readableDatabase // 获取可读数据库实例
        // 执行查询操作，使用LIKE语句进行模糊匹配
        val cursor = db.query("city", arrayOf(COLUMN_CITY, COLUMN_CODE), "$COLUMN_PINYIN LIKE ?", arrayOf("%$pinyin%"), null, null, null)
        val result = mutableListOf<City>() // 存储查询结果
        while (cursor.moveToNext()) {
            val cityName = cursor.getString(cursor.getColumnIndex(COLUMN_CITY))
            val cityCode = cursor.getString(cursor.getColumnIndex(COLUMN_CODE))
            result.add(City(cityName, cityCode)) // 创建City对象并添加到结果列表
        }
        cursor.close() // 关闭游标
        db.close() // 关闭数据库连接
        return result // 返回查询结果
    }


}
