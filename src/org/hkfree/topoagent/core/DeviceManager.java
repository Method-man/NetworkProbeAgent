/**
 * This class works with found devices from all modules
 */
package org.hkfree.topoagent.core;

import org.hkfree.topoagent.domain.Device;
import java.util.HashMap;
import java.util.Map;
import org.jnetpcap.packet.format.FormatUtils;

/**
 *
 * @author Filip Valenta
 */
public class DeviceManager {

    private HashMap<String, Device> devices = new HashMap<>();

    public Device getDevice(byte[] bMac) {
        String sMac = FormatUtils.mac(bMac);
        Device d = devices.get(sMac);
        if (d == null) {
            d = new Device(sMac, bMac);
            devices.put(sMac, d);
            LogService.logInfo(this, devices);
        }

        return d;

    }

    /**
     * Returns number of found devices
     *
     * @return int
     */
    public int devicesCount() {
        return devices.size();
    }

    /**
     * Get gateway device
     *
     * @return
     */
    public Device getGateway() {
        for (Map.Entry<String, Device> d : devices.entrySet()) {
            if (d.getValue().isGateway()) {
                return d.getValue();
            }
        }
        return null;
    }

    /**
     * Find Device by this MAC first 5 bytes
     *
     * @param mac
     * @return
     */
    public Device getByFirst5BytesOfMAC(String mac) {
        String first5Bytes = mac.substring(0, 14).toUpperCase();
        for (Map.Entry<String, Device> d : devices.entrySet()) {
            String first5BytesOfLowestMAC = d.getValue().getMacLowest(false).substring(0, 14).toUpperCase();
            if (first5Bytes.equals(first5BytesOfLowestMAC)) {
                return d.getValue();
            }
        }
        return null;
    }

    public HashMap<String, Device> getAllDevices() {
        return devices;
    }
    
    /**
     * Set SSID to AP
     * Client connected to AP has SSID and BSSID, so it can bet set also to the AP/router
     * 
     * @param apBSSID
     * @param apSSID 
     */
    public void setSSIDtoAP(String apBSSID, String apSSID) {
        Device AP = getByFirst5BytesOfMAC(apBSSID);
        if(AP != null) {
            getDevice(AP.getMacAsByte()).setInfo(Device.DEVICE_SSID, apSSID);
        }
    }

}
