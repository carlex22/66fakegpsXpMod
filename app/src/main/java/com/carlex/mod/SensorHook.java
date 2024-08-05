package com.carlex.mod;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import org.json.JSONException;
import android.content.Context;
import android.content.Intent;

import android.app.Activity;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import de.robv.android.xposed.IXposedHookZygoteInit;


public class SensorHook implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    private static final String TAG = "SensorHook";

    private SensorEventListener originalAccelListener;
    private DataReceiver dataReceiver;
    private SensorEventListener originalGyroListener;
    private SensorEventListener originalMagnetometerListener;

    private Queue<float[]> accelHistory = new LinkedList<>();
    private Queue<float[]> gyroHistory = new LinkedList<>();
    private Queue<float[]> magnetometerHistory = new LinkedList<>();
    
    private static Context systemContext;
    
    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        XposedBridge.log(TAG + ": initialized in Zygote");
        
        
        
    }


    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.hookAllMethods(SensorManager.class, "registerListener", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                SensorEventListener originalListener = (SensorEventListener) param.args[0];
                Sensor sensor = (Sensor) param.args[1];
                    
                if (param.args[1] != null) 
                if (sensor.getType() == Sensor.TYPE_ACCELEROMETER || sensor.getType() == Sensor.TYPE_GYROSCOPE || sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                    SensorEventListener proxyListener = new SensorEventListener() {
                        @Override
                        public void onSensorChanged(SensorEvent event) {
                            try {
                                float[] modifiedValues = readSensorDataFromReceiver(sensor.getType());
                                if (modifiedValues != null) {
                                    float[] smoothedValues = processSensorData(sensor.getType(), modifiedValues);
                                    SensorEvent modifiedEvent = createSensorEvent(sensor, smoothedValues, event.accuracy, event.timestamp);
                                    originalListener.onSensorChanged(modifiedEvent);
                                } else {
                                    originalListener.onSensorChanged(event);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error in onSensorChanged", e);
                            }
                        }

                        @Override
                        public void onAccuracyChanged(Sensor sensor, int accuracy) {
                            try {
                                originalListener.onAccuracyChanged(sensor, accuracy);
                            } catch (Exception e) {
                                Log.e(TAG, "Error in onAccuracyChanged", e);
                            }
                        }
                    };

                    param.args[0] = proxyListener;

                    if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                        originalAccelListener = originalListener;
                    } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                        originalGyroListener = originalListener;
                    } else if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                        originalMagnetometerListener = originalListener;
                    }
                }/* else {
                    Log.d(TAG, "Blocking sensor type: " + sensor.getType());
                    param.setResult(null);
                }*/
            }
        });

        XposedBridge.hookAllMethods(SensorManager.class, "unregisterListener", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
              /*  SensorEventListener listener = (SensorEventListener) param.args[0];
                Sensor sensor = (Sensor) param.args[1];

                if ((sensor.getType() == Sensor.TYPE_ACCELEROMETER && listener == originalAccelListener) ||
                    (sensor.getType() == Sensor.TYPE_GYROSCOPE && listener == originalGyroListener) ||
                    (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD && listener == originalMagnetometerListener)) {
                    param.setResult(null);
                }*/
            }
        });

        XposedBridge.hookAllMethods(SensorManager.class, "getSensorList", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    
                // Registrar o DataReceiver
                if (!DataReceiver.isRunning) {
                    //DataReceiver dataReceiver = new DataReceiver();
                    //IntentFilter filter = new IntentFilter("com.carlex.drive.ACTION_SEND_DATA");
                    Context context = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
        
                    //  Context context = (Context) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(
                    //5   XposedHelpers.findClass("android.app.ActivityThread", lpparam.classLoader), "currentApplication"), "getApplicationContext");
                    
                    // Registrar o BroadcastReceiver
                    dataReceiver = new DataReceiver();
                    IntentFilter filter = new IntentFilter("com.carlex.drive.ACTION_SEND_DATA");
                    context.registerReceiver(dataReceiver, filter);
            
                    // Iniciar o Serviço do Emissor
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.carlex.drive", "com.carlex.drive.DataService"));
                    context.startForegroundService(intent);     
                        
                    Log.i(TAG, "register Data from receiver ");
              
                        
                }    else Log.i(TAG, "ja tem register Data from receiver ");
                    
                List<Sensor> originalList = (List<Sensor>) param.getResult();
                List<Sensor> filteredList = new ArrayList<>();

                for (Sensor sensor : originalList) {
                    if (sensor.getType() == Sensor.TYPE_ACCELEROMETER || sensor.getType() == Sensor.TYPE_GYROSCOPE || sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                        filteredList.add(sensor);
                    }
                }

                param.setResult(filteredList);
            }
        });

       
    }

    private float[] readSensorDataFromReceiver(int sensorType) {
        try {
            StringBuilder content = DataReceiver.allPrefs;
              Log.i(TAG, "Data from receiver "+content);
          

            if (content == null || content.length() == 0) {
                return getDefaultValues(sensorType);
            }

            JSONArray jsonArray = new JSONArray(content.toString());
            if (jsonArray.length() > 0) {
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                if (sensorType == Sensor.TYPE_ACCELEROMETER) {
                    float ax = (float) jsonObject.optDouble("Ax");
                    float ay = (float) jsonObject.optDouble("Ay");
                    float az = (float) jsonObject.optDouble("Az");
                    return new float[]{ax, ay, az};
                } else if (sensorType == Sensor.TYPE_GYROSCOPE) {
                    return new float[]{
                        (float) jsonObject.optDouble("Gx"),
                        (float) jsonObject.optDouble("Gy"),
                        (float) jsonObject.optDouble("Gz")
                    };
                } else if (sensorType == Sensor.TYPE_MAGNETIC_FIELD) {
                    return new float[]{
                        (float) jsonObject.optDouble("MagX"),
                        (float) jsonObject.optDouble("MagY"),
                        (float) jsonObject.optDouble("MagZ")
                    };
                }
            }
            return getDefaultValues(sensorType);
        } catch (Exception e) {
            Log.e(TAG, "Error reading sensor data from receiver", e);
            return getDefaultValues(sensorType);
        }
    }

    private float[] getDefaultValues(int sensorType) {
        if (sensorType == Sensor.TYPE_ACCELEROMETER)
            return new float[]{0f, 0f, 0f};
        else if (sensorType == Sensor.TYPE_GYROSCOPE)
            return new float[]{0f, 0f, 0f};
        else if (sensorType == Sensor.TYPE_MAGNETIC_FIELD)
            return new float[]{0f, 0f, 0f};
        else
            return new float[]{0f, 0f, 0f};
    }

    private float[] processSensorData(int sensorType, float[] newValues) {
        Queue<float[]> history;
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            history = accelHistory;
        } else if (sensorType == Sensor.TYPE_GYROSCOPE) {
            history = gyroHistory;
        } else if (sensorType == Sensor.TYPE_MAGNETIC_FIELD) {
            history = magnetometerHistory;
        } else {
            return newValues;
        }

        if (history.size() >= 10) {
            history.poll();
        }
        history.add(newValues);

        float[] smoothedValues = new float[3];
        for (float[] values : history) {
            for (int i = 0; i < 3; i++) {
                smoothedValues[i] += values[i];
                smoothedValues[i] = round(smoothedValues[i], 3);
            }
        }
        for (int i = 0; i < 3; i++) {
            smoothedValues[i] /= history.size();
        }

        float[] threshold = new float[3];
        for (int i = 0; i < 3; i++) {
            threshold[i] = smoothedValues[i] * 2.25f;
        }

        for (int i = 0; i < 3; i++) {
            if (Math.abs(newValues[i] - smoothedValues[i]) > threshold[i]) {
                return smoothedValues;
            }
        }

        return newValues;
    }

    private SensorEvent createSensorEvent(Sensor sensor, float[] values, int accuracy, long timestamp) {
        try {
            Constructor<?> constructor = SensorEvent.class.getDeclaredConstructor(int.class);
            constructor.setAccessible(true);
            SensorEvent event = (SensorEvent) constructor.newInstance(values.length);
            Field sensorField = SensorEvent.class.getDeclaredField("sensor");
            Field valuesField = SensorEvent.class.getDeclaredField("values");
            Field accuracyField = SensorEvent.class.getDeclaredField("accuracy");
            Field timestampField = SensorEvent.class.getDeclaredField("timestamp");
            sensorField.setAccessible(true);
            valuesField.setAccessible(true);
            accuracyField.setAccessible(true);
            timestampField.setAccessible(true);

            sensorField.set(event, sensor);
            valuesField.set(event, values);
            accuracyField.set(event, accuracy);
            timestampField.set(event, timestamp);

            return event;
        } catch (Exception e) {
            Log.e(TAG, "Error creating SensorEvent", e);
            return null;
        }
    }

    private float round(float value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.floatValue();
    }
}
