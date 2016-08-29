package com.shituocheng.calcalculateapplication.com.myapplication;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Handler threadHandler;
    MyHandler myHandler;
    Handler uiHandler;
    HandlerThread handlerThread;

    private TextView textView;
    private static final int MESSAGE_OBJ=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView)findViewById(R.id.text);
        handlerThread = new HandlerThread("test",Thread.NORM_PRIORITY);
        handlerThread.start();
       // fetchData();
        threadHandler = new Handler(handlerThread.getLooper());
        threadHandler.post(new Runnable() {
            @Override
            public void run() {
                fetchData();
            }
        });
        // myHandler = new MyHandler(handlerThread.getLooper());

        uiHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == MESSAGE_OBJ){
                    String bit = (String) msg.obj;
                    textView.setText(bit);
                }
            }
        };

    }

    private void fetchData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String text = test();
                Message message = uiHandler.obtainMessage();
                message.obj = text;
                message.what = MESSAGE_OBJ;
                message.sendToTarget();
            }
        }).start();
    }

    private String test(){

        HttpURLConnection connection;
        InputStream inputStream;
        String api = "https://api.dribbble.com/v1/"+"shots"+"?"+"list"+"="+"animated"+"&"+ "access_token=" + "aef92385e190422a5f27496da51e9e95f47a18391b002bf6b1473e9b601e6216";
        try {
            connection = (HttpURLConnection)new URL(api).openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine())!=null){
                stringBuilder.append(line);
            }

            final String text = stringBuilder.toString();
            Log.d("test",text);

            return text;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    class MyHandler extends Handler {
        public MyHandler() {

        }

        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_OBJ) {
                String bit = (String) msg.obj;
                textView.setText(bit);
            }

        }
    }
}
