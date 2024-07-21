package com.carlex.mod;

import android.content.Context;
import android.location.LocationManager;
import android.location.OnNmeaMessageListener;
import android.util.Log;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
//import com.topjohnwu.superuser.io.SuFile;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NmeaHook implements IXposedHookLoadPackage {
    private static final String TAG = "xNMEAHook";
    private static final String DIRECTORY_PATH = "/storage/emulated/0/carlex/";
    private static final String INPUT_FILE = "locations.json";

    private static Context systemContext;
    private static String NMEA = "$GPGGA,123519,4807.038,N,01131.000,E,1,08,0.9,545.4,M,46.9,M,,";
    /*
    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        XposedBridge.log(TAG + ": initialized in Zygote");
        systemContext = (Context) XposedHelpers.callMethod(
            XposedHelpers.callStaticMethod(
                XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread"
            ), "getSystemContext"
        );
    }*/

    private static void updateNmeaFromJson() {
        
        /*
       try {
            Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", "chmod 777 "+DIRECTORY_PATH+INPUT_FILE});
            process.waitFor();
        } catch (Exception e) {
           Log.e(TAG, "Error setting file permissions: " + e.getMessage());
        }
        */
        
       File file = new File(DIRECTORY_PATH, INPUT_FILE);
        
        
       // File file = new File(FILE_PATH);
      //  File file = SuFile.open(DIRECTORY_PATH, INPUT_FILE);
      
      try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
 
        if (!file.exists()) {
            Log.e(TAG, "File not found: " + INPUT_FILE);
            return;
        }
            
        } catch (IOException e) {
            Log.e(TAG, "Error reading loc data from JSON file: ", e);
            return ;
        }
        

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            JSONArray jsonArray = new JSONArray(jsonString.toString());
            if (jsonArray.length() > 0) {
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                if (jsonObject.has("nmea")) {
                    NMEA = jsonObject.getString("nmea");
                } else {
                    //Log.e(TAG, "NMEA key not found in JSON");
                }
            }
        } catch (IOException | JSONException e) {
            //Log.e(TAG, "Error reading NMEA from JSON file: ", e);
        }
    }

   
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("com.carlex.drive")) {
            return;
        }
        
        //XposedBridge.log(TAG + ": NmeaHook loaded for package " + lpparam.packageName);
        try {
            // Hook para o método addNmeaListener
            XposedHelpers.findAndHookMethod(LocationManager.class, "addNmeaListener", OnNmeaMessageListener.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    final OnNmeaMessageListener originalListener = (OnNmeaMessageListener) param.args[0];

                    OnNmeaMessageListener hookedListener = new OnNmeaMessageListener() {
                        @Override
                        public void onNmeaMessage(String message, long timestamp) {
                           // //Log.i(TAG, "Original NMEA message: " + message + " " + timestamp);
                            
                                // Modifique a mensagem NMEA e o timestamp
                                updateNmeaFromJson();
                                message = NMEA;
                                
                            // Passe a mensagem modificada
                            originalListener.onNmeaMessage(message, timestamp);
                            //Log.i(TAG, "Hooked NMEA message passed: " + message + " " + timestamp);
                        }
                    };
                    param.args[0] = hookedListener;
                   // //Log.i(TAG, "Hooked addNmeaListener");
                }
            });
        } catch (Throwable t) {
            //Log.e(TAG, "Error hooking addNmeaListener: " + t);
        }
    }
}
