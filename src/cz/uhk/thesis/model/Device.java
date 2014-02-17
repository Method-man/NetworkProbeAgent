
package cz.uhk.thesis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jnetpcap.packet.format.FormatUtils;

/**
 *
 * @author Filip Valenta
 */
public class Device {
    
    public static final String DEVICE_INFO_HOST_ID = "hostid";
    public static final String DEVICE_PHYSICAL_MEDIUM = "physicalmedium";
    public static final String DEVICE_WIRELESS_MODE = "wirelessmode";
    public static final String DEVICE_IPV6 = "ipv6";
    public static final String DEVICE_LINK_SPEED = "linkspeed";
    public static final String DEVICE_MACHINE_NAME = "machinename";
            
    private String ip = "";
    private String mac = "";
    private byte[] bMac = new byte[6];
    private boolean isGateway = false;
    private List<byte[]> route2internet = new ArrayList<>();
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
    
    public void addRoute2internet(byte[] ip)
    {
        route2internet.add(ip);
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
    
    @Override
    public String toString()
    {
        String s = "ip:"+getIp()+" mac:"+getMac()+"\r\n";
        s += "is gateway: "+(IsGateway()?"yes":"no")+"\r\n";
        for(Map.Entry<String, String> i: info.entrySet()) {
            s += i.getKey()+": "+i.getValue()+"\r\n";
        }
        if(IsGateway()) {
            int hop = 1;
            for(byte[] ipr: route2internet) {
                s += "hop "+hop+": "+FormatUtils.ip(ipr)+"\r\n";
                hop++;
            }
        }
        s += "---------------------------------\r\n";
        return s;
    }
    
}
