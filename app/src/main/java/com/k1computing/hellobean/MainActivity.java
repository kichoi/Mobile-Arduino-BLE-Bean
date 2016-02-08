package com.k1computing.hellobean;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.punchthrough.bean.sdk.Bean;
import com.punchthrough.bean.sdk.BeanDiscoveryListener;
import com.punchthrough.bean.sdk.BeanListener;
import com.punchthrough.bean.sdk.BeanManager;
import com.punchthrough.bean.sdk.message.BeanError;
import com.punchthrough.bean.sdk.message.Callback;
import com.punchthrough.bean.sdk.message.DeviceInfo;
import com.punchthrough.bean.sdk.message.LedColor;
import com.punchthrough.bean.sdk.message.ScratchBank;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BeanDiscoveryListener, BeanListener {
    final String TAG = "BlueBean";
    final List<Bean> beans = new ArrayList<>();
    Bean bean = null;
    TextView textView =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        textView = (TextView)findViewById(R.id.main_text);
        textView.setText("Start Bluebean discovery ...");
        Log.d(TAG,"Start Bluebean discovery ...");
        BeanManager.getInstance().startDiscovery(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"MainActivity onResume");
    }

    @Override
    protected void onStop() {
        Log.d(TAG,"MainActivity.onStop");
        super.onStop();
    }

    @Override
    public void onBeanDiscovered(Bean bean, int rssi) {
        Log.d(TAG,"A bean is found: "+bean);
        StringBuffer aBuf= new StringBuffer(textView.getText());
        aBuf.append("\n");
        aBuf.append(""+bean.getDevice().getName()+" address: "+bean.getDevice().getAddress());
        textView.setText(aBuf.toString());
        beans.add(bean);
    }

    @Override
    public void onDiscoveryComplete() {
        StringBuffer aBuf= new StringBuffer(textView.getText());
        aBuf.append("\n");
        aBuf.append("not more Bluebean found");
        textView.setText(aBuf.toString());
        for (Bean bean : beans) {
            Log.d(TAG, "Bean name: "+bean.getDevice().getName());
            Log.d(TAG, "Bean address: "+bean.getDevice().getAddress());
         }
        if(beans.size()>0){
            bean=beans.get(0);
            bean.connect(this,this);
        }
    }

    // BeanListener Methods
    @Override
    public void onConnected() {
        Log.d(TAG,"connected to Bean!");
        bean.readDeviceInfo(new Callback<DeviceInfo>() {
            @Override
            public void onResult(DeviceInfo deviceInfo) {
                Log.d(TAG,deviceInfo.hardwareVersion());
                Log.d(TAG,deviceInfo.firmwareVersion());
                Log.d(TAG,deviceInfo.softwareVersion());
            }
        });

        LedColor ledColor = LedColor.create(0,0,60);
        bean.setLed(ledColor);
        bean.readTemperature(new Callback<Integer>() {
            @Override
            public void onResult(Integer data){
                Log.d(TAG, "Temperature: "+data);
                LedColor ledColor = LedColor.create(0,0,0);
                bean.setLed(ledColor);
            }
        });
    }

    @Override
    public void onConnectionFailed() {
        Log.d(TAG,"onConnectionFailed");
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG,"onDisconnected");
    }

    @Override
    public void onSerialMessageReceived(byte[] data) {
        Log.d(TAG,"onSerialMessageReceived");
        Log.d(TAG,"data: "+data);
    }

    @Override
    public void onScratchValueChanged(ScratchBank bank, byte[] value) {
        Log.d(TAG,"onScratchValueChanged");
        Log.d(TAG,"bank: "+bank+"\tvalue: "+value);
    }

    @Override
    public void onError(BeanError error) {
        Log.d(TAG,"onError");
        Log.d(TAG,"error: "+error);
    }
}
