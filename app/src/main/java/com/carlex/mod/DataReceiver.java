package com.carlex.mod;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.carlex.mod.MainActivity;
import java.util.Map;

public class DataReceiver extends BroadcastReceiver {
    public static StringBuilder allPrefs = new StringBuilder();
    public static boolean isRunning = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("com.carlex.drive.ACTION_SEND_DATA".equals(intent.getAction())) {
            // Processar todas as preferências recebidas
            allPrefs.setLength(0); // Limpar o StringBuilder
            for (String key : intent.getExtras().keySet()) {
                Object value = intent.getExtras().get(key);
                allPrefs.append(key).append(": ").append(value.toString()).append("\n");
            }
            isRunning = true;
            if (MainActivity.textView!=null)
                MainActivity.textView.setText(allPrefs.toString());
            Log.d("DataReceiver", "Preferências recebidas: " + allPrefs.toString());
        }
    }
}
