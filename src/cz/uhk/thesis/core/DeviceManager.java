
/**
 * This class works with found devices from all modules
 */

package cz.uhk.thesis.core;

import cz.uhk.thesis.model.Device;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Filip Valenta
 */
public class DeviceManager {
    
    // TODO: rozpoznat, ze 2 zarizeni se stejnou IP maji rozdilny MAC
    // TODO: rozpoznat 2 cesty do stejneho bodu
    
    HashMap<String, Device> devices = new HashMap<>();
    
    public Device getDevice(String mac)
    {
        Device d = devices.get(mac);
        if(d == null) {
            d = new Device(mac);
            devices.put(mac, d);
        }
        deviceListChanged();
        
        return d;
    }
    
    /**
     * Returns number of found devices
     * 
     * @return int
     */
    public int DevicesCount()
    {
        return devices.size();
    }
    
    /**
     * Trigger the Logger of devices if device list is not empty
     */
    public void deviceListChanged()
    {
        if(DevicesCount() > 0) {
            List<Device> d = new ArrayList<>(devices.values());
            Logger.Log2File(d);
        }
    }
    
}
