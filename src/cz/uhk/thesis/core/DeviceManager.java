
/**
 * This class works with found devices from all modules
 */

package cz.uhk.thesis.core;

import cz.uhk.thesis.model.Device;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jnetpcap.packet.format.FormatUtils;

/**
 *
 * @author Filip Valenta
 */
public class DeviceManager {
    
    // TODO: rozpoznat, ze 2 zarizeni se stejnou IP maji rozdilny MAC
    // TODO: rozpoznat 2 cesty do stejneho bodu
    
    HashMap<String, Device> devices = new HashMap<>();
    
    public Device getDevice(byte[] bMac)
    {
        String sMac = FormatUtils.mac(bMac);
        Device d = devices.get(sMac);
        if(d == null) {
            d = new Device(sMac, bMac);
            devices.put(sMac, d);
            LogInfo();
        }
        
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
     * Get gateway device
     * 
     * @return 
     */
    public Device GetGateway()
    {
        for(Map.Entry<String, Device> d: devices.entrySet()) {
            if(d.getValue().isIsGateway()) {
                return d.getValue();
            }
        }
        return null;
    }

    /**
     * Trigger the Logger of devices if device list is not empty
     */
    public void LogInfo() {
        
        // TODO: presunout do Loggeru !
        
        Logger.Log2Console(this, "ukladam nove info");
        if(DevicesCount() > 0) {
            List<Device> d = new ArrayList<>(devices.values());
            Logger.Log2File(d);
        }
    }
    
}
