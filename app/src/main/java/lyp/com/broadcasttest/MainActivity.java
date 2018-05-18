package lyp.com.broadcasttest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Network;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;

    private LocalReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
     /*   intentFilter = new IntentFilter();// 创建 IntentFilter 实例
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");// 添加广播值(网络状态发生改变)
        networkChangeReceiver = new NetworkChangeReceiver(); // 创建 NetworkChangeReceiver 实例
        registerReceiver(networkChangeReceiver, intentFilter);//注册广播   */
        localBroadcastManager = localBroadcastManager.getInstance(this);//获取实例
        Button button=findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
     /*           //定义intent
                Intent intent=new Intent("lyp.com.broadcasttest.MY_BROADCAST");
   //标准 广播   sendBroadcast(intent);
                //有序广播
                sendOrderedBroadcast(intent,null);*/
                Intent intent=new Intent("lyp.com.broadcasttest.LOCAL_BROADCAST");
                localBroadcastManager.sendBroadcast(intent);//发送本地广播
            }
        });
        intentFilter=new IntentFilter();
        intentFilter.addAction("lyp.com.broadcasttest.LOCAL_BROADCAST");//添加广播值
        localReceiver= new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver,intentFilter);//注册本地广播监听器
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消注册     unregisterReceiver(networkChangeReceiver);
        localBroadcastManager.unregisterReceiver(localReceiver);

    }

    class LocalReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context,"收到本地广播",Toast.LENGTH_SHORT).show();
        }
    }

    class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
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
    }
}