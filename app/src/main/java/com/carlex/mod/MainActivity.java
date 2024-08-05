package com.carlex.mod;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;



public class MainActivity extends Activity {
    private DataReceiver dataReceiver;
   private DATAgnss dataReceiverG;
    public static TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.superr);
        
        
        
        verificarDataLimite();

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
        
            
        Log.i("carlex.mod", "register Data from receiver "+dataReceiver.toString());
        
        
    }

    
    private static final String DATA_LIMITE = "2024-08-31";


    private void verificarDataLimite() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dataLimite = sdf.parse(DATA_LIMITE);
            Date dataAtual = new Date();

            if (dataAtual.after(dataLimite)) {
                // Exibe uma mensagem de erro e finaliza o aplicativo
               this.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Desregistrar o BroadcastReceiver
        unregisterReceiver(dataReceiver);
        unregisterReceiver(dataReceiverG);
    }
}
