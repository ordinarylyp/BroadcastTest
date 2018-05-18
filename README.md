BroadcastTest
===================================
### 一、广播机制简介
Android 中的广播主要分两种类型：标准广播和有序广播。

##### 标准广播（Normal broadcasts）
  是一种完全异步执行的广播，在广播发出之后，所有的广播接收器几乎都会在同一时刻接收到这条广播消息，因此它们之间没有任何先后顺序可 言。这种广播的效率会比较高，但同时也意味着它是无法被截断的。标准广播的工作流程如下：
![broadcast1](/img/broadcast1.jpg "broadcast1.jpg")

##### 有序广播（Ordered broadcasts）
是一种同步执行的广播，在广播发出之后，同一时刻只会有一个广播接收器能够收到这条广播消息，当这个广播接收器中的逻辑执行完毕后，广播才会继续传递。所以此时的广播接收器是有先后顺序的，优先级高的广播接收器就可以先收到广播消息，并且前面的广播接收器还可以截断正在传递的广播，这样后面的广播接收器就无法收到广播消息了。有序广播的工作流程如下：

![broadcast2](/img/broadcast2.jpg "broadcast2.jpg")

### 二、动态注册监听网络变化
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
同时还要在onCreate()方法中注册广播，具体代码如下：
```Java
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);
        // 创建 IntentFilter 实例
        intentFilter = new IntentFilter();
        // 添加广播值
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        // 创建 NetworkChangeReceiver 实例
        networkChangeReceiver = new NetworkChangeReceiver();
        // 注册广播
        registerReceiver(networkChangeReceiver,intentFilter);
    }
```
注意事项：

动态注册的广播接收器一定都要取消注册才行，这里我们是在 onDestroy()方法中通过调用 unregisterReceiver()方法来实现的。
```Java
@Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消注册
        unregisterReceiver(networkChangeReceiver);
    }
```

在 [AndroidManifest.xml](/app/src/main/AndroidManifest.xml) 文件中加入访问系统的网络状态权限：
```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```		
运行截图：

![network1](/img/network1.png "network1")
![network2](/img/network2.png "network2")

### 三、静态注册广播实现开机启动
新建广播接收器[BootCompleteReceiver](/app/src/main/java/lyp/com/broadcasttest/BootCompleteReceiver.java),同时在 [AndroidManifest.xml](/app/src/main/AndroidManifest.xml) 文件中修改相应的权限：
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="lyp.com.broadcasttest">
	...
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
	...
        <receiver
            android:name=".BootCompleteReceiver"
            android:enabled="true"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
            </intent-filter>
        </receiver>
    </application>

</manifest>
```
运行结果如图：

![Boot](/img/Boot.png "Boot")

### 四、自定义广播
##### 1.标准广播
新建一个广播[MyBroadcastReceiver](/app/src/main/java/lyp/com/broadcasttest/MyBroadcastReceiver.java),修改[AndroidManifest.xml](/app/src/main/AndroidManifest.xml)中相应的权限：
```xml
        <receiver
            android:name=".MyBroadcastReceiver"
            android:enabled="true"
            android:exported="true">
            <intent-filter>
                <action android:name="lyp.com.broadcasttest.MY_BROADCAST"/>
            </intent-filter>
        </receiver>
```
同时在[activity_mian.xml]()中添加了一个Button按钮用于发出广播，并在[MainActivity](/app/src/main/java/lyp/com/broadcasttest/MainActivity.java)中实现
```Java
 protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button=findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //定义intent
                Intent intent=new Intent("lyp.com.broadcasttest.MY_BROADCAST");
		//发送广播
		sendBroadcast(intent);
            }
        });
    }

```
运行结果如下：

![mybroadcast1](/img/mybroadcast1.png "mybroadcast1")

##### 2.有序广播
新建一个BroadcastTest2项目，同时新建一个AnotherBroadcastReceiver，代码如下（没有上传这个项目）
```Java
public class AnotherBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Toast.makeText(context,"另外一个广播接收器收到广播",Toast.LENGTH_SHORT).show();
    }
```
同时在这个项目的AndroidManifest.xml中修改权限：
```xml
        <receiver
            android:name=".AnotherBroadcastReceiver"
            android:enabled="true"
            android:exported="true">
            <intent-filter>
                <action android:name="lyp.com.broadcasttest.MY_BROADCAST"/>
            </intent-filter>
        </receiver>
```
最终运行结果;

![mybroadcast1](/img/mybroadcast1.png "mybroadcast1")
![mybroadcast2](/img/mybroadcast2.png "mybroadcast2")
