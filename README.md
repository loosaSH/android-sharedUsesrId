# Manifest中的sharedUserId
## 一、manifest标签包含内容


	<manifest xmlns:android="http://schemas.android.com/apk/res/android"  // 命名空间
	    android:versionCode="1"  // 版本号，会被gradle中覆盖，不推荐
	    android:versionName="1.0"  // 版本名称，同上
	    android:sharedUserId="net.loosash.share"  // sharedUserId 本文详细介绍
	    android:sharedUserLabel="@string/app_name"  // 为用户提供一个可读的标签，value仅能使用资源id
	    android:installLocation="internalOnly"  // 安装位置 默认internalOnly：只能安装在内部存储中；preferExternal：安装在外部存储中，当不可用时安装在内部存储中，安装后用户可以通过系统设置移动安装位置；auto：用户可以选择安装在内部存储还是外部存储中。
	    package="net.loosash.learnshareduserid">
	    ......
	</manifest>

## 二、sharedUserId注意事项
-  sharedUserId的value必须包含一个"."，否则在打包安装到手机的时候会报错。  
- 某些功能的实现需要对相同shareUserId的apk使用相同的签名。  
## 三、对sharedUserId的理解
我们都知道android的每一个应用都运行在单独的虚拟机上，以便提高系统的稳定性，每个应用进程都是由单独的Linux系统用户所创建，相同的sharedUserId的应用归属的linux相同的用户，资源共享则有很多的的便利可以利用。  
我做了一个测试，使用adb shell top命令查看进程。  
附带adb shell top命令解析


	>adb shell top -h
	Usage: top [ -m max_procs ] [ -n iterations ] [ -d delay ] [ -s sort_column ] [-t ] [ -h ]
    -m num  Maximum number of processes to display. 最多显示多少个进程
    -n num  Updates to show before exiting.  刷新次数 
    -d num  Seconds to wait between updates. 刷新间隔时间（默认5秒）
    -s col  Column to sort by (cpu,vss,rss,thr). 按哪列排序 
    -t      Show threads instead of processes. 显示线程信息而不是进程
    -h      Display this help screen.  显示帮助文档 

这张图是两个应用使用不同的sharedUserId
![不同sharedUserId.png](http://p0.qhimg.com/t01829bec486c4a5eb2.png)

下面这张图是改成了相同的sharedUserId
![相同sharedUserId.png](http://p0.qhimg.com/t01453aab81b3bb06d5.png)
## 四、对于相同sharedUserId的使用
以下两个应用的包名分别为net.loosash.learnshareduserid（A应用）、net.loosash.learnshareduserid2（B应用）
1、获取同名sharedUserId应用SP  
当然SP存储的跨应用处理还有其他方式，具体会在SP部分中进行说明，这里特指getSharePreferences(String name ,@PreferencesMode int mode)方法中mode为Context.MODE_PRIVATE情况。  
通过拿到Context来操作SP的读取。  
A应用中储存  


        val preferences = getSharedPreferences("sp", Context.MODE_PRIVATE)
        preferences.edit().putInt(key_test_int, 1024).apply()
        Log.d(tag,"获取sp内$key_test_int 值为${preferences.getInt(key_test_int,0)}")

B应用中读取SP  


        val otherContext = this.createPackageContext("net.loosash.learnshareduserid",Context.CONTEXT_IGNORE_SECURITY)
        val preferences = otherContext.getSharedPreferences("sp", Context.MODE_PRIVATE)
        Log.d(tag,"获取sp内$key_test_int 值为${preferences.getInt(key_test_int,0)}")

查看日志输出  
A应用![net.loosash.learnshareduserid应用输出.png](http://p0.qhimg.com/t01395e472801988c92.png)
B应用![net.loosash.learnshareduserid2应用输出.png](http://p0.qhimg.com/t01e7a2017784b4f82e.png)
可见不同的应用共享SP中的数据了，同理数据库也同样共享  
2、访问同名sharedUserId应用 data/data 目录  
在A中增加assets文件test.txt，在B中增加文件test_from_b.txt  
在B中加入代码  

manifest中增加权限  

  		<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
  		<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
增加代码  

		//var file = File("/data/data/net.loosash.learnshareduserid/test.txt")
        var file = File("/data/data/net.loosash.learnshareduserid/test_from_b.txt")
        if (!file.exists()) {
            file.createNewFile()
        }
        // 在B中将B的test_from_b写入A
        var input = this.assets.open("test_from_b.txt")
        // 在B中将A的test写入A
		//var input = otherContext.assets.open("test.txt")
        var output = FileOutputStream(file)
        val buffer = ByteArray(1024)

        input.bufferedReader().useLines { lines ->
            lines.forEach {
                output.write((it+"\n").toByteArray())
            }
        }
        input.close()
        output.close()

在不同sharedUserId时，运行B,报错Permission denied，无法访问A的data/data 目录![不同sharedUserId报权限错误.png](http://p0.qhimg.com/t01e1b4b4ad358e0ad6.png)
访问A的 data/data 目录，无test.txt及test_from_b.txt![目录中没有对应文件.png](http://p0.qhimg.com/t01a89cab9e6a5e5d98.png)
修改相同sharedUserId，运行B，访问 data/data 目录，发现已经存在test.txt及test_from_b.txt![目录中存在对应文件.png](http://p0.qhimg.com/t0117a06dc6f606b3c2.png)

# 五、总结
不同应用之间可以通过设置相同sharedUserId来实现在Linux上的用户统一，来打破不同应用间的沙盒性质，已实现数据、资源的共享。

[demo代码放在  https://github.com/loosaSH/android-sharedUsesrId](https://github.com/loosaSH/android-sharedUsesrId)




