package com.carlex.mod;



import android.content.Intent;

import android.app.Activity;
import android.content.ComponentName;
import android.content.IntentFilter;

import android.content.Context;
import android.location.GnssStatus;
import android.location.LocationManager;
import android.util.Log;
import androidx.annotation.NonNull;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

import java.util.ArrayList;
import java.util.List;

public class GnssStatusHook implements IXposedHookLoadPackage {
    private static final String TAG = "GnssStatusHook";
    private static final String DIRECTORY_PATH = "/storage/emulated/0/carlex/";
    private static final String CELL_DATA_FILE = "satellites.json";
    private static Context systemContext;
    private static JSONArray satelliteData;

  /*  @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        XposedHelpers.findAndHookMethod(
            "android.app.ActivityThread",
            null,
            "systemMain",
            new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    systemContext =
                        (Context) XposedHelpers.callStaticMethod(
                            XposedHelpers.findClass("android.app.ActivityThread", null),
                            "currentApplication"
                        );
                }
            }
        );
    }*/
    
    private DATAgnss dataReceiver;


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
                        
                        if (!DATAgnss.isRunning) {
                    //DataReceiver dataReceiver = new DataReceiver();
                    //IntentFilter filter = new IntentFilter("com.carlex.drive.ACTION_SEND_DATA");
                    Context context = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
        
                    //  Context context = (Context) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(
                    //5   XposedHelpers.findClass("android.app.ActivityThread", lpparam.classLoader), "currentApplication"), "getApplicationContext");
                    
                    // Registrar o BroadcastReceiver
                    dataReceiver = new DATAgnss();
                    IntentFilter filter = new IntentFilter("com.carlex.drive.ACTION_SEND_DATA");
                    context.registerReceiver(dataReceiver, filter);
            
                    // Iniciar o Serviço do Emissor
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.carlex.drive", "com.carlex.drive.DataServiceGnss"));
                    context.startService(intent);     
                        
                    Log.i(TAG, "register Data from receiver ");
              
                        
                }    else Log.i(TAG, "ja tem register Data from receiver ");
       
                        
                        final GnssStatus.Callback originalCallback = (GnssStatus.Callback) param.args[0];
                        try {
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
                                public void onSatelliteStatusChanged(@NonNull GnssStatus status) {
                                    Log.i(TAG, "Original GNSS status: " + status);
                                    updateSatelliteDataFromJson();
                                    CustomGnssStatus fakeStatus = createFakeGnssStatus(status);
                                    GnssStatus newStatus = convertToGnssStatus(fakeStatus);
                                    originalCallback.onSatelliteStatusChanged(newStatus);
                                    Log.i(TAG, "Hooked GNSS status: " + newStatus);
                                }
                            };
                            param.args[0] = hookedCallback;
                            Log.i(TAG, "Hooked registerGnssStatusCallback");
                        } catch (Throwable t) {
                            Log.e(TAG, "Error: " + t);
                            t.printStackTrace();
                        }
                    }
                }
            );
        } catch (Throwable t) {
            Log.e(TAG, "Error: " + t);
            t.printStackTrace();
        }
    }

    private static void updateSatelliteDataFromJson() {
        /*try {
            Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", "chmod 777 " + DIRECTORY_PATH + CELL_DATA_FILE});
            process.waitFor();
        } catch (Exception e) {
            Log.e(TAG, "Error setting file permissions: " + e.getMessage());
        }*/
        
       StringBuilder content = DATAgnss.allPrefs;
              Log.i(TAG, "Data from receiver "+content);
          

        if (content == null || content.length() == 0) {
            return;
        }
        
        try{
            JSONArray jsonArray = new JSONArray(content.toString());
            if (jsonArray.length() > 0) {
                //JSONObject jsonObject = jsonArray.getJSONObject(0);
               // return jsonArray.getJSONObject(0);
                satelliteData = new JSONArray(jsonArray.toString());
            }
        } catch (JSONException e) {
                 Log.i(TAG,  " Error reading cell data: " + e.getMessage());
        }
        Log.i(TAG, " readCellData: Falha ao ler dados da célula");
        
        
        
        /*

        File file = new File(DIRECTORY_PATH, CELL_DATA_FILE);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            if (!file.exists()) {
            Log.e(TAG, "File not found: " + CELL_DATA_FILE);
            return;
        }
            
        } catch (IOException e) {
            Log.e(TAG, "Error reading satellite data from JSON file: ", e);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            satelliteData = new JSONArray(jsonString.toString());
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error reading satellite data from JSON file: ", e);
        }*/
    }

    public CustomGnssStatus createFakeGnssStatus(GnssStatus originalStatus) {
        if (satelliteData == null || satelliteData.length() == 0) {
            Log.e(TAG, "Satellite data not loaded or empty");
            return new CustomGnssStatus(new ArrayList<>()); // Return an empty custom status
        }

        try {
            List<CustomGnssStatus.Satellite> satellites = new ArrayList<>();
            JSONObject satelliteInfo = satelliteData.getJSONObject(0);
            JSONArray satellitesArray = satelliteInfo.getJSONArray("SatelitesData");

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

                satellites.add(new CustomGnssStatus.Satellite(
                        constellationType,
                        svid,
                        cn0DbHz,
                        elevationDegrees,
                        azimuthDegrees,
                        hasEphemerisData,
                        hasAlmanacData,
                        usedInFix));
            }

            return new CustomGnssStatus(satellites);
        } catch (Exception e) {
            Log.e(TAG, "Error creating fake GNSS status: " + e.getMessage(), e);
            return new CustomGnssStatus(new ArrayList<>()); // Return an empty custom status
        }
    }

    public GnssStatus convertToGnssStatus(CustomGnssStatus customStatus) {
        try {
            Class<?> gnssStatusClass = Class.forName("android.location.GnssStatus");
            Method getSatelliteCountMethod = gnssStatusClass.getDeclaredMethod("getSatelliteCount");
            Method getConstellationTypeMethod = gnssStatusClass.getDeclaredMethod("getConstellationType", int.class);
            Method getSvidMethod = gnssStatusClass.getDeclaredMethod("getSvid", int.class);
            Method getCn0DbHzMethod = gnssStatusClass.getDeclaredMethod("getCn0DbHz", int.class);
            Method getElevationDegreesMethod = gnssStatusClass.getDeclaredMethod("getElevationDegrees", int.class);
            Method getAzimuthDegreesMethod = gnssStatusClass.getDeclaredMethod("getAzimuthDegrees", int.class);
            Method hasEphemerisDataMethod = gnssStatusClass.getDeclaredMethod("hasEphemerisData", int.class);
            Method hasAlmanacDataMethod = gnssStatusClass.getDeclaredMethod("hasAlmanacData", int.class);
            Method usedInFixMethod = gnssStatusClass.getDeclaredMethod("usedInFix", int.class);

            gnssStatusClass = GnssStatus.class; // Need to obtain the correct class reference
            Constructor<?> constructor = gnssStatusClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            GnssStatus gnssStatus = (GnssStatus) constructor.newInstance();

            for (int i = 0; i < customStatus.getSatelliteCount(); i++) {
                getSatelliteCountMethod.invoke(gnssStatus, customStatus.getSatelliteCount());
                getConstellationTypeMethod.invoke(gnssStatus, i, customStatus.getConstellationType(i));
                getSvidMethod.invoke(gnssStatus, i, customStatus.getSvid(i));
                getCn0DbHzMethod.invoke(gnssStatus, i, customStatus.getCn0DbHz(i));
                getElevationDegreesMethod.invoke(gnssStatus, i, customStatus.getElevationDegrees(i));
                getAzimuthDegreesMethod.invoke(gnssStatus, i, customStatus.getAzimuthDegrees(i));
                hasEphemerisDataMethod.invoke(gnssStatus, i, customStatus.hasEphemerisData(i));
                hasAlmanacDataMethod.invoke(gnssStatus, i, customStatus.hasAlmanacData(i));
                usedInFixMethod.invoke(gnssStatus, i, customStatus.usedInFix(i));
            }

            return gnssStatus;
        } catch (Exception e) {
            Log.e(TAG, "Error converting custom GNSS status: " + e.getMessage(), e);
            return null;
        }
    }
}