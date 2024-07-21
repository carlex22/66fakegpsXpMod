package com.carlex.mod;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;


public class MainActivity extends Activity {
    private DataReceiver dataReceiver;
    public static TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.superr);

        // Encontrar o TextView no layout
        textView = findViewById(R.id.textv);

        // Registrar o BroadcastReceiver
        dataReceiver = new DataReceiver();
        IntentFilter filter = new IntentFilter("com.carlex.drive.ACTION_SEND_DATA");
        registerReceiver(dataReceiver, filter);

        // Iniciar o Serviço do Emissor
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.carlex.drive", "com.carlex.drive.DataService"));
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Desregistrar o BroadcastReceiver
        unregisterReceiver(dataReceiver);
    }
}
