package studio.su.austin.chueatchat;

import android.Manifest;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.angads25.toggle.LabeledSwitch;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.king.view.radarview.RadarView;

import ru.katso.livebutton.LiveButton;


public class MainActivity extends AppCompatActivity {
    private LiveButton mLiveButton;
    private BluetoothAdapter BA;  //定義藍芽連接器的ID
    private DrawerLayout mDrawerLayout;
    String adrass="noadrass";
    String D_adrass="no";
    int D_rssi=-100;
    boolean flag=true;
    trackThread1 thread1 = new trackThread1();
    Handler handler1 = new Handler();
    RadarView rv;
    LabeledSwitch labeledSwitches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                1);
        //藍芽裝置
        BA = BluetoothAdapter.getDefaultAdapter();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(ActionFoundReceiver , filter);
        //會動的雷達
        rv = (RadarView)findViewById(R.id.rv);

        //藍芽高級開關
        labeledSwitches = (LabeledSwitch)findViewById(R.id.switchBT);
        labeledSwitches.setOnToggledListener(toggle_listener);

        //側邊攔
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        //Closing drawer on item click
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                }
        );

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        //搜尋開關測試
        LiveButton mLiveButton=(LiveButton) findViewById(R.id.searchbutton);
        mLiveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                adrass="nodrass";
                //先取消搜尋藍芽，防止使用者連按造成效能嚴重耗損
                BA.cancelDiscovery();
                //開始搜尋藍芽，搜尋時間為12秒
                BA.startDiscovery();

                Toast.makeText(getApplicationContext(), "開始搜尋", Toast.LENGTH_SHORT).show();
                rv.start();

            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //藍芽開關
    private OnToggledListener toggle_listener = new OnToggledListener(){
        public void onSwitched(LabeledSwitch labeledSwitches, boolean isOn) {
            //如果BluetoothAdapter沒有被開啟
            if(isOn) {
                if (!BA.isEnabled()) {
                    //請求使用者開啟藍芽設備
                    Intent intent = new Intent(BA.ACTION_REQUEST_ENABLE);
                    //執行開啟藍芽的請求
                    startActivity(intent);
                    //利用快顯訊息告知藍芽啟動；顯示2sec
                    Toast.makeText(getApplicationContext(), "啟動藍芽", Toast.LENGTH_SHORT).show();
                } else {
                    //當藍芽已被啟動時；則利用快顯訊息告知藍芽已經啟動了；顯示2sec
                    Toast.makeText(getApplicationContext(), "已啟動藍芽", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                //關閉藍芽
                BA.disable();
                //利用快顯訊息告知藍芽關閉；顯示2sec
                Toast.makeText(getApplicationContext(), "關閉藍芽", Toast.LENGTH_SHORT).show();
            }

        }
    };
    //掃描Thread
    public class trackThread1 extends Thread{

        public void run() {
            if (flag) {
                Vibrator myVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
                //先取消搜尋藍芽，防止使用者連按造成效能嚴重耗損
                BA.cancelDiscovery();
                //開始搜尋藍芽，搜尋時間為12秒
                BA.startDiscovery();
                //Toast.makeText(getApplicationContext(), "開搜", Toast.LENGTH_SHORT).show();
                if (D_rssi > -50) {
                    flag=false;
                    //震動
                    myVibrator.vibrate(100);
                    //接下個畫面
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, FindFriendActivity.class);
                    startActivity(intent);
                }
            }
            handler1.postDelayed(thread1, 1000);
        }

    }

    //藍芽接收器事件
    private final BroadcastReceiver ActionFoundReceiver  = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent)
        {
            //此為BroadcastReceiver提供的方法，必須寫出來
            //每次有IntentFilter時都會自動呼叫這個方法接收

            String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_FOUND) == true);
            {
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                //儲存搜索到的藍芽裝置
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                //將藍芽裝置Address儲存
                D_adrass=device.getAddress();
                Toast.makeText(getApplicationContext(), "Name: "+device.getName() + "Address: "+D_adrass, Toast.LENGTH_SHORT).show();
                if(D_adrass.equals("98:B6:E9:0F:85:2A")) { //pokemon 98:B6:E9:0F:85:2A//GIGABYTE E4:A4:71:F9:36:01
                    D_rssi = rssi;
                    handler1.post(thread1);
                }

            }
        }
    };

    //使用者離開應用程式時，把藍芽功能關閉
    protected void onDestroy()
    {
        super.onDestroy();
        BA.disable();
        unregisterReceiver(ActionFoundReceiver);
    }
}
