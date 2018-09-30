package net.loosash.learnshareduserid

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log





class MainActivity : AppCompatActivity() {

    val key_test_int = "key_test_int"
    val tag = "learnshareduserid"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val preferences = getSharedPreferences("sp", Context.MODE_PRIVATE)
        preferences.edit().putInt(key_test_int, 1024).apply()
        Log.d(tag,"获取sp内$key_test_int 值为${preferences.getInt(key_test_int,0)}")


        val clazz = classLoader.loadClass("net.loosash.learnshareduserid.MyTest")

        val method = clazz.getMethod("myTest")
        Log.d("xx",method.name)

        val invoke = method.invoke(clazz)
    }






}
