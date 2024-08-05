package com.carlex.mod;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;


import com.carlex.mod.MainActivity;
import java.util.Map;

public class DataReceiver extends BroadcastReceiver {
    public static StringBuilder allPrefs = new StringBuilder();
    public static boolean isRunning = false;

    @Override
    public void onReceive(Context context, Intent intent) {
      verificarDataLimite();
       
       
        if ("com.carlex.drive.ACTION_SEND_DATA".equals(intent.getAction())) {
            // Processar todas as preferências recebidas
            allPrefs.setLength(0); 
            allPrefs.append("[{"); // Limpar o StringBuilder
            for (String key : intent.getExtras().keySet()) {
                Object value = intent.getExtras().get(key);
                allPrefs.append('"'+key+'"').append(":").append(value.toString()).append(", ");
            }
            allPrefs.append('"'+"carlex"+'"'+":0.22}]");
            isRunning = true;
            if (MainActivity.textView!=null)
                MainActivity.textView.setText(allPrefs.toString());
          //  Log.d("DataReceiver", "Preferências recebidas: " + allPrefs.toString());
        }
    }
    
   private static final String DATA_LIMITE = "2024-08-31";

    
 public   Date dataLimite;
   public  Date dataAtual;

    private void verificarDataLimite() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
             dataLimite = sdf.parse(DATA_LIMITE);
            dataAtual = new Date();

            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        if (dataAtual.after(dataLimite)) {
                // Exibe uma mensagem de erro e finaliza o aplicativo
              double dd = 2/0;
            }
    }
    
}
