package com.rusdelphi.infotecsservice;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.SortedSet;
import java.util.TreeSet;


public class DecomposeService extends IntentService {

    private static final int TCP_SERVER_PORT = 21111;

    public DecomposeService() {
        super("LocalService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            // final String action = intent.getAction();
            //  if (ACTION_START.equals(action)) {
            handleActionStart();

        }
    }

    private String decompose(int n) {
        SortedSet<Integer> set = new TreeSet<>();
        set.add(1);
        int factor = 2;
        while (n > 1 && factor * factor <= n) {
            while (n % factor == 0) {
                set.add(factor);
                n /= factor;
            }
            ++factor;
        }
        if (n > 1) {
            set.add( n);
        }
        return set.toString();
    }

    private void handleActionStart() {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(TCP_SERVER_PORT);
            //accept connections
            Socket s = ss.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            //receive a message
            String incomingMsg = in.readLine();
            Log.d("TcpServer", "received: " + incomingMsg);
            //send a message
            int n = Integer.parseInt(incomingMsg);
            String outgoingMsg = decompose(n)+ System.getProperty("line.separator");
            out.write(outgoingMsg);
            out.flush();
            Log.d("TcpServer", "sent: " + outgoingMsg);
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


}
