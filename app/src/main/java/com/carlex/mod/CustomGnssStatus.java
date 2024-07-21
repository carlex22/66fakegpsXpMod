package com.carlex.mod;

import java.util.ArrayList;
import java.util.List;

public class CustomGnssStatus {
    private final List<Satellite> satellites;

    public CustomGnssStatus(List<Satellite> satellites) {
        this.satellites = satellites;
    }

    public int getSatelliteCount() {
        return satellites.size();
    }

    public int getConstellationType(int satIndex) {
        return satellites.get(satIndex).constellationType;
    }

    public int getSvid(int satIndex) {
        return satellites.get(satIndex).svid;
    }

    public float getCn0DbHz(int satIndex) {
        return satellites.get(satIndex).cn0DbHz;
    }

    public float getElevationDegrees(int satIndex) {
        return satellites.get(satIndex).elevationDegrees;
    }

    public float getAzimuthDegrees(int satIndex) {
        return satellites.get(satIndex).azimuthDegrees;
    }

    public boolean hasEphemerisData(int satIndex) {
        return satellites.get(satIndex).hasEphemerisData;
    }

    public boolean hasAlmanacData(int satIndex) {
        return satellites.get(satIndex).hasAlmanacData;
    }

    public boolean usedInFix(int satIndex) {
        return satellites.get(satIndex).usedInFix;
    }

    public static class Satellite {
        int constellationType;
        int svid;
        float cn0DbHz;
        float elevationDegrees;
        float azimuthDegrees;
        boolean hasEphemerisData;
        boolean hasAlmanacData;
        boolean usedInFix;

        public Satellite(int constellationType, int svid, float cn0DbHz, float elevationDegrees, float azimuthDegrees,
                         boolean hasEphemerisData, boolean hasAlmanacData, boolean usedInFix) {
            this.constellationType = constellationType;
            this.svid = svid;
            this.cn0DbHz = cn0DbHz;
            this.elevationDegrees = elevationDegrees;
            this.azimuthDegrees = azimuthDegrees;
            this.hasEphemerisData = hasEphemerisData;
            this.hasAlmanacData = hasAlmanacData;
            this.usedInFix = usedInFix;
        }
    }
}
