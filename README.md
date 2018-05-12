### BroadcastTest
动态注册监听网络变化
这是Android自带的系统广播，我们可以在应用程序中通过监听这些广播来得到各种系统的状态信息。若想要接收到这些广播，就需要使用广播接收器。
所以在[MainActivity](/app/src/main/java/lyp/com/broadcasttest/MainActivity.java)中创建一个NetworkChangeReceiver 类继承自BroadcastReceiver， 并重写父类的 onReceive() 方法。当有广播到来时，onReceive()方法就会得到执行， 具体的逻辑在这个方法中处理。具体的代码如下：
```Java
 public void onReceive(Context context, Intent intent) {
//            Toast.makeText(context,"network changes",Toast.LENGTH_SHORT).show();
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                Toast.makeText(context, "网络可用", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "网络不可用", Toast.LENGTH_SHORT).show();

            }
        }
```
通过ConnectivityManager获取管理网络连接的系统服务类的实例，接下来再判断网络是否可用。
注意事项：

动态注册的广播接收器一定都要取消注册才行，这里我们是在 onDestroy()方法中通过调用 unregisterReceiver()方法来实现的。
在 [AndroidManifest.xml](/app/src/main/AndroidManifest.xml) 文件中加入访问系统的网络状态权限：
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
运行截图：

![network1](/img/network1.png "network1")
![network2](/img/network2.png "network2")
