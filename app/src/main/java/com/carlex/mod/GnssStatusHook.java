package com.carlex.mod;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.GnssStatus;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.location.GnssStatus;
import android.location.LocationManager;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class GnssStatusHook implements IXposedHookLoadPackage {
    private static final String TAG = "GnssStatusHook";
    private static JSONArray satelliteData;
    public static DataReceiver dataReceiver;

    private static void updateSatelliteDataFromJson() {
        
        
        try {
            
        String content = dataReceiver.allPrefs.toString(); // Supõe que DATAgnss.allPrefs contém os dados necessários
        Log.i(TAG, "Data from receiver: " + content);
        
         
        String sat = "";
            JSONArray jsonArray = new JSONArray(content.toString());
            if (jsonArray.length() > 0) {
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                content = jsonObject.getString("SatelitesData");
                content = content.replace("\\\"", "\"");
            } else {
                Log.i(TAG, "Data is empty, initializing empty satelliteData");
                satelliteData = new JSONArray();
                return;
            }

            jsonArray = new JSONArray(content.toString());

        if (content == null || content.length() == 0) {
            satelliteData = new JSONArray(); // Inicializa como um JSONArray vazio
            Log.i(TAG, "SatelitesData is empty, initializing empty satelliteData");
            return;
        }
        
            
          //  JSONArray jsonArray = new JSONArray(content.toString());
            if (jsonArray.length() > 0) {
                satelliteData = new JSONArray(jsonArray.toString());
                Log.i(TAG, "Satellite data successfully updated from preferences : "+jsonArray.toString());
            } else {
                satelliteData = new JSONArray(); // Inicializa como um JSONArray vazio se o conteúdo estiver vazio
                Log.i(TAG, "jsonArray is empty, initializing empty satelliteData");
            }
        } catch (JSONException e) {
            satelliteData = new JSONArray(); // Inicializa como um JSONArray vazio em caso de erro
            Log.e(TAG, "Error reading satellite data from preferences: " + e.getMessage());
        }
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        try {
            XposedBridge.log("Hooking package: " + lpparam.packageName);
            XposedHelpers.findAndHookMethod(
                LocationManager.class,
                "registerGnssStatusCallback",
                GnssStatus.Callback.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Context context = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
                        
                        if (!DataReceiver.isRunning) {
                            // Registrar o BroadcastReceiver
                            dataReceiver = new DataReceiver();
                            IntentFilter filter = new IntentFilter("com.carlex.drive.ACTION_SEND_DATA");
                            context.registerReceiver(dataReceiver, filter);
                    
                            // Iniciar o Serviço do Emissor
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName("com.carlex.drive", "com.carlex.drive.DataService"));
                            context.startForegroundService(intent); 
                            Log.i(TAG, "Registered DataReceiver and started DataService");
                            
                            
                        } else {
                            Log.i(TAG, "DataReceiver is already running");
                        }
                        
                        Log.d("DataReceiver", "Preferências GNSS: " + dataReceiver.allPrefs.toString());
 

                        updateSatelliteDataFromJson();

                        final GnssStatus.Callback originalCallback = (GnssStatus.Callback) param.args[0];
                        GnssStatus.Callback hookedCallback = new GnssStatus.Callback() {
                            @Override
                            public void onStarted() {
                                Log.i(TAG, "GNSS started");
                                originalCallback.onStarted();
                            }

                            @Override
                            public void onStopped() {
                                Log.i(TAG, "GNSS stopped");
                                originalCallback.onStopped();
                            }

                            @Override
                            public void onFirstFix(int ttffMillis) {
                                Log.i(TAG, "GNSS first fix: " + ttffMillis);
                                originalCallback.onFirstFix(ttffMillis);
                            }

                            @Override
                            public void onSatelliteStatusChanged(
                            @NonNull GnssStatus status) {
                                    Log.i(TAG, "Original GNSS status: " + status);
                                    updateSatelliteDataFromJson();
                                    GnssStatus fakeStatus =
                                    createFakeGnssStatus(status);
                                    originalCallback.onSatelliteStatusChanged( fakeStatus);
                                    Log.i(TAG, "Hooked GNSS status: " + fakeStatus);
                            }
                        };
                        param.args[0] = hookedCallback;
                        Log.i(TAG, "Hooked registerGnssStatusCallback");
                    }
                }
            );
        } catch (Throwable t) {
            Log.e(TAG, "Error: " + t);
            t.printStackTrace();
        }
    }

    public GnssStatus createFakeGnssStatus(GnssStatus originalStatus) {
        if (satelliteData == null || satelliteData.length() == 0) {
            Log.e(TAG, "Satellite data not loaded or empty");
            return originalStatus;
        }

        try {
            GnssStatus.Builder builder = new GnssStatus.Builder();
            JSONArray satellitesArray = satelliteData;

            for (int i = 0; i < satellitesArray.length(); i++) {
                JSONObject satelliteObject = satellitesArray.getJSONObject(i);
                String prnKey = satelliteObject.keys().next();
                JSONObject satelliteDetails = satelliteObject.getJSONObject(prnKey);

                int svid = Integer.parseInt(prnKey);
                int constellationType = GnssStatus.CONSTELLATION_GPS;
                float cn0DbHz = (float) satelliteDetails.getDouble("snr");
                float elevationDegrees = (float) satelliteDetails.getDouble("elevation");
                float azimuthDegrees = (float) satelliteDetails.getDouble("azimuth");
                boolean hasEphemerisData = satelliteDetails.getBoolean("hasEphemeris");
                boolean hasAlmanacData = satelliteDetails.getBoolean("hasAlmanac");
                boolean usedInFix = satelliteDetails.getBoolean("usedInFix");

                builder.addSatellite(
                        constellationType,
                        svid,
                        cn0DbHz,
                        elevationDegrees,
                        azimuthDegrees,
                        hasAlmanacData,
                        hasEphemerisData,
                        usedInFix,
                        false,
                        0.0f,
                        false,
                        2.0f);
            }

            return builder.build();
        } catch (Exception e) {
            Log.e(TAG, "Error creating fake GNSS status: " + e.getMessage(), e);
            return originalStatus;
        }
    }
}
