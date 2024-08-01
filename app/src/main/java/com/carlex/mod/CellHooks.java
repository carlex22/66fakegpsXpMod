package com.carlex.mod;

import android.util.Log;
import android.telephony.gsm.GsmCellLocation;
import android.telephony.TelephonyManager;
import android.telephony.CellLocation;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;

import org.json.JSONObject;
//import com.topjohnwu.superuser.io.SuFile;
import android.content.Context;
import android.telephony.SubscriptionInfo;
import android.telephony.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import android.content.Intent;

import android.app.Activity;
import android.content.ComponentName;
import android.content.IntentFilter;

import java.io.IOException;

public class CellHooks implements IXposedHookLoadPackage {
    private static final String TAG = "CellHooks";
    private static final String DIRECTORY_PATH = "/storage/emulated/0/carlex/";
    private static final String CELL_DATA_FILE = "cell_data.json";
    private static Context systemContext;
    private DataReceiver dataReceiver;

 /*   @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        XposedHelpers.findAndHookMethod("android.app.ActivityThread", null, "systemMain", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                systemContext = (Context) XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentApplication");
                //Log.d(TAG, "System context obtained");
            }
        });
    }*/
    
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        hookPackage(lpparam);
    }

    private void hookPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedBridge.log(TAG + ": handleLoadPackage for package: " + lpparam.packageName);

        
        
        
        
        // Hook for CdmaCellLocation.getBaseStationLatitude.lua
  /*      XposedHelpers.findAndHookMethod("android.telephony.cdma.CdmaCellLocation", lpparam.classLoader,
                "getBaseStationLatitude", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getBaseStationLatitude called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.optInt("getBaseStationLatitude");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getBaseStationLatitude data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for CdmaCellLocation.getBaseStationLongitude.lua
        XposedHelpers.findAndHookMethod("android.telephony.cdma.CdmaCellLocation", lpparam.classLoader,
                "getBaseStationLongitude", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getBaseStationLongitude called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.optInt("getBaseStationLongitude");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getBaseStationLongitude data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for CellIdentityCdma.getBasestationId.lua
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityCdma", lpparam.classLoader,
                "getBasestationId", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getBasestationId called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getBasestationId");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getBasestationId data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for CellIdentityCdma.getLatitude.lua
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityCdma", lpparam.classLoader,
                "getLatitude", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getLatitude called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.optInt("getLatitude");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getLatitude data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for CellIdentityCdma.getNetworkId.lua
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityCdma", lpparam.classLoader,
                "getNetworkId", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getNetworkId called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getNetworkId");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getNetworkId data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for CellIdentityCdma.getSystemId.lua
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityCdma", lpparam.classLoader,
                "getSystemId", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getSystemId called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getSystemId");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getSystemId data loaded: " + value.toString());
                        }
                    }
                });
*/
        // Hook for CellIdentityGsm.getCid.lua
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityGsm", lpparam.classLoader,
                "getCid", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getCid called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.optInt("getCid");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getCid data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for CellIdentityGsm.getLac.lua
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityGsm", lpparam.classLoader,
                "getLac", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getLac called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.optInt("getLac");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getLac data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for CellIdentityGsm.getMcc.lua
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityGsm", lpparam.classLoader,
                "getMcc", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMcc called");
                    
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
                    context.startService(intent);     
                        
                    Log.i(TAG, "register Data from receiver ");
              
                        
                }    else Log.i(TAG, "ja tem register Data from receiver ");
       
        

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getMcc");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getMcc data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for CellIdentityGsm.getMccString.lua
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityGsm", lpparam.classLoader,
                "getMccString", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMccString called");
                    
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
                    context.startService(intent);     
                        
                    Log.i(TAG, "register Data from receiver ");
              
                        
                }    else Log.i(TAG, "ja tem register Data from receiver ");
       
        

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getMccString");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getMccString data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for CellIdentityGsm.getMncString.lua
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityGsm", lpparam.classLoader,
                "getMncString", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMncString called");

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
                    context.startService(intent);     
                        
                    Log.i(TAG, "register Data from receiver ");
              
                        
                }    else Log.i(TAG, "ja tem register Data from receiver ");
       
        
                    
                    
                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getMncString");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getMncString data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for CellIdentityLte.getMcc.lua
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityLte", lpparam.classLoader,
                "getMcc", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMcc called");
                    
                    
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
                    context.startService(intent);     
                        
                    Log.i(TAG, "register Data from receiver ");
              
                        
                }    else Log.i(TAG, "ja tem register Data from receiver ");
       
        
                    

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getMcc");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getMcc data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for CellIdentityTdscdma.getMccString.lua
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityTdscdma", lpparam.classLoader,
                "getMccString", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMccString called");
                    
                    
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
                    context.startService(intent);     
                        
                    Log.i(TAG, "register Data from receiver ");
              
                        
                }    else Log.i(TAG, "ja tem register Data from receiver ");
       
        
                    

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getMccString");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getMccString data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for CellIdentityTdscdma.getMncString.lua
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityTdscdma", lpparam.classLoader,
                "getMncString", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMncString called");
                    
                    
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
                    context.startService(intent);     
                        
                    Log.i(TAG, "register Data from receiver ");
              
                        
                }    else Log.i(TAG, "ja tem register Data from receiver ");
       
        
                    
                    
                    

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getMncString");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getMncString data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for CellIdentityWcdma.getCid.lua
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityWcdma", lpparam.classLoader,
                "getCid", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getCid called");
                    
                    
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
                    context.startService(intent);     
                        
                    Log.i(TAG, "register Data from receiver ");
              
                        
                }    else Log.i(TAG, "ja tem register Data from receiver ");
       
        
                    

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.optInt("getCid");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getCid data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for CellIdentityWcdma.getLac.lua
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityWcdma", lpparam.classLoader,
                "getLac", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getLac called");
                    
                    
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
                    context.startService(intent);     
                        
                    Log.i(TAG, "register Data from receiver ");
              
                        
                }    else Log.i(TAG, "ja tem register Data from receiver ");
       
        
                    
                    

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.optInt("getLac");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getLac data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for CellIdentityWcdma.getMcc.lua
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityWcdma", lpparam.classLoader,
                "getMcc", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMcc called");
                    
                    
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
                    context.startService(intent);     
                        
                    Log.i(TAG, "register Data from receiver ");
              
                        
                }    else Log.i(TAG, "ja tem register Data from receiver ");
       
        
                    
                    

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getMcc");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getMcc data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for CellIdentityWcdma.getMccString.lua
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityWcdma", lpparam.classLoader,
                "getMccString", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMccString called");
                    
                    
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
                    context.startService(intent);     
                        
                    Log.i(TAG, "register Data from receiver ");
              
                        
                }    else Log.i(TAG, "ja tem register Data from receiver ");
       
        
                    

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getMccString");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getMccString data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for CellIdentityWcdma.getMnc.lua
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityWcdma", lpparam.classLoader,
                "getMnc", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMnc called");
                    
                    
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
                    context.startService(intent);     
                        
                    Log.i(TAG, "register Data from receiver ");
              
                        
                }    else Log.i(TAG, "ja tem register Data from receiver ");
       
        
                    

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getMnc");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getMnc data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for Configuration.createFromParcel.lua
     /*   XposedHelpers.findAndHookMethod("android.content.res.Configuration", lpparam.classLoader,
                "createFromParcel", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("createFromParcel called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("createFromParcel");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("createFromParcel data loaded: " + value.toString());
                        }
                    }
                });*/
