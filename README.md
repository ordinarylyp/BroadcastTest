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
		//发送标准广播
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

这表明我们应用程序发出的广播是可以被其他应用程序接收到的，不过到目前为止我们的广播还都只是标准的广播，接下来尝试有序广播

首先修改[MainActivity](/app/src/main/java/lyp/com/broadcasttest/MainActivity.java)中的代码
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
		//发送有序广播
		 sendOrderedBroadcast(intent,null);
            }
        });
    }
```
发送有序广播 sendOrderedBroadcast() 方法接收两个参数，第一个是 Intent，第二个是与权限相关的字符串，在这传 null 就行了。当然现在运行程序点击按钮两个应用程序还是会收到广播，接下来还需要修改[ AndroidManifest.xml](/app/src/main/AndroidManifest.xml) 文件中的标签<receiver>中的代码，设定广播接收器的先后顺序：
```xml
        <receiver
            android:name=".MyBroadcastReceiver"
            android:enabled="true"
            android:exported="true">
            <intent-filter android:priority="100">
                <action android:name="lyp.com.broadcasttest.MY_BROADCAST"/>
            </intent-filter>
        </receiver>
````
上述代码，通过 android:priority 属性给广播接收器设置了优先级，优先级比较高的广播接收器就可以先收到广播。把 MyBroadcastReceiver 的优先级设成了 100，保证它一定会在 AnotherBroadcastReceiver 之前收到广播。

接下来，修改 MyBroadcastReceiver 中的代码，设置是否允许广播继续传递。在 onReceive()方法中调用 abortBroadcast()方法，表示将这条广播截断，后面的广播接收器将无法再接收到这条广播。
### 五、本地广播
之前的广播都属于系统全局广播，这样的广播可以被其他任何应用程序接收到，也会接收到来自于其他应用程序的广播，造成安全上的问题。

为了解决决这个问题就可以采用本地广播机制，而实现这个最主要就是通过LocalBroadcastManager来对广播进行管理，需要注意的是本地广播是需要动态注册的。
对[MainActivity](/app/src/main/java/lyp/com/broadcasttest/MainActivity.java)中的代码修改如下：
```Java
public class MainActivity extends AppCompatActivity {

    private IntentFilter intentFilter;
    private LocalReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;//管理广播

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        localBroadcastManager = localBroadcastManager.getInstance(this);//获取实例
        Button button=findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent("lyp.com.broadcasttest.LOCAL_BROADCAST");
                localBroadcastManager.sendBroadcast(intent);//发送本地广播
            }
        });
        intentFilter=new IntentFilter(); // 创建 IntentFilter 实例
        intentFilter.addAction("lyp.com.broadcasttest.LOCAL_BROADCAST");//添加广播值
        localReceiver= new LocalReceiver(); // 创建 LocalReceiver 实例
        localBroadcastManager.registerReceiver(localReceiver,intentFilter);//注册本地广播监听器
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消注册    
        localBroadcastManager.unregisterReceiver(localReceiver);

    }

    class LocalReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context,"收到本地广播",Toast.LENGTH_SHORT).show();
        }
    }
 }

```
运行结果：

![local](/img/local.png "local")
