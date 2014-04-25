package org.hkfree.topoagent.domain;

import java.util.HashMap;
import java.util.Map;
import org.jnetpcap.packet.format.FormatUtils;

/**
 *
 * @author Filip Valenta
 */
public class Device {
    
    public static final String CONNECTION_WIFI = "IEEE 802.11";

    public static final String DEVICE_HOST_ID = "hostid";
    public static final String DEVICE_PHYSICAL_MEDIUM = "physicalmedium";
    public static final String DEVICE_WIRELESS_MODE = "wirelessmode";
    public static final String DEVICE_IPV6 = "ipv6";
    public static final String DEVICE_LINK_SPEED = "linkspeed";
    public static final String DEVICE_MACHINE_NAME = "machinename";
    public static final String DEVICE_BSSID = "bssid";
    public static final String DEVICE_SSID = "ssid";
    public static final String DEVICE_CHARACTERISTICS = "characteristics";
    public static final String DEVICE_802_11_PHYSICAL_MEDIUM = "80211physicalmedium";

    public static final int PACKET_LOSS_UNDEFINED = -1;

    private String ip = "";
    private String mac = "";
    private byte[] bMac = new byte[6];
    private boolean isGateway = false;
    private HashMap<byte[], Integer> route2internet = new HashMap<>();
    private HashMap<String, String> info = new HashMap<>();

    /**
     * Get info from position pos
     *
     * @param pos
     * @return
     */
    public String getInfo(String pos) {
        String i = info.get(pos);
        if (i != null && (pos == DEVICE_HOST_ID || pos == DEVICE_BSSID)) {
            i = i.toUpperCase();
        }
        return i;
    }

    /**
     * Set info to position pos
     *
     * @param pos
     * @param val
     */
    public void setInfo(String pos, String val) {
         // null a prazdne nevkladat !
        if(val != null && !val.equals("")) {
            info.put(pos, val);
        }
    }

    public Device(String mac, byte[] bMac) {
        this.mac = mac;
        this.bMac = bMac;
    }

    public void addRoute2internet(byte[] ip) {
        setPacketLoss(ip, PACKET_LOSS_UNDEFINED);
    }

    /**
     * Overwrite row, set packet loss
     *
     * @param ip
     * @param packetLoss
     */
    public void setPacketLoss(byte[] ip, int packetLoss) {
        route2internet.put(ip, packetLoss);
    }

    public void setIP(String ip) {
        this.ip = ip;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getIp() {
        return ip;
    }

    public String getMac() {
        return mac;
    }

    public byte[] getMacAsByte() {
        return bMac;
    }

    public boolean isGateway() {
        return isGateway;
    }

    public void setIsGateway(boolean isGateway) {
        this.isGateway = isGateway;
    }

    public HashMap<byte[], Integer> getRoute2Internet() {
        return route2internet;
    }

    @Override
    public String toString() {
        // only debug
        String s = "ip:" + getIp() + " mac:" + getMac() + "\r\n";
        s += "is gateway: " + (isGateway() ? "yes" : "no") + "\r\n";
        for (Map.Entry<String, String> i : info.entrySet()) {
            s += i.getKey() + ": " + i.getValue() + "\r\n";
        }
        if (isGateway()) {
            int hop = 1;
            for (Map.Entry<byte[], Integer> route : route2internet.entrySet()) {
                s += "hop " + hop + ": " + FormatUtils.ip(route.getKey()) + ", packet received: " + route.getValue() + "\r\n";
                hop++;
            }
        }
        s += "---------------------------------\r\n";
        return s;
    }

    /**
     * Find lowest MAC of this Device
     *
     * @param allowBSSID check BSSID, often false
     * @return
     */
    public String getMacLowest(boolean allowBSSID) {

        String lowestMac = getMac().toUpperCase();
        String lowestMacLast = lowestMac.substring(15, 17);
        String lowestMacFirst = lowestMac.substring(0, 14);

        /**
         * if
         *
         * allowed comparison of BSSID DEVICE_BSSID is not empty. 
         * first parts of host id MAC and actual shortest mac are SAME !
         */
        String deviceBSSID = getInfo(DEVICE_BSSID);
        if (allowBSSID && deviceBSSID != null) {
            deviceBSSID = deviceBSSID.toUpperCase();
            if (deviceBSSID.substring(0, 14).equals(lowestMacFirst)) {
                int compare = deviceBSSID.substring(15, 17).compareTo(lowestMacLast);
                if (compare < 0) {
                    lowestMac = deviceBSSID;
                    lowestMacLast = lowestMac.substring(15, 17);
                }
            }
        }

        /* 
         * if 
         *
         * HOST_ID is not empty
         * first parts of host id MAC and actual shortest mac are SAME !
         */
        String deviceHostId = getInfo(DEVICE_HOST_ID);
        if (deviceHostId != null) {
            deviceHostId = deviceHostId.toUpperCase();
            if (deviceHostId.substring(0, 14).equals(lowestMacFirst)) {
                int compare = getInfo(DEVICE_HOST_ID).substring(15, 17).compareTo(lowestMacLast);
                if (compare < 0) {
                    lowestMac = deviceHostId;
                }
            }
        }

        return lowestMac;
    }

}