/*
        // Hook for SubscriptionInfo.getCardId.lua
        XposedHelpers.findAndHookMethod("android.telephony.SubscriptionInfo", lpparam.classLoader,
                "getCardId", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getCardId called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getCardId");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getCardId data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for SubscriptionInfo.getCarrierId.lua
        XposedHelpers.findAndHookMethod("android.telephony.SubscriptionInfo", lpparam.classLoader,
                "getCarrierId", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getCarrierId called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getCarrierId");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getCarrierId data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for SubscriptionInfo.getCarrierName.lua
        XposedHelpers.findAndHookMethod("android.telephony.SubscriptionInfo", lpparam.classLoader,
                "getCarrierName", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getCarrierName called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getCarrierName");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getCarrierName data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for SubscriptionInfo.getCountryIso.lua
        XposedHelpers.findAndHookMethod("android.telephony.SubscriptionInfo", lpparam.classLoader,
                "getCountryIso", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getCountryIso called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getCountryIso");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getCountryIso data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for SubscriptionInfo.getDisplayName.lua
        XposedHelpers.findAndHookMethod("android.telephony.SubscriptionInfo", lpparam.classLoader,
                "getDisplayName", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getDisplayName called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getDisplayName");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getDisplayName data loaded: " + value.toString());
                        }
                    }
                });
*/
        // Hook for SubscriptionInfo.getMcc.lua
        XposedHelpers.findAndHookMethod("android.telephony.SubscriptionInfo", lpparam.classLoader,
                "getMcc", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMcc called");
                    
                    
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
                    context.startService(intent);     
                        
                    Log.i(TAG, "register Data from receiver ");
              
                        
                }    else Log.i(TAG, "ja tem register Data from receiver ");
       
        
                    
                    

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getMcc");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getMcc data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for SubscriptionInfo.getMccString.lua
        XposedHelpers.findAndHookMethod("android.telephony.SubscriptionInfo", lpparam.classLoader,
                "getMccString", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMccString called");
                    
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
                    context.startService(intent);     
                        
                    Log.i(TAG, "register Data from receiver ");
              
                        
                }    else Log.i(TAG, "ja tem register Data from receiver ");
       
        
                    
                    

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getMccString");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getMccString data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for SubscriptionInfo.getMnc.lua
        XposedHelpers.findAndHookMethod("android.telephony.SubscriptionInfo", lpparam.classLoader,
                "getMnc", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMnc called");
                    
                    
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
                    context.startService(intent);     
                        
                    Log.i(TAG, "register Data from receiver ");
              
                        
                }    else Log.i(TAG, "ja tem register Data from receiver ");
       
        
                    
                    

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getMnc");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getMnc data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for SubscriptionInfo.getMncString.lua
        XposedHelpers.findAndHookMethod("android.telephony.SubscriptionInfo", lpparam.classLoader,
                "getMncString", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMncString called");
                    
                    
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
                    context.startService(intent);     
                        
                    Log.i(TAG, "register Data from receiver ");
              
                        
                }    else Log.i(TAG, "ja tem register Data from receiver ");
       
        
                    

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getMncString");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getMncString data loaded: " + value.toString());
                        }
                    }
                });

    /*    
        // Hook for SubscriptionInfo.getPortIndex.lua
        XposedHelpers.findAndHookMethod("android.telephony.SubscriptionInfo", lpparam.classLoader,
                "getPortIndex", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getPortIndex called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getPortIndex");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getPortIndex data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for TelephonyManager.getAllCellInfo.lua
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader,
                "getAllCellInfo", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getAllCellInfo called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getAllCellInfo");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getAllCellInfo data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for TelephonyManager.getCellLocation.lua
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader,
                "getCellLocation", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getCellLocation called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getCellLocation");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getCellLocation data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for TelephonyManager.getDataState.lua
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader,
                "getDataState", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getDataState called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getDataState");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getDataState data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for TelephonyManager.getGroupIdLevel1.lua
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader,
                "getGroupIdLevel1", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getGroupIdLevel1 called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getGroupIdLevel1");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getGroupIdLevel1 data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for TelephonyManager.getImei.lua
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader,
                "getImei", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getImei called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getImei");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getImei data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for TelephonyManager.getImei_slot.lua
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader,
                "getImei_slot", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getImei_slot called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getImei_slot");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getImei_slot data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for TelephonyManager.getLine1Number.lua
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader,
                "getLine1Number", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getLine1Number called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getLine1Number");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getLine1Number data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for TelephonyManager.getManufacturerCode.lua
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader,
                "getManufacturerCode", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getManufacturerCode called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getManufacturerCode");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getManufacturerCode data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for TelephonyManager.getManufacturerCode_slot.lua
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader,
                "getManufacturerCode_slot", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getManufacturerCode_slot called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getManufacturerCode_slot");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getManufacturerCode_slot data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for TelephonyManager.getMeid.lua
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader,
                "getMeid", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMeid called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getMeid");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getMeid data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for TelephonyManager.getMeid_slot.lua
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader,
                "getMeid_slot", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMeid_slot called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getMeid_slot");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getMeid_slot data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for TelephonyManager.getMmsUAProfUrl.lua
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader,
                "getMmsUAProfUrl", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMmsUAProfUrl called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getMmsUAProfUrl");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getMmsUAProfUrl data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for TelephonyManager.getNai.lua
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader,
                "getNai", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getNai called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getNai");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getNai data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for TelephonyManager.getNeighboringCellInfo.lua
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader,
                "getNeighboringCellInfo", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getNeighboringCellInfo called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getNeighboringCellInfo");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getNeighboringCellInfo data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for TelephonyManager.getNetworkCountryIso.lua
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader,
                "getNetworkCountryIso", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getNetworkCountryIso called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getNetworkCountryIso");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getNetworkCountryIso data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for TelephonyManager.getNetworkOperator.lua
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader,
                "getNetworkOperator", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getNetworkOperator called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getNetworkOperator");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getNetworkOperator data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for TelephonyManager.getNetworkOperatorName.lua
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader,
                "getNetworkOperatorName", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getNetworkOperatorName called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getNetworkOperatorName");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getNetworkOperatorName data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for TelephonyManager.getNetworkSpecifier.lua
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader,
                "getNetworkSpecifier", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getNetworkSpecifier called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getNetworkSpecifier");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getNetworkSpecifier data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for TelephonyManager.getPhoneType.lua
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader,
                "getPhoneType", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getPhoneType called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getPhoneType");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getPhoneType data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for TelephonyManager.getPrimaryImei.lua
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader,
                "getPrimaryImei", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getPrimaryImei called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getPrimaryImei");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getPrimaryImei data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for TelephonyManager.getSimCarrierIdName.lua
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader,
                "getSimCarrierIdName", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getSimCarrierIdName called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getSimCarrierIdName");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getSimCarrierIdName data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for TelephonyManager.getSimCountryIso.lua
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader,
                "getSimCountryIso", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getSimCountryIso called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getSimCountryIso");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getSimCountryIso data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for TelephonyManager.getSimOperator.lua
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader,
                "getSimOperator", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getSimOperator called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getSimOperator");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getSimOperator data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for TelephonyManager.getSimSerialNumber.lua
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader,
                "getSimSerialNumber", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getSimSerialNumber called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getSimSerialNumber");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getSimSerialNumber data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for TelephonyManager.getSimSpecificCarrierId.lua
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader,
                "getSimSpecificCarrierId", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getSimSpecificCarrierId called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getSimSpecificCarrierId");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getSimSpecificCarrierId data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for TelephonyManager.getSimState_slot.lua
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader,
                "getSimState_slot", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getSimState_slot called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getSimState_slot");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getSimState_slot data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for TelephonyManager.getSubscriberId.lua
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader,
                "getSubscriberId", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getSubscriberId called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getSubscriberId");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getSubscriberId data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for TelephonyManager.getSubscriptionId_slot.lua
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader,
                "getSubscriptionId_slot", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getSubscriptionId_slot called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getSubscriptionId_slot");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getSubscriptionId_slot data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for TelephonyManager.getVoiceMailAlphaTag.lua
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader,
                "getVoiceMailAlphaTag", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getVoiceMailAlphaTag called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getVoiceMailAlphaTag");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getVoiceMailAlphaTag data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for TelephonyManager.getVoiceMailNumber.lua
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader,
                "getVoiceMailNumber", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getVoiceMailNumber called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getVoiceMailNumber");
                        if (value == null) {
        //Log.d(TAG, "valor null");
} else {
                            param.setResult(value);
                            log("getVoiceMailNumber data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for TelephonyManager.listen.lua
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", lpparam.classLoader,
                "listen", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("listen called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("listen");
                        if (value == null) {
                            //Log.d(TAG, "valor null");
                    } else {
                            param.setResult(value);
                            log("listen data loaded: " + value.toString());
                        }
                    }
                });
        */
    }

    private JSONObject readCellData() {
       // log(TAG + ": readCellData: Lendo dados do arquivo JSON");
       
        
        
        // Registrar o DataReceiver
                
        
        StringBuilder content = DataReceiver.allPrefs;
              Log.i(TAG, "Data from receiver "+content);
          

        if (content == null || content.length() == 0) {
            return null;
        }
        
        try{
            JSONArray jsonArray = new JSONArray(content.toString());
            if (jsonArray.length() > 0) {
                //JSONObject jsonObject = jsonArray.getJSONObject(0);
                return jsonArray.getJSONObject(0);
            }
        } catch (JSONException e) {
                 log(TAG + ": Error reading cell data: " + e.getMessage());
        }
        log(TAG + ": readCellData: Falha ao ler dados da célula");
        
        return null;
        
        /* File file = new File(DIRECTORY_PATH, CELL_DATA_FILE);
     
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        if (!file.exists()) {
            Log.e(TAG, "File not found: " + CELL_DATA_FILE);
            return null;
        }
                 
        } catch (IOException e) {
            Log.e(TAG, "Error reading cell data from JSON file: ", e);
            return null;
        }
        
        
        //File file = SuFile.open(DIRECTORY_PATH, CELL_DATA_FILE);
        StringBuilder jsonContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            JSONArray jsonArray = new JSONArray(jsonContent.toString());
            if (jsonArray.length() > 0) {
              //  log(TAG + ": readCellData: Dados da célula lidos com sucesso");
                return jsonArray.getJSONObject(0);
            }
        } catch (IOException | JSONException e) {
                 log(TAG + ": Error reading cell data: " + e.getMessage());
        }
        log(TAG + ": readCellData: Falha ao ler dados da célula");
        return null;*/
    }

    private void log(String message) {
        //Log.d(TAG, message);
    }
}
