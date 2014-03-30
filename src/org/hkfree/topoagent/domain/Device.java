
package org.hkfree.topoagent.domain;

import java.util.HashMap;
import java.util.Map;
import org.jnetpcap.packet.format.FormatUtils;

/**
 *
 * @author Filip Valenta
 */
public class Device {
    
    public static final String DEVICE_HOST_ID = "hostid";
    public static final String DEVICE_PHYSICAL_MEDIUM = "physicalmedium";
    public static final String DEVICE_WIRELESS_MODE = "wirelessmode";
    public static final String DEVICE_IPV6 = "ipv6";
    public static final String DEVICE_LINK_SPEED = "linkspeed";
    public static final String DEVICE_MACHINE_NAME = "machinename";
    public static final String DEVICE_BSSID = "bssid";
    
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
    public String getInfo(String pos)
    {
        return info.get(pos);
    }
    
    /**
     * Set info to position pos
     * @param pos
     * @param val 
     */
    public void setInfo(String pos, String val)
    {
        info.put(pos, val);
    }

    public Device(String mac, byte[] bMac) {
        this.mac = mac;
        this.bMac = bMac;
    }
    
    public void AddRoute2internet(byte[] ip) 
    {
        SetPacketLoss(ip, PACKET_LOSS_UNDEFINED);
    }
    
    /**
     * Overwrite row, set packet loss
     * 
     * @param ip
     * @param packetLoss 
     */
    public void SetPacketLoss(byte[] ip, int packetLoss)
    {
        route2internet.put(ip, packetLoss);
    }
    
    public void SetIP(String ip)
    {
        this.ip = ip;
    }
    
    public void SetMac(String mac)
    {
        this.mac = mac;
    }

    public String getIp() {
        return ip;
    }

    public String getMac() {
        return mac;
    }
    
    public byte[] getMacAsByte()
    {
        return bMac;
    }

    public boolean IsGateway() {
        return isGateway;
    }

    public void setIsGateway(boolean isGateway) {
        this.isGateway = isGateway;
    }  
    
    public HashMap<byte[], Integer> GetRoute2Internet()
    {
        return route2internet;
    }
    
    @Override
    public String toString()
    {
        // only debug
        String s = "ip:"+getIp()+" mac:"+getMac()+"\r\n";
        s += "is gateway: "+(IsGateway()?"yes":"no")+"\r\n";
        for(Map.Entry<String, String> i: info.entrySet()) {
            s += i.getKey()+": "+i.getValue()+"\r\n";
        }
        if(IsGateway()) {
            int hop = 1;
            for(Map.Entry<byte[], Integer> route: route2internet.entrySet()) {
                s += "hop "+hop+": "+FormatUtils.ip(route.getKey())+", packet received: "+route.getValue()+"\r\n";
                hop++;
            }
        }
        s += "---------------------------------\r\n";
        return s;
    }
    
}
