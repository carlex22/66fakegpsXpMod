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
import android.content.Context;
import android.content.Intent;
import android.content.ComponentName;
import android.app.AndroidAppHelper;

import org.json.JSONException;
import org.json.JSONObject;
import com.topjohnwu.superuser.io.SuFile;
import android.content.Context;
import android.telephony.SubscriptionInfo;
import android.telephony.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import android.content.IntentFilter;
import android.os.Bundle;
import java.io.IOException;

public class CellHooks implements IXposedHookLoadPackage {
    private static final String TAG = "XXX CellHooks";
    private static final String DIRECTORY_PATH = "/data/data/com.carlex.drive/shared_prefs/";
    private static final String CELL_DATA_FILE = "FakeSensor.xml";
    public static DataReceiver dataReceiver;

    private static Context appContext;

    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (appContext == null) {
            appContext = AndroidAppHelper.currentApplication();
            if (appContext == null) {
                appContext = getSystemContext(lpparam.packageName);
            }
            Log.i(TAG, "App context initialized.");
        }

        if (appContext == null) {
            Log.e(TAG, "Failed to initialize app context.");
            return;
        }

        if (!DataReceiver.isRunning) {
            // Registrar o BroadcastReceiver
            dataReceiver = new DataReceiver();
            IntentFilter filter = new IntentFilter("com.carlex.drive.ACTION_SEND_DATA");
            appContext.registerReceiver(dataReceiver, filter);

            // Iniciar o Serviço do Emissor
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.carlex.drive", "com.carlex.drive.DataService"));
            appContext.startForegroundService(intent);
            Log.i(TAG, "Registered DataReceiver and started DataService");
        } else {
            Log.i(TAG, "DataReceiver is already running");
        }

