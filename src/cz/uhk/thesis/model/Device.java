
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
    public static final String DEVICE_MACHINE_NAME = "machinename";
            
    private String ip = "";
    private String mac = "";
    private byte[] bMac = new byte[6];
    private boolean isGateway = false;
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

    public boolean isIsGateway() {
        return isGateway;
    }

    public void setIsGateway(boolean isGateway) {
        this.isGateway = isGateway;
    }  
    
    @Override
    public String toString()
    {
        String s = "ip:"+getIp()+" mac:"+getMac()+"\r\n";
        s += "is gateway: "+(isIsGateway()?"yes":"no")+"\r\n";
        for(Map.Entry<String, String> i: info.entrySet()) {
            s += i.getKey()+": "+i.getValue()+"\r\n";
        }
        return s;
    }
    
}
