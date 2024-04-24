package com.example.myweather

import android.app.Application
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // 应用启动时调用，用于执行初始化操作
        Log.d("abc", "MyApp onCreate")
        copyDatabaseFile() // 调用复制数据库文件的方法
    }

    private fun copyDatabaseFile() {
        try {
            // 检查数据库文件是否已经存在
            val outputFile = File(getDatabasePath("my_database.db").path)
            if (outputFile.exists()) {
                // 如果文件已存在，不进行复制操作
                Log.d("abc", "Database file already exists. Skipping copy operation.")
                return
            }

            // 从assets目录复制数据库文件到应用的数据库目录
            val inputStream = assets.open("city.db") // 打开assets目录下的city.db文件
            val outputStream = FileOutputStream(outputFile) // 准备输出流写入到目标位置

            val buffer = ByteArray(1024) // 缓冲区
            var length: Int

            // 读取并写入文件直到完成
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }

            // 清理工作
            outputStream.flush()
            outputStream.close()
            inputStream.close()

            Log.d("abc", "Database file copied successfully.")

        } catch (e: IOException) {
            // 复制过程中的异常处理
            e.printStackTrace()
        }
    }
}
