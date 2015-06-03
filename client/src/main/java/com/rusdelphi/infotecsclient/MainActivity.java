package com.rusdelphi.infotecsclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;


public class MainActivity extends Activity {
    private static final int TCP_SERVER_PORT = 21111;
    private EditText mEt;
    private String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEt = (EditText) findViewById(R.id.et_number);
    }

    public void showToast(final String toast) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onSendClick(View v) {
        number = mEt.getText().toString();
        if (number.isEmpty()) {
            Toast.makeText(this, "Введите число", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent("com.rusdelphi.infoteksservice.START_SERVICE");
        intent.setPackage("com.rusdelphi.infoteksservice"); // костыль для 21 апи
        startService(intent);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        Socket s = new Socket("localhost", TCP_SERVER_PORT);
                        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                        //send output msg
                        String outMsg = number + System.getProperty("line.separator");
                        out.write(outMsg);
                        out.flush();
                        Log.d("TcpClient", "sent: " + outMsg);
                        //accept server response
                        String inMsg = in.readLine();
                        Log.d("TcpClient", "received: " + inMsg);
                        showToast("Ответ: " + inMsg);
                        //close connection
                        s.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();


    }


}
