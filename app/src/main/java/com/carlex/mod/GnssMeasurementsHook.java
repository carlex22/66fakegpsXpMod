package com.carlex.mod;


import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class GnssMeasurementsHook implements IXposedHookLoadPackage {
    
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.google.android.apps.location.gps.gnsslogger")) {
            return;
        }

        try {
            XposedHelpers.findAndHookMethod(
                "android.location.LocationManager",
                lpparam.classLoader,
                "registerGnssMeasurementsCallback",
                "android.location.GnssMeasurementsEvent$Callback",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("Interceptando chamada para registerGnssMeasurementsCallback");
                        param.setResult(false); // Retorna false para interceptar a chamada
                    }
                }
            );
        } catch (Exception e) {
            XposedBridge.log("Falha ao aplicar hook no método: " + e.getMessage());
        }
    }
}