        hookPackage(lpparam);
    }

    private Context getSystemContext(String packageName) {
        try {
            Context systemContext = (Context) XposedHelpers.callMethod(
                    XposedHelpers.callStaticMethod(
                            XposedHelpers.findClass("android.app.ActivityThread", null),
                            "currentActivityThread"
                    ),
                    "getSystemContext"
            );
            return systemContext.createPackageContext(packageName, Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
        } catch (Exception e) {
            Log.e(TAG, "Error getting system context: " + e.getMessage());
            return null;
        }
    }

    private void hookPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedBridge.log(TAG + ": handleLoadPackage for package: " + lpparam.packageName);

        // Hook for CellIdentityLte.getMnc
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityLte", lpparam.classLoader,
                "getMnc", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMnc Lte called");

                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getMnc");
                        if (value != null) {
                            param.setResult(value);
                            log("getMnc data loaded: " + value.toString());
                        }
                    }
                });
        
        // Hook for CellIdentityLte.getMcc
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityLte", lpparam.classLoader,
                "getMcc", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMcc Lte called");

                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getMcc");
                        if (value != null) {
                            param.setResult(value);
                            log("getMcc data loaded: " + value.toString());
                        }
                    }
                });
        
        
        // Hook for CellIdentityLte.getTac (equivalente a LAC no LTE)
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityLte", lpparam.classLoader,
                "getTac", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getTac called");

                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.optInt("getLac");
                        if (value != null) {
                            param.setResult(value);
                            log("getTac data loaded: " + value.toString());
                        }
                    }
                });
        
        
        // Hook for CellIdentityLte.getCi (equivalente a CID no LTE)
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityLte", lpparam.classLoader,
                "getCi", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getCi called");

                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.optInt("getCid");
                        if (value != null) {
                            param.setResult(value);
                            log("getCi data loaded: " + value.toString());
                        }
                    }
                });
        
        
        
         // Hook for CellIdentityLte.getMnc
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityLte", lpparam.classLoader,
                "getMncString", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMncString Lte called");

                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        String value = cellData.optString("getMnc");
                        if (value != null) {
                            param.setResult(value);
                            log("getMnc data loaded: " + value.toString());
                        }
                    }
                });
        
        // Hook for CellIdentityLte.getMcc
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityLte", lpparam.classLoader,
                "getMccString", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMccString Lte called");

                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        String value = cellData.optString("getMcc");
                        if (value != null) {
                            param.setResult(value);
                            log("getMcc data loaded: " + value.toString());
                        }
                    }
                });
        
        
      /*  // Hook for CellIdentityLte.getTac (equivalente a LAC no LTE)
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityLte", lpparam.classLoader,
                "getTacString", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getTacString called");

                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        String value = cellData.optString("getLac");
                        if (value != null) {
                            param.setResult(value);
                            log("getTac data loaded: " + value.toString());
                        }
                    }
                });*/
        
        
        /*// Hook for CellIdentityLte.getCi (equivalente a CID no LTE)
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityLte", lpparam.classLoader,
                "getCiString", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getCiString called");

                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        String value = cellData.optString("getCid");
                        if (value != null) {
                            param.setResult(value);
                            log("getCi data loaded: " + value.toString());
                        }
                    }
                });*/
        
        
        // Hook for CellIdentityGsm.getCid
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
                        if (value != null) {
                            param.setResult(value);
                            log("getCid data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for CellIdentityGsm.getLac
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
                        if (value != null) {
                            param.setResult(value);
                            log("getLac data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for CellIdentityGsm.getMcc
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityGsm", lpparam.classLoader,
                "getMcc", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMcc called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getMcc");
                        if (value != null) {
                            param.setResult(value);
                            log("getMcc data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for CellIdentityGsm.getMccString
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityGsm", lpparam.classLoader,
                "getMccString", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMccString called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        String value = cellData.optString("getMcc");
                        if (value != null) {
                            param.setResult(value);
                            log("getMccString data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for CellIdentityGsm.getMncString
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityGsm", lpparam.classLoader,
                "getMncString", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMncString called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        String value = cellData.optString("getMnc");
                        if (value != null) {
                            param.setResult(value);
                            log("getMncString data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for CellIdentityLte.getMcc
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityLte", lpparam.classLoader,
                "getMcc", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMcc called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getMcc");
                        if (value != null) {
                            param.setResult(value);
                            log("getMcc data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for CellIdentityTdscdma.getMccString
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityTdscdma", lpparam.classLoader,
                "getMccString", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMccString called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        String value = cellData.optString("getMcc");
                        if (value != null) {
                            param.setResult(value);
                            log("getMccString data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for CellIdentityTdscdma.getMncString
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityTdscdma", lpparam.classLoader,
                "getMncString", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMncString called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        String value = cellData.optString("getMnc");
                        if (value != null) {
                            param.setResult(value);
                            log("getMncString data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for CellIdentityWcdma.getCid
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityWcdma", lpparam.classLoader,
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
                        if (value != null) {
                            param.setResult(value);
                            log("getCid data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for CellIdentityWcdma.getLac
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityWcdma", lpparam.classLoader,
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
                        if (value != null) {
                            param.setResult(value);
                            log("getLac data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for CellIdentityWcdma.getMcc
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityWcdma", lpparam.classLoader,
                "getMcc", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMcc called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getMcc");
                        if (value != null) {
                            param.setResult(value);
                            log("getMcc data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for CellIdentityWcdma.getMccString
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityWcdma", lpparam.classLoader,
                "getMccString", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMccString called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        String value = cellData.optString("getMcc");
                        if (value != null) {
                            param.setResult(value);
                            log("getMccString data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for CellIdentityWcdma.getMnc
        XposedHelpers.findAndHookMethod("android.telephony.CellIdentityWcdma", lpparam.classLoader,
                "getMnc", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMnc called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getMnc");
                        if (value != null) {
                            param.setResult(value);
                            log("getMnc data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for SubscriptionInfo.getMcc
        XposedHelpers.findAndHookMethod("android.telephony.SubscriptionInfo", lpparam.classLoader,
                "getMcc", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMcc called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getMcc");
                        if (value != null) {
                            param.setResult(value);
                            log("getMcc data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for SubscriptionInfo.getMccString
        XposedHelpers.findAndHookMethod("android.telephony.SubscriptionInfo", lpparam.classLoader,
                "getMccString", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMccString called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        String value = cellData.optString("getMcc");
                        if (value != null) {
                            param.setResult(value);
                            log("getMccString data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for SubscriptionInfo.getMnc
        XposedHelpers.findAndHookMethod("android.telephony.SubscriptionInfo", lpparam.classLoader,
                "getMnc", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMnc called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        Object value = cellData.opt("getMnc");
                        if (value != null) {
                            param.setResult(value);
                            log("getMnc data loaded: " + value.toString());
                        }
                    }
                });

        // Hook for SubscriptionInfo.getMncString
        XposedHelpers.findAndHookMethod("android.telephony.SubscriptionInfo", lpparam.classLoader,
                "getMncString", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("getMncString called");

                        // Ler dados do arquivo JSON
                        JSONObject cellData = readCellData();
                        if (cellData == null) {
                            log("Não foi possível obter informações da célula.");
                            return;
                        }

                        String value = cellData.optString("getMnc");
                        if (value != null) {
                            param.setResult(value);
                            log("getMncString data loaded: " + value.toString());
                        }
                    }
                });

    }

    private JSONObject readCellData() {
        
        int maxAttempts = 5; // Número máximo de tentativas
        int attempt = 0;
        long retryDelay = 50;
        
        while (attempt < maxAttempts) {
            try {
                String content = DataReceiver.allPrefs.toString(); // Supõe que DATAgnss.allPrefs contém os dados necessários
                JSONArray jsonArray = new JSONArray(content);
                return jsonArray.getJSONObject(0);
            } catch (Exception e) {
                Log.e(TAG ,": Error reading cell data ");
                attempt++;
                if (attempt < maxAttempts) {
                    try {
                        Thread.sleep(retryDelay); // Esperar antes de tentar novamente
                    } catch (InterruptedException ie) {
                        Log.e(TAG, ": Thread interrupted during sleep: " + ie.getMessage());
                    }
                }
            }
        }
        Log.e(TAG , ": readCellData: Falha ao ler dados da célula após " + maxAttempts + " tentativas");
        return null;
    }
    
    private void log(String message) {
        Log.d(TAG, message);
    }
    
}
