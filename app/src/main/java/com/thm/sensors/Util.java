package com.thm.sensors;

import java.util.HashMap;

public final class Util {
    /**
     * Device Type
     */
    public final static int MASTER = 0, SLAVE = 1;

    /**
     * Data Structure
     */
    public static HashMap<String, String> beaconDeviceMap = new HashMap<>(), beaconColorMap = new HashMap<>();
    public static HashMap<String, Integer> beaconModeMap = new HashMap<>();

    /**
     * Slave connection value
     */
    public static boolean isLogin;
    public static String connectedBeacon;
}
