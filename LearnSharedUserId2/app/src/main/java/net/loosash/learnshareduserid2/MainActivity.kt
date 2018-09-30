package net.loosash.learnshareduserid2

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {

    val key_test_int = "key_test_int"
    val tag = "learnshareduserid2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val otherContext = this.createPackageContext("net.loosash.learnshareduserid",
                Context.CONTEXT_IGNORE_SECURITY or Context.CONTEXT_INCLUDE_CODE)

        // 获取同sharedUserId应用SP
        val preferences = otherContext.getSharedPreferences("sp", Context.MODE_PRIVATE)
        Log.d(tag, "获取sp内$key_test_int 值为${preferences.getInt(key_test_int, 0)}")

        // 测试不同sharedUserId是否可以获取到资源文件，结果为可以访问
        Log.d(tag, "获取资源R.string.app_name:A应用->${otherContext.getText(R.string.app_name)}  B应用->${getText(R.string.app_name)}")

        // 测试不同sharedUserId是否可以访问内部方法，结果为可以
        val otherClassloader = otherContext.classLoader
        val clazz = otherClassloader.loadClass("net.loosash.learnshareduserid.MyTest")
        val method = clazz.getMethod("myTest")
        method.invoke(clazz)


        var file = File("/data/data/net.loosash.learnshareduserid/test.txt")
//        var file = File("/data/data/net.loosash.learnshareduserid/test_from_b.txt")
        if (!file.exists()) {
            file.createNewFile()
        }
        // 在B中将B的test_from_b写入A
//        var input = this.assets.open("test_from_b.txt")
        // 在B中将A的test写入A
        var input = otherContext.assets.open("test.txt")
        var output = FileOutputStream(file)

        input.bufferedReader().useLines { lines ->
            lines.forEach {
                output.write((it+"\n").toByteArray())
            }
        }


        input.close()
        output.close()


    }

}
