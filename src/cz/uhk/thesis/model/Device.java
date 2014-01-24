
package cz.uhk.thesis.model;

import java.util.HashMap;
import java.util.Map;

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
            
    private String ip = "";
    private String mac = "";
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

    public Device(String mac) {
        this.mac = mac;
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
    
    @Override
    public String toString()
    {
        String s = "ip:"+getIp()+" mac:"+getMac()+"\r\n";
        for(Map.Entry<String, String> i: info.entrySet()) {
            s += i.getKey()+": "+i.getValue()+"\r\n";
        }
        return s;
    }
    
}
